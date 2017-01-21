    package image.viewer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
/**
 * Takes a user chosen file and reads it as an Image type then displays the
 * picture in the window giving the user the option to go back to the previous 
 * picture and moving forward back to the most recent picture that was opened.
 * @author Kyle Hewitt
 */
public class ImageViewer extends JPanel {
    private final JFrame frame;// Frame
    private final JPanel p1;//Panel
    private final JButton next, previous;//forward and back function buttons
    private final JMenuBar bar;//menu bar
    private final JMenu chooseFile;//menu section
    private final JMenuItem open, close;//menu item
    private final JFileChooser fileChooser;//file chooser
    private File currentFile;//The File the user selected
    private final MyStack forward = new MyStack();//the stack that holds forward pictures
    private final MyStack backward = new MyStack();//the stack that holds previous pictures
    private Image picture, currentPic;//temp holders for opening and push/pop between stacks
    private final Container cp; //to show the picture in the window
    private boolean insert= false;//used when inserting a picture when not at the end
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        ImageViewer obj = new ImageViewer();
    }//main
    /**
     * Constructor builds the GUI
     */
    public ImageViewer(){
        
        frame = new JFrame("Image Viewer");
        frame.setBounds(250, 250, 650, 650);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        p1 = new JPanel();
        frame.add(p1, BorderLayout.SOUTH);
        next = new JButton(">");
        previous = new JButton("<");
        p1.add(previous);
        p1.add(next);
        
        fileChooser = new JFileChooser();
        bar = new JMenuBar();
        frame.add(bar, BorderLayout.NORTH);
        chooseFile = new JMenu("File");
        bar.add(chooseFile);
        open = new JMenuItem("Open");
        chooseFile.add(open);
        close = new JMenuItem("Exit");
        chooseFile.add(close);
        cp = frame.getContentPane();
	cp.add(this,BorderLayout.CENTER);
        
        frame.setVisible(true);
        buttonFunctions();
    }//constructor
    /**
     * A continuation of the constructor; sets the function of all user operated
     * buttons and selections in the window.
     * For annoy inner classes I used lambda expressions suggested by the IDE
     * and specified at:
     * https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
     */    
    public void buttonFunctions(){
        open.addActionListener((ActionEvent e) -> {
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION){
                try{
                    currentFile = fileChooser.getSelectedFile();
                    currentPic = ImageIO.read(currentFile);
                    currentPic.getGraphics();//triggers to check if the file is a pic
                    if(!forward.isEmpty()){//sets the condition if the user 
                                          //inserts a picture when not at the end
                        backward.push(picture);
                        setAtEnd();
                    }
                    else{
                        backward.push(currentPic);
                    }
                    picture = currentPic;   
                    repaint();
                }catch(IOException | NullPointerException ioe){
                    JOptionPane.showMessageDialog(null, "Try Opening a Picture...");
                    cp.repaint();
                    picture = backward.peek();
                }              
                repaint();
            }
        });
        
        close.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        previous.addActionListener((ActionEvent e) -> {
            if(!backward.isEmpty()){
                forward.push(picture);
                picture = backward.pop();
            }
            repaint();
        });
        
        next.addActionListener((ActionEvent e) -> {
            if(!forward.isEmpty()){
                backward.push(picture);
                picture = forward.pop();
            }
            repaint();
        });
    }//buttonFunctions
    /**
     * This method is invoked if the user selects the previous button then tries
     * to open another picture. When setAtEnd is called it moves anything that 
     * would be on the forward stack onto the backward stack as if the user scrolled
     * to the end so that the new picture is inserted at the end.
     */
    public void setAtEnd(){
        Image temp;
        while(!forward.isEmpty()){
           temp = forward.pop();
           backward.push(temp);
        }
        insert = true;

    }//setAtEnd
    /**
     * Used to display the picture in the window.
     * @param g 
     */
    public void paintComponent(Graphics g){
        if(picture != null){
            cp.repaint();
            if (!insert)
                g.drawImage(picture,60,30,500,500,null);
            if (insert){
                g.drawImage(currentPic,60,30,500,500,null);
                insert = false;
            }
        }
        
    }//paintComponent
 /**
  * MyStack is the inner class created for the stacks used inside the ImageViewer
  * class. It has the functions push, pop, isEmpty, peek, and size.
  */
    private class MyStack{
        private final ArrayList<Image> theStack;
        
        private MyStack(){
        theStack = new ArrayList();
        }
        
        private void push(Image obj){
            theStack.add(obj);
        }
        
        private Image pop(){
          Image popObj = theStack.get(theStack.size()-1);
          theStack.remove(theStack.size()-1);
          return popObj;
        }
        private boolean isEmpty(){
            return theStack.isEmpty();
        }
        private Image peek(){
            Image peeker = theStack.get(theStack.size()-1);
            return peeker;
        }
        private int size(){
            return theStack.size();
        }
    }//MyStack
}//ImageViewer
