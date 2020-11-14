/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vasallo_Noureddine_5;

import javax.swing.JFrame;

/**
 *
 * @author Jonathan Vasallo
 */
public class VasalloJ_5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Server s = new Server(5000);
        s.start();
        
        Client c1 = new Client("127.0.0.1",5000,"Alice");
        c1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c1.setSize(600, 400);
        c1.setVisible(true);
        Thread t1 = new Thread(c1);
        t1.start();
        
        Client c2 = new Client("127.0.0.1",5000,"Bob");
        c2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c2.setSize(600, 400);
        c2.setLocation(700, 0);
        c2.setVisible(true);
        Thread t2 = new Thread(c2);
        t2.start();
        
        Client c3 = new Client("127.0.0.1",5000,"Charlie");
        c3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c3.setSize(600, 400);
        c3.setLocation(450, 500);
        c3.setVisible(true);
        Thread t3 = new Thread(c3);
        t3.start();
       
    }
    
}
