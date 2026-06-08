package com.myapp;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.net.URISyntaxException;


public class Executioner{
    
    private int port;
    private InetSocketAddress socket;
    private HttpServer master;
    private RouteController handler;



    /*
     * serve : METHOD
     * start the servive hosting at port(8080) over localhost.
     */
    public void serve(){
        try {
            port    = Integer.parseInt(App.env.get("port"));
            socket  = new InetSocketAddress(port);
            master  = HttpServer.create(socket, 0);
            handler = new RouteController();

            master.createContext("/", handler);
            master.setExecutor(null);
            master.start();
            
            System.out.println("o-exe--> service hosting at http://localhost:" + port);
        } 
        catch (java.net.BindException e) {
            System.err.println("o-exe--> PORT:"+port + " is already in use");
        } 

        catch (IOException e) {
            System.err.println("o-exe--> I/O error occured");
        }
    }



    /*
     * stop : METHOD
     * close the socket responsible for hosting.
     */
    public void stop(){
        System.out.println("o-exe--> stoping service hosting");
        master.stop(0);
    }





    /*
     * OpenBrowser : METHOD
     * for opening browser at given port.
     */

    public void OpenBrowser() {
        String url = "http://localhost:" + App.env.get("port");

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Desktop browsing is not supported on this platform.");
        }
    }

}