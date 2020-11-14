/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vasallo_Noureddine_5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Jonathan Vasallo
 */
public class Client extends JFrame implements Runnable {

    private JButton img;
    private JTextField chatField;
    private Socket connectionToServer = null;
    private JPanel TimeLineWindow;
    private JPanel SideWindow, BottomPanel;
    private JTextArea infoChat;
    private JButton addLink, filterUser, filterHash, filterSubString, clearButton,quit;
    private JTextArea InfoLabel;
    private TreeMap<String, ArrayList<Image>> connectorImage;
    private ArrayList<Image> imageArray;
    private String[] names = {"Alice", "Bob", "Charlie"};
    private ArrayList<String> nameTrackArray;
    private ArrayList<Image> temp = new ArrayList();
    private Integer numImageTotalImported;
    private ArrayList<String> AllWords = new ArrayList();

    private String address;
    private int port;
    private String username;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private ObjectInputStream objectInput = null;
    

    public Client(String a, int p, String un) {
        
        super(" User : " + un);

        this.setLayout(new BorderLayout()); // Debatable if this Will Stay this way 
        chatField = new JTextField();
        connectorImage = new TreeMap();
        SideWindow = new JPanel();
        quit = new JButton("Disconnect");
        clearButton = new JButton("Clear Filter/View All");
        numImageTotalImported = 0;

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TimeLineWindow.removeAll();
                
                for(int i = 0 ; i< AllWords.size(); i++ ){ // for everything in the list 
                    if (AllWords.get(i).endsWith("Uploaded An Image Below.")) {
                                        
                                        //System.out.println(AllWords.get(i) + " position " + i + " img link " + AllWords.get(i + 1));
                                        ImageIcon iconLogo = new ImageIcon(AllWords.get(i + 1));
                                        Image image = iconLogo.getImage(); // transform it 
                                        Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); // Smooth Scale
                                        iconLogo = new ImageIcon(newimg);
                                        JLabel newLabel = new JLabel();
                                        JLabel newLabel2 = new JLabel();
                                        newLabel2.setText(AllWords.get(i));
                                        newLabel.setIcon(iconLogo);
                                        TimeLineWindow.add(newLabel2);
                                        TimeLineWindow.add(newLabel); // should add the label to the panel 
                                        TimeLineWindow.repaint();
                                        TimeLineWindow.validate();
                                        
                                    } else if (AllWords.get(i).endsWith("Posted the Link Below.")) { // Its a Link 
                                       
                                        JLabel newLabel = new JLabel();
                                        JLabel newLabel2 = new JLabel();
                                        newLabel2.setText(AllWords.get(i));
                                        newLabel.setText(AllWords.get(i + 1));
                                        newLabel.setForeground(Color.BLUE.darker()); 
                                        
                                        // Sets the String to Blue / Makes It Clickable
                                        newLabel.addMouseListener(new MouseAdapter() {

                                            @Override
                                            public void mouseClicked(MouseEvent e) {

                                                //System.out.println(newLabel.getText());
                                                try {

                                                    Desktop.getDesktop().browse(new URI(
                                                            newLabel.getText()));

                                                } catch (IOException | URISyntaxException e1) {
                                                    e1.printStackTrace();
                                                }

                                                // the user clicks on the label
                                            }

                                            @Override
                                            public void mouseEntered(MouseEvent e) {
                                                // the mouse has entered the label
                                            }

                                            @Override
                                            public void mouseExited(MouseEvent e) {
                                                // the mouse has exited the label
                                            }
                                        });
                                        TimeLineWindow.add(newLabel2);
                                        TimeLineWindow.add(newLabel); // Should add the Label to the Panel 
                                        TimeLineWindow.repaint();
                                        TimeLineWindow.validate();
                                    } else {
                                        if(AllWords.get(i).startsWith("https:")==false && AllWords.get(i).startsWith("C:")==false){
                                            
                                        
                                        JLabel newLabel = new JLabel();
                                        newLabel.setText(AllWords.get(i));

                                        TimeLineWindow.add(newLabel); // Should add the Label to the Panel 
                                        TimeLineWindow.repaint();
                                        TimeLineWindow.validate();
                                        }

                                    }
                    
                }
                
                TimeLineWindow.repaint();
                TimeLineWindow.revalidate();
                
