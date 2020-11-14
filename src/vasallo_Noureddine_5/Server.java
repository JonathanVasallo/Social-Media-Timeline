/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vasallo_Noureddine_5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan | Jad
 */
public class Server extends Thread {
    
    private Socket connectionToChatWindows = null;
    private ServerSocket server = null;
    ArrayList<ChatThread> threads = new ArrayList();
    private TreeMap<String, String> textBox = new TreeMap(); // Might not Need 
    private ArrayList<String> allTextArray = new ArrayList();
    
    private int port;
    
    public Server(int _port){
        
        this.port = _port;
        
        try{
            
            server = new ServerSocket(this.port);
            
        } /* end try */ catch (IOException ex) {
            
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            
        } // end catch
    } // end of server sub clas
        
        
    @Override
    public void run(){
        
    while(true){
        
        try {
            
            connectionToChatWindows = server.accept();
            
            ChatThread newThread = new ChatThread(this.port);
            newThread.start();
            threads.add(newThread);
            
        } /* end try */ catch (IOException ex) {
            
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            
        } // end catch
        
    } // end while  
    
} // end run
    
    private void postToChatWindows(String line){
        
     for(int i = 0; i < threads.size(); i++){
         threads.get(i).post(line);
         
    } // end for
     
}// end postToChatWindow
    
    
  // all below is the chat Thread Class 
    public class ChatThread extends Thread {
        
        private BufferedReader in = null;
        private PrintWriter out = null;
        
        public ChatThread(int p) {
            
            try {
            
            in = new BufferedReader(new InputStreamReader(connectionToChatWindows.getInputStream()));   
            out = new PrintWriter(connectionToChatWindows.getOutputStream());
           
            } catch (IOException ex) {
                
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            
        } // end pub SC
        
        @Override
        public void run(){
            
         while(true) {
             
             String line = "";
             
             try {
                 
                 line = in.readLine();
                 if(line.startsWith("https:")){ // not a img -> is a text
                    
                }
                 // try to make a condition where it knows if its a FILE OR NOT 
                 
             } catch (IOException ex) {
                 
                 Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                 
             }
             
             postToChatWindows(line);
             
         }   
            
        }
        
        public void post(String line){
         out.println(line);
        
        out.flush();
        }
        @Override
        public void finalize(){
            
            System.out.println("closing connection");
            
            try {
                
                connectionToChatWindows.close();
                in.close();
                out.close();
                
            } catch (IOException ex) {
                
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                
            } finally {
                
            try {
                
                super.finalize();
                
            }   catch (Throwable ex) {
                
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            
            }
            
        }
        
    }
  
}
