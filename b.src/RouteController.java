package com.myapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class RouteController implements HttpHandler {

    /* 
     * handle : METHOD
     * it used is used for hadling api request
    */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String r = exchange.getRequestURI().getPath();

        if (r.startsWith("/image/")) {
            fetchImage(exchange);
            return;
        }

        switch (r) {
            case "/"        -> home(exchange);
            case "/putFile" -> putFile(exchange);
            case "/getFile" -> getFile(exchange);
            default         -> dump(exchange);
        }
    }





    /* 
     * send....Response : METHOD
     * designed for sending response back, as per api request
    */

    private void sendBytesResponse(
        HttpExchange exchange, 
        byte[] response, 
        int statusCode
    ) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void sendTextResponse(
        HttpExchange exchange, 
        String response, 
        int statusCode
    ) throws IOException {
        byte[] bytes = response.getBytes();
        sendBytesResponse(exchange, bytes, statusCode); 
    }





    /* 
     * home : METHOD
     * define the routing for "/"
    */

    private void home(HttpExchange exchange) throws IOException {

        Path uiFile = Paths.get(App.env.get("projectDIR") + App.env.get("uiFile"));
        byte[] response;
        int statusCode;

        try {
            response = Files.readAllBytes(uiFile);
            statusCode = 200;
        } 
        catch (IOException e) {
            response = "File not found or unreadable".getBytes();
            statusCode = 404;
        }

        sendBytesResponse(exchange, response, statusCode);
    }
    




    /*
     * putFile : METHOD
     * responsible 
     * a. for receiving a file stream
     * b. invoking chunk encryption
     * c. storing distributed blocks
     * d. returning clean base-64 cryptographic key response
     */
    private void putFile(HttpExchange exchange) throws IOException {
        // 1. Inject Universal CORS Headers IMMEDIATELY to prevent frontend network crashes
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // 2. Intercept and approve browser CORS Preflight 'OPTIONS' requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // 3. Validate actual Request Method
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendTextResponse(exchange, "Method Not Allowed", 405);
            return;
        }

        // 4. Extract Original Filename from Header (Sent by Frontend)
        String originalFilename = exchange.getRequestHeaders().getFirst("X-Original-Filename");
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "vault_file.txt";
        }

        String keyStr;
        String name = originalFilename.contains(".") ? originalFilename.substring(0, originalFilename.lastIndexOf('.')) : originalFilename;
        String ext = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1) : "txt";
        
        try (InputStream is = exchange.getRequestBody()) {
            keyStr = VaultEngine.encryption(is, name, ext);
        } 
        catch (Exception e) {
            System.err.println("o-rte-> Encryption failed: " + e.getMessage());
            sendTextResponse(exchange, "Internal Vault Encryption Error", 500);
            return;
        }   System.out.println("o-rte-> Encryption suceed and stored \n");

        // returning key as file.
        byte[] responseBytes = keyStr.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        sendBytesResponse(exchange, responseBytes, 200);

    }

    

    
    
    /*
     * getFile : METHOD
     * responsible 
     * a. for receiving authentication token key
     * b. finding structural layout metadata logs
     * c. re-assembling decrypted blocks
     * d. returning fully reconstituted file back to client context
     */
    private void getFile(HttpExchange exchange) throws IOException {
        
        // getting the key from the request body
        String keyStr;
        try (InputStream is = exchange.getRequestBody()) {
            keyStr = new String(is.readAllBytes()).trim().replace(" ", "+");
        }

        String transaction = findTransactionLine(keyStr); 

        byte[] decryptedBytes;
        try { // perform decryption using VaultEngine
            decryptedBytes = VaultEngine.decryption(keyStr);
        } catch (Exception e) {
            sendTextResponse(exchange, "Internal Server Decryption Error", 500);
            return;
        }

        if (decryptedBytes == null) {
            sendTextResponse(exchange, "Unauthorized", 403);
            return;
        }

        // 4. DYNAMIC FILENAME EXTRACTION: 
        // Parse the transaction line (format: key,name,extension,references)
        String filename = "recovered_file.txt"; // Default fallback
        if (transaction != null) {
            String[] parts = transaction.split(",");
            if (parts.length >= 3) {
                filename = parts[1] + "." + parts[2];
            }
        }

        // 5. Set the header using the extracted filename
        exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        sendBytesResponse(exchange, decryptedBytes, 200);
    }




    
    // Ensure this helper exists in RouteController.java
    private String findTransactionLine(String key) throws IOException {
        File recordsFile = new File("f.records/records.txt");
        if (!recordsFile.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(recordsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(key + ",")) {
                    return line;
                }
            }
        }
        return null;
    }





    /*
     * fetchImage : METHOD
     * responsible for fetching and returing image,
     * as per request at live.
    */

    private void fetchImage(HttpExchange exchange) throws IOException {
        String p = exchange.getRequestURI().getPath();
        Path img = Paths.get(App.env.get("imgDIR"), p.replace("/image/", ""));

        String contentType = Files.probeContentType(img);

        switch (img.getFileName().toString().toLowerCase()) {
            case ".gif"     -> contentType = "image/gif";
            default         -> contentType = "application/octet-stream";
        }
        exchange.getResponseHeaders().set("Content-Type", contentType );
    
        try {
            byte[] responseBytes = Files.readAllBytes(img);
            sendBytesResponse(exchange, responseBytes, 200);
        } catch (IOException e) {
            System.err.println("o-rte-> Failed to find image asset: " + img.toAbsolutePath());
            sendTextResponse(exchange, "Image asset not found", 404);
        }
    }





    /* 
     * dump : METHOD
     * define the response for calling invalid api
    */

    private void dump(HttpExchange exchange) throws IOException {
        sendTextResponse(exchange, "dumping site...", 404);
    }
}