            // Post it as a String the Label it is Inside of to have a MouseListener 

            }

        });

        imageArray = new ArrayList();
        nameTrackArray = new ArrayList();

        BottomPanel = new JPanel();
        filterUser = new JButton("Filter by Username");
        filterUser.addActionListener(new ActionListener() {
            
            
            public void actionPerformed(ActionEvent evt) {
                
                String input = JOptionPane.showInputDialog("Enter the Username(s) You Want to Search for. *CASE SENSITIVE* ");
                //System.out.println(input);
                // This should find ALL tweets by "Charlie" | "Bob" | "Alice"
                try{
                    
                
                if (input.isBlank() == false ) {
                    
                    TimeLineWindow.removeAll();

                    String[] splited = input.split(" ");
                    //System.out.println(splited.length + "Length of Split");
                    
                    for (int i = 0; i < AllWords.size(); i++) { // For Every Single Text / img link 
                        
                        if (AllWords.get(i).startsWith("C:") == false) {   // not image
                            
                            for (int j = 0; j < splited.length; j++) {

                                if (AllWords.get(i).startsWith(splited[j])) { 
                                    
                            // If the Whole Statement has "Charlie" in it then Search for Charlie
                                    // Reset the Labels 

                                    if (AllWords.get(i).endsWith("Uploaded An Image Below.")) {
                                        
                                       // System.out.println(AllWords.get(i) + " position " + i + " img link " + AllWords.get(i + 1));
                                        ImageIcon iconLogo = new ImageIcon(AllWords.get(i + 1));
                                        Image image = iconLogo.getImage(); // transform it 
                                        Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); // Smooth Scale
                                        iconLogo = new ImageIcon(newimg);
                                        JLabel newLabel = new JLabel();
                                        JLabel newLabel2 = new JLabel();
                                        newLabel2.setText(AllWords.get(i));
                                        newLabel.setIcon(iconLogo);
                                        TimeLineWindow.add(newLabel2);
                                        TimeLineWindow.add(newLabel); // should add the label to the panel 
                                        TimeLineWindow.repaint();
                                        TimeLineWindow.validate();
                                        
                                    } else if (AllWords.get(i).endsWith("Posted the Link Below.")) { // Its a Link 
                                       
                                        JLabel newLabel = new JLabel();
                                        JLabel newLabel2 = new JLabel();
                                        newLabel2.setText(AllWords.get(i));
                                        newLabel.setText(AllWords.get(i + 1));
                                        newLabel.setForeground(Color.BLUE.darker()); 
                                        
                                        // Sets the String to Blue / Makes It Clickable
                                        newLabel.addMouseListener(new MouseAdapter() {

                                            @Override
                                            public void mouseClicked(MouseEvent e) {

                                                //System.out.println(newLabel.getText());
                                                try {

                                                    Desktop.getDesktop().browse(new URI(
                                                            newLabel.getText()));

                                                } catch (IOException | URISyntaxException e1) {
                                                    e1.printStackTrace();
                                                }

                                                // the user clicks on the label
                                            }

                                            @Override
                                            public void mouseEntered(MouseEvent e) {
                                                // the mouse has entered the label
                                            }

                                            @Override
                                            public void mouseExited(MouseEvent e) {
                                                // the mouse has exited the label
                                            }
                                        });
                                        TimeLineWindow.add(newLabel2);
                                        TimeLineWindow.add(newLabel); // Should add the Label to the Panel 
                                        TimeLineWindow.repaint();
                                        TimeLineWindow.validate();
                                    } else {
                                        JLabel newLabel = new JLabel();
                                        newLabel.setText(AllWords.get(i));

                                        TimeLineWindow.add(newLabel); // Should add the Label to the Panel 
                                        TimeLineWindow.repaint();
                                        TimeLineWindow.revalidate();

                                    } 
                                    
                                } // end if
                                
                            } // end for
                            
                        } // end if

                    } // end of all words | end for
                
                } // end if
                 TimeLineWindow.revalidate();
                }
                catch(NullPointerException e){
                    System.out.println("Exception Caught");
                }
                System.out.println(AllWords.size());

                
            } // end actionPerformed

        });

        filterHash = new JButton("Filter by HashTag");
        filterHash.addActionListener(new ActionListener() {
            
            
            public void actionPerformed(ActionEvent evt) {
                
                String hash = JOptionPane.showInputDialog("Type in the HashTag You Want to Search For Below");
                try{
                    
                
                if (hash.startsWith("#")) { // do it
                    
                    TimeLineWindow.removeAll();

                    for (int i = 0; i < AllWords.size(); i++) {
                        
                        if (AllWords.get(i).contains(hash)) { // Contains Hashtag then Display it 
                            
                            
                                JLabel newLabel = new JLabel();
                                newLabel.setText(AllWords.get(i));

                                TimeLineWindow.add(newLabel); // Should add the Label to the Panel 
                                TimeLineWindow.repaint();
                                TimeLineWindow.validate();

                            
                        } // end if

                    } // end for
                    
                } // end if
                else if(hash.startsWith("#")==false || hash.isBlank()){
                JOptionPane.showMessageDialog(null, "Please type the Hashtag symbol.");
                }
                 TimeLineWindow.revalidate();
                }catch(NullPointerException e){
                    
                }

            } // end actionPerformed

        });

        filterSubString = new JButton("Filter by SubString");
        filterSubString.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String subStr = JOptionPane.showInputDialog("Type the Substring Below ");
                try{
                    
                
                
                if (subStr.isBlank() == false) { // do it 
                    TimeLineWindow.removeAll();
                     TimeLineWindow.validate();
                    for (int i = 0; i < AllWords.size(); i++) {
                        
                        if (AllWords.get(i).contains(subStr)) { 

                        // Contains Substring then Display it 
                            if (AllWords.get(i).endsWith("Uploaded An Image Below.")) {
                                
                                ImageIcon iconLogo = new ImageIcon(AllWords.get(i + 1));
                                Image image = iconLogo.getImage(); // transform it 
                                Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                                iconLogo = new ImageIcon(newimg);
                                JLabel newLabel = new JLabel();
                                JLabel newLabel2 = new JLabel();
                                newLabel2.setText(AllWords.get(i));
                                newLabel.setIcon(iconLogo);
                                TimeLineWindow.add(newLabel2);
                                TimeLineWindow.add(newLabel); // should add the label to the panel 
                                TimeLineWindow.repaint();
                                TimeLineWindow.validate();
                                
                            } else if (AllWords.get(i).endsWith("Posted the Link Below.")) { // its a link 
                                
                                JLabel newLabel = new JLabel();
                                JLabel newLabel2 = new JLabel();
                                newLabel2.setText(AllWords.get(i));
                                newLabel.setText(AllWords.get(i + 1));
                                newLabel.setForeground(Color.BLUE.darker());
                                newLabel.addMouseListener(new MouseAdapter() {
                                
                                @Override
                            public void mouseClicked(MouseEvent e) {

                               // System.out.println(newLabel.getText());
                                
                                try {

                                    Desktop.getDesktop().browse(new URI(
                                 newLabel.getText()));

                                     } catch (IOException | URISyntaxException e1) {
                                    e1.printStackTrace();
                                
                                }

                                // the user clicks on the label
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                // the mouse has entered the label
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                // the mouse has exited the label
                            }
                        });  
                                
                                TimeLineWindow.add(newLabel2);
                                TimeLineWindow.add(newLabel); // should add the label to the panel 
                                TimeLineWindow.repaint();
                                TimeLineWindow.validate();
                                
                                
                            } else if (AllWords.get(i).startsWith("https:")) {
                                
                                JLabel newLabel = new JLabel();
                                JLabel newLabel2 = new JLabel();
                                newLabel.setText(AllWords.get(i));
                                newLabel2.setText(AllWords.get(i - 1));
                                newLabel.setForeground(Color.BLUE.darker());
                                newLabel.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent e) {

                                //System.out.println(newLabel.getText());
                                
                                try {

                                    Desktop.getDesktop().browse(new URI(
                                 newLabel.getText()));

                                     } catch (IOException | URISyntaxException e1) {
                                    e1.printStackTrace();
                                
                                }

                                // the user clicks on the label
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                // the mouse has entered the label
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                // the mouse has exited the label
                            }
                        });     
                                TimeLineWindow.add(newLabel2);
                                TimeLineWindow.add(newLabel);
                                
                                // should add the label to the panel 
                                TimeLineWindow.repaint();
                                TimeLineWindow.validate();

                            } else {
                                JLabel newLabel = new JLabel();
                                newLabel.setText(AllWords.get(i));

                                TimeLineWindow.add(newLabel); // should add the label to the panel 
                                TimeLineWindow.repaint();
                                TimeLineWindow.validate();

                            } // end else
                            
                        } // end if

                    } // end for
                    TimeLineWindow.repaint(); 
                 TimeLineWindow.revalidate(); 
                } // end if
                }catch(NullPointerException e) {
                    
                }      
            } // end actionPerformed

        });

        BottomPanel.setLayout(new GridLayout(2, 1));

        InfoLabel = new JTextArea("To Post on the Timeline, Press Enter After Typing in the Chat Box Below");
        InfoLabel.setFont(new Font("Verdana", Font.PLAIN, 15));
        InfoLabel.setEditable(false);
        SideWindow.setLayout(new GridLayout(7, 1));

        infoChat = new JTextArea(" Welcome To Fake Twitter");
        infoChat.setFont(new Font("Verdana", Font.PLAIN, 15));
        infoChat.setEditable(false);

        addLink = new JButton("Add Link");
        addLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String link = JOptionPane.showInputDialog("Type or Paste in the Link/URL Below ");
                //System.out.println(link);
                try{
                    
                
                if(link.startsWith("https:")){
                
                out.println(username + " Posted the Link Below.");
                out.println(link);
                //

                out.flush();
                }else if(link.startsWith("https:")==false){
                    JOptionPane.showMessageDialog(null, "Please Enter A Valid Link.");
                }
                }catch(NullPointerException e){
                    
                }
                
            
                // Post it as a String the Label it is inside of to have a MouseListener 

            }

        }); // end of add link button 

        img = new JButton(" Add Image");
        img.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //System.out.println("Button Was Clicked");
                
                try {
                    
                    JFileChooser fc = new JFileChooser();
                    int value = fc.showOpenDialog(fc);
                    
                    if (value == 0) {
                        File file = fc.getSelectedFile(); // finish the scan
                        //System.out.println(file.getName() + "File Name ");
                        //File filetemp = file.getAbsoluteFile();
                        
                        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".PNG")) { 
                            out.println(username + ": Uploaded An Image Below.");

                            
                            
                            // If we are Here its an Image 
                            out.println(file.getAbsoluteFile());
                            

                            out.flush();

                        }
                        else{
                        JOptionPane.showMessageDialog(null, "This platform can only take .jpg/.png files.");
                        }

                    }
                } catch (NullPointerException e) {

                }
                
                
            }

        }); // end of button 
        
        quit.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                
                System.out.println("All Users Have Disconnected");
                System.exit(0);
                
            }

        });
        
        
        // All The Buttons added to the Side of Window
        SideWindow.add(img);
        SideWindow.add(addLink);
        SideWindow.add(filterUser);
        SideWindow.add(filterHash);
        SideWindow.add(filterSubString);
        SideWindow.add(clearButton);
        SideWindow.add(quit);
        
        // Text Field and Info Label added to the Bottom of Window
        BottomPanel.add(InfoLabel);
        BottomPanel.add(chatField);
        
        this.add(infoChat, BorderLayout.PAGE_START);
        this.add(SideWindow, BorderLayout.EAST);
        this.add(BottomPanel, BorderLayout.PAGE_END);

        chatField.addKeyListener(new KeyHandler());

        TimeLineWindow = new JPanel();

        TimeLineWindow.setLayout(new BoxLayout(TimeLineWindow, BoxLayout.PAGE_AXIS));

        //TimeLineWindow.add(but);
        this.add(new JScrollPane(TimeLineWindow), BorderLayout.CENTER);
        // In Between here lies the material the user will see 
        // I think a Text area needs to go here because we can put links text and images (draw) 
        this.address = a;
        this.port = p;
        this.username = un;
        
        if (numImageTotalImported == 0) { // this is 0 
            
            connectorImage.put(username, temp);
            
        } // end if
    } // end Client

    @Override
    public void run() {
        // do something 
        try {
            
            connectionToServer = new Socket(address, port);
            System.out.println(this.username + " Connected");

            in = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
            out = new PrintWriter(connectionToServer.getOutputStream());

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            
        }

        while (true) {
            try {
                
                String word = in.readLine();
               // System.out.println(word + " word!"); // Debug
                
                // Where text was Edited try catch in.readline();
                
                if (word.startsWith("C:") == false) { // reg text!
                    
                    if (word.startsWith("https:") == false) {
                        
                        AllWords.add(word);

                        JLabel newLabel = new JLabel();
                        newLabel.setText(word);

                        TimeLineWindow.add(newLabel); // Should add the Label the the Panel
                        TimeLineWindow.validate();
                        
                    } // end if
                    
                    if (word.startsWith("https:")) {
                        
                        AllWords.add(word);
                        JLabel newLabel = new JLabel();
                        newLabel.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent e) {

                               // System.out.println(newLabel.getText());
                                try {

                                    Desktop.getDesktop().browse(new URI(
                                            newLabel.getText()));

                                } catch (IOException | URISyntaxException e1) {
                                    e1.printStackTrace();
                                }

                                // the user clicks on the label
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                // the mouse has entered the label
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                // the mouse has exited the label
                            }
                        });

                        newLabel.setText(word);
                        newLabel.setForeground(Color.BLUE.darker());
                        TimeLineWindow.add(newLabel); // should add the label to the panel 
                        TimeLineWindow.validate();
                        
                    } // end if

                } /* end if */ else { // if it does match C:
                    //System.out.println("Matches with Condition");
                    
                    if (word.endsWith(".jpg") == false) { // doesnt end with .jpg
                        
                        if (word.endsWith(".png") == false && word.endsWith(".PNG") == false) { // reg text!
                            
                            JLabel newLabel = new JLabel();
                            AllWords.add(word);
                            newLabel.setText(word);
                            TimeLineWindow.add(newLabel); // should add the label to the panel 
                            TimeLineWindow.validate();
                            
                        } /* end if */ else if (word.endsWith("png") || word.endsWith("PNG")) { // matches a pic png
                            
                           // System.out.println("This is a picture of png format");

                            AllWords.add(word);

                            ImageIcon iconLogo = new ImageIcon(word);
                            Image image = iconLogo.getImage(); // transform it 
                            Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                            iconLogo = new ImageIcon(newimg);  // transform it back = new ImageIcon(newimg);  // transform it back
                            JLabel newLabel = new JLabel();

                            newLabel.setIcon(iconLogo);

                            TimeLineWindow.add(newLabel); // should add the label to the panel 
                            TimeLineWindow.validate();

                            if (numImageTotalImported != 0) {

                                if (username.equals(nameTrackArray.get(numImageTotalImported - 1))) { // this means that the picture and charlie are connected and only save with the correct 
                                    // publishers name 
                                    //System.out.println(username + " Test to see if it will be CHARLIE");   #DEBUG  

                                    connectorImage.get(username).add(newimg); // adds the current image to the global image ArrayList 

                                   // System.out.println(connectorImage.size() + " checking for dupes ");

                                   // System.out.println(connectorImage.get(username) + " what is inside of ArrayList<IMAGE> ");
                                    

                                } // end if

                            } // end if

                            //only save the image if this clients username matches the text above this images output 
                        
                        } // end else if

                    } /* end if */ else if (word.endsWith(".jpg")) { // jpg pic 
                        
                        AllWords.add(word);
                       // System.out.println("This is a Picture of jpg Format");
                        ImageIcon iconLogo = new ImageIcon(word);
                        Image image = iconLogo.getImage(); // transform it 
                        Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                        iconLogo = new ImageIcon(newimg);
                        JLabel newLabel = new JLabel();
                        newLabel.setIcon(iconLogo);
                        TimeLineWindow.add(newLabel); // should add the label to the panel 
                        TimeLineWindow.validate();
                        
                    } // end else if

                } // end else

            } /* end try */ catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                
            } // end catch
            
            this.validate();
            
        } // end while
        
    } // end run 

    
    // Detects if the VK_Enter Key is Pressed
    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                
              //  System.out.println("WE HIT THE VK ENTER BUTTONNN");

                out.println(username + ":  " + chatField.getText());
                //System.out.println(username + ":  " + chatField.getText());
                out.flush();
                chatField.setText("");
                
            }
            
        }
        
    }
    
}
