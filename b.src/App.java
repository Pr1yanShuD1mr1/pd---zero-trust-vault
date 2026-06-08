package com.myapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;






/*
 * minimal code for stating the code
 * 
 */

public class App{

    public static final Scanner Reader = new Scanner(System.in);
    public static final envReader env  = new envReader();

    private static String command;
    private static Executioner server;





    public static void main(String[] args){ 
        if (!shouldStart()) { return; }

        server = new Executioner();
        server.serve();
        server.OpenBrowser();

        try { new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); } 
        catch (Exception e) { e.printStackTrace(); }

        asciiArt();

        System.out.println("o-exe--> service hosting at http://localhost:" + App.env.get("port"));
        System.out.println("o-app--> press enter to stop");
        command = Reader.nextLine();
        server.stop();

    }



    

    private static boolean shouldStart() {
        String banner = """
================================================================================
                PROJECT244 : ZERO-TRUST FILE ENCRYPTION VAULT                       
================================================================================
 Project244 is a security architecture and data protection system,
 designed to prevent unauthorized access and ransomware attacks on sensitive files.
        
                 -+++++-                
                +=-::::-*               
              -+-::::  -:+*:            
            *=:::::::  @@#**@.          
          :#-:--:::: #@@@#**%-          
         .#:--=-::: =@@@@#**+%=         
        *#===++:::: *@@@@@%**%-         
        **==*+=::  @@@@@@@@#*+#%        
       %*+==*-:: =#@@@@@@@@@*+#%        
       %#****-:: #@@@@@@@@@@*+#%        
        #%%#*++-:*#@@@@@@@@#*+#%        
         .@@#***=:-#@@@@@@%*+%@#        
          :%%%%##==-*@@@%*+*@@#*%-      
        :+#+***####--=+#+*####****+-    
       ##+======%##%+==-%#*=******++#   
     .%*====---+-::=#%%%#=-:-+*****+#.  
    %%+===-:--:::::::::+%#=.-=+*****+%  
    %%+=====::::::::::-....---+*****+%  
    %%*+==+-:::::::::::==%#.:-=+****%@  
      @@#*+::::::::::--.   -:-==**%@%   
      @@@@#+=-:::::::::==%%=-=*#@@@@%   
      @@%#*###*====**####@@%%@%#*%#+%   
      %**+=============#@#***%%**%#+%   
      %*==--:::::::::::=%*+==++=**++%   
      %*==-::::::::::::-%+=-====**++%   
      %*==-::::::::::::-%+=---==**++%   
      %*==-::::::::::::-%*+=--==**++%   
      %*==-::::::::::::-%+======**++%   
      %*==-::::::::::::-%+======**++%   
      %*==-::::::::::::-%+=---==++++%   
      %*==-::::::::::::-%+=----==+++%   
      %*==-::::::::::::-%*+----==+++%   
      %*==-::::::::::::=%@%---===+++%   
      ##+=-::::::::::::# +%======#*+%   
       :#*-::::::::::::# +%===-==**+%   
         :#-:::::::-:::# +%=*+===##+%   
           #+--::::--::# +%=*++*+*#*    
              .%%--#+::# +%**#%#        
                 =##+::# +@##           
                     +*# +%.            
                       #                

 Core Principles:
  • Zero-Trust Architecture: No user, device, or network is inherently trusted.
  • Robust Cryptography: Highly secure, isolated repository for your data.
  • Continuous Validation: Every request is continuously authenticated & authorized.
================================================================================
""";
        System.out.println(banner);
        System.out.print("Should we start? (y/n): ");
        String input = Reader.nextLine();

        return input != null && input.trim().equalsIgnoreCase("y");
    }





    private static void asciiArt(){
        String artFile = "./c.res/ascii-art---by-vagonparovoz.txt";
        try {
            FileReader fileReader = new FileReader(artFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
