/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cashdispenser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;
 
 
public class PinCode extends JPanel
                          implements ActionListener {
    private static String OK = "ok";
    private static String EXIT = "exit";
 
    private JFrame controllingFrame; //needed for dialogs
    private JPasswordField passwordField;
    private JFrame f = new JFrame();
    private Connection myConnection;
    private static Vector v;
 
    public PinCode() {
        //Use the default FlowLayout.
        controllingFrame = f;
 
        //Create everything.
        passwordField = new JPasswordField(10);
        passwordField.setActionCommand(OK);
        passwordField.addActionListener(this);
        
        v = new Vector<String>();
 
        JLabel label = new JLabel("Enter the pincode: ");
        label.setLabelFor(passwordField);
 
        JComponent buttonPane = createButtonPanel();
 
        //Lay out everything.
        JPanel textPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        textPane.add(label);
        textPane.add(passwordField);
 
        f.pack();
        f.setLocationRelativeTo(null);
        add(textPane);
        add(buttonPane);
        //readInValuesFromDatabase();
    }
 
    protected JComponent createButtonPanel() {
        JPanel p = new JPanel(new GridLayout(0,1));
        JButton okButton = new JButton("OK");
        JButton helpButton = new JButton("Exit");
 
        okButton.setActionCommand(OK);
        helpButton.setActionCommand(EXIT);
        okButton.addActionListener(this);
        helpButton.addActionListener(this);
 
        p.add(okButton);
        p.add(helpButton);
 
        return p;
    }
 
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (OK.equals(cmd)) { //Process the password.
            int input = Integer.parseInt(passwordField.getText());
            System.out.println("The input to actionPerformed was " + input);
            if (isPasswordCorrect(input)) {
                JOptionPane.showMessageDialog(controllingFrame,
                    "Success! You typed the right pincode.");
                
                //Password was correct, run the Cash Dispenser
                CashDispenser app;
                app = new CashDispenser("Cash Dispenser");
                app.setSize(600,450);
                app.setVisible(true);
                app.pack();
                app.setLocationRelativeTo(null);
            } else {
                JOptionPane.showMessageDialog(controllingFrame,
                    "Invalid password. Try again.",
                    "Error Message",
                    JOptionPane.ERROR_MESSAGE);
            }
 
            //Zero out the possible password, for security.
            //Arrays.fill(input, '0');
 
            passwordField.selectAll();
            resetFocus();
        } else { //The user has pressed exit.
            JOptionPane.showMessageDialog(controllingFrame,
                "Exit the application");
            System.exit(0);
        }
    }
 
    /**
     * Checks the passed-in array against the correct password.
     * After this method returns, you should invoke eraseArray
     * on the passed-in array.
     */
    private boolean isPasswordCorrect(int input) {
       boolean isCorrect = true;

       setupDatabaseConnection();
       String pass1 = "";
       Integer passToInt = 0;
       System.out.println("Value from input is " + input);
       try {

            //Statement stmt = myConnection.createStatement(); 
            //ResultSet res = stmt.executeQuery("SELECT * FROM AccountHolder"))
            //Connection con = myConnection.getConnection();
            PreparedStatement stmt;
            stmt = myConnection.prepareStatement("SELECT * FROM AccountHolder where pincode='" + input+"'");
            ResultSet res = stmt.executeQuery();
            res = stmt.executeQuery();
            while (res.next()) {
                pass1 = res.getString("pincode");
                passToInt = Integer.parseInt(pass1);
                System.out.println("Value from pass1 is " + pass1);
                System.out.println("Value from passToInt is " + passToInt);
            }
            if (input == passToInt) { 
                JOptionPane.showMessageDialog(this,"Correct pincode, carry on");
            }
            else {
                JOptionPane.showMessageDialog(this,"Incorrect pincode","Error",JOptionPane.ERROR_MESSAGE);
                isCorrect = false;
            }
        } catch (SQLException | HeadlessException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            isCorrect = false;
        }
       return isCorrect;
    }
 
    //Must be called from the event dispatch thread.
    protected void resetFocus() {
        passwordField.requestFocusInWindow();
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Enter Pincode");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        final PinCode newContentPane = new PinCode();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Make sure the focus goes to the right component
        //whenever the frame is initially given the focus.
        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                newContentPane.resetFocus();
            }
        });
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
          /**
     * Setting up the database connection
     */
  private void setupDatabaseConnection()
  {      
    try
    {
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      this.myConnection = DriverManager.getConnection("jdbc:sqlserver://194.47.129.139;user=dv1454_ht13_49;password=pLNpoCz4;database=dv1454_ht13_49");
      System.out.println("Successfully Connected to the database!");

    }    
    catch (ClassNotFoundException e) { 
         System.out.println("Could not find the database driver " + e.getMessage());
     } 
    catch (SQLException e) {
         System.out.println("Could not connect to the database " + e.getMessage());
     }
  
    
  }

  //
  // MAIN
  //
  public static void main(String[] args)
  {
      final PinCode pinCode;
      pinCode = new PinCode();
      //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        pinCode.createAndShowGUI();
            }    
        });
          
  }

}
