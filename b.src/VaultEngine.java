package com.myapp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

public class VaultEngine {





    // defining encryption wrapper for file.
    public static String encryption(InputStream fileStream, String filename, String extension) throws Exception {
        System.out.println("o-eng--> encryption process started");
        AES256 crypter = new AES256();
        byte[] k = crypter.generateKey();

        int n = 0;
        int bytesRead;
        byte[] buffer = new byte[1024*1024]; // 1 MB
        StringBuilder references = new StringBuilder();

        File filesDir = new File("./e.files");
        if (!filesDir.exists()) { filesDir.mkdirs(); }

        // Segment file into 1024-byte chunks, encrypt each chunk, and save individually
        System.out.println("o-eng--> segmenting file into chunks");
        while ((bytesRead = fileStream.read(buffer)) != -1) {
            
            // encrypting chunks
            byte[] chunkToEncrypt = new byte[bytesRead];
            System.arraycopy(buffer, 0, chunkToEncrypt, 0, bytesRead);
            byte[] encryptedBuffer = crypter.encrypt(chunkToEncrypt);

            long i;
            File chunkFile;
            Random r = new Random();
            
            // generating a unique no for chunk, between 10000000 to 99999999
            while (true) {
                i = 10000000L + (long)(r.nextDouble() * 90000000L); 
                chunkFile = new File(filesDir, String.valueOf(i));
                if (!chunkFile.exists()) { break; }
            }   references.append(i).append("|");
            System.out.println("o-eng--> chunk " + n + " : " + i);
            n++;

            // saving chunk
            try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
                fos.write(encryptedBuffer);
            }
        }

        String keyString = crypter.key2String(k);
        File recordsFile = new File("f.records/records.txt");
        
        // Append transaction logs to vault records mapping key to chunks
        try (FileWriter fw = new FileWriter(recordsFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(keyString + "," + filename + "," + extension + "," + references.toString());
            bw.newLine();
        }

        return keyString;
    }




    
    // defining decryption wrapper for file.
    public static byte[] decryption(String key) throws Exception {
        System.out.println("o-eng--> decrytion process started");
        File recordsFile = new File("f.records/records.txt");
        if (!recordsFile.exists()) { return null; }


        // find the existence of keyin records.
        String transaction = null;
        try (BufferedReader br = new BufferedReader(new FileReader(recordsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(key + ",")) {
                    transaction = line;
                    break;
                }
            }
        }

        if (transaction == null) {
            System.out.println("o-eng--> key absence");
            return null;
        }   System.out.println("o-eng--> key present");


        String[] parts = transaction.split(",", 4);
        if (parts.length < 4) { return null; }
        String referencesStr = parts[3];
        AES256 crypter = new AES256();
        byte[] rawKey = crypter.String2Key(key);
        crypter.setKey(rawKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String[] referenceIds = referencesStr.split("\\|");

        // reading, decrypting, stitching each segments to stream back original file
        for (String id : referenceIds) {
            if (id.trim().isEmpty()) continue;
            File chunkFile = new File("./e.files/" + id);
            System.out.print("o-eng--> chunk " + id);
            if (chunkFile.exists()) {
                System.out.println(" present");
                byte[] encryptedBuffer = Files.readAllBytes(chunkFile.toPath());
                byte[] decryptedBuffer = crypter.decrypt(encryptedBuffer);
                outputStream.write(decryptedBuffer);
            }else{
                System.out.println(" absent");
            }
        }

        return outputStream.toByteArray();
    }
}