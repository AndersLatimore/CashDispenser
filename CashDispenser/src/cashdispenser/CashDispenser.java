/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cashdispenser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;


/**
 *
 * @author Anders Akesson <andyweb.info>
 */

//
// Example on how to connect to a SQL Server using JDBC:ODBC
//

public class CashDispenser extends JFrame
{
  //
  // STATE
  //
  private JTextArea txtOut = new JTextArea(10, 30);
  private JScrollPane scroll = new JScrollPane();
  private JButton cmdClose = new JButton("Close");
  private JButton cmdLogOut = new JButton("Log Out");
  private JButton cmdWithdraw = new JButton("Withdraw Sum");
  private Connection myConnection;
  private JTextField t = new JTextField(15);
  private Vector v = new Vector();
  private JComboBox jcb = new JComboBox(v);
  private static int pinCode;
  
  // BEHAVIOUR

    /**
     * Constructor
     */
    public CashDispenser(int pincode)
  {
    super();
    pinCode = pincode;
    init(pincode);
  }

    /**
     * Constructor
     * @param title
     */
    public CashDispenser(String title, int pincode)
  {
    super();
    this.setTitle(title);
    pinCode = pincode;
    init(pincode);
  }

    /**
     * method to initiate everything
     */
  private void init(int pincode)
  {
    addComponentsToFrame();
    addListeners();
    setupDatabaseConnection();
    txtOut.append("Ok, logged in\r\n");
    
    try
    {
        Statement stmtThree = myConnection.createStatement(); 
        ResultSet resThree = stmtThree.executeQuery("select name from AccountHolder where pincode ="+ pincode);
        
        while(resThree.next()) {
            txtOut.append("Welcome " + resThree.getString("name") + "\r\n");
        }
          try (Statement stmt = myConnection.createStatement(); 
               ResultSet res = stmt.executeQuery("select * from Account_Connections where ah_id = (select id from AccountHolder where pincode ="+ pincode+")")) {

              while (res.next()) {
                  txtOut.append("Accountnr " + res.getString("cash_accountNr") + "\r\n");
                  v.add(res.getString("cash_accountNr"));
                  
              jcb.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                          txtOut.setText("Account Number: " +  "   "
                              + ((JComboBox) e.getSource()).getSelectedItem());
                          
                    String valueFromSelectedItem = String.valueOf(jcb.getSelectedItem());
            
                    if(!valueFromSelectedItem.equals("null")) {
                        Integer accNr = Integer.parseInt(valueFromSelectedItem);
                        
                        try {Statement stmtTwo = myConnection.createStatement(); 
                             ResultSet resTwo = stmtTwo.executeQuery("select balance from Cash_Account where accNr ="+accNr);
                             
                             while(resTwo.next()) {
                                 txtOut.append("\nYour balance is $" + resTwo.getString("balance"));
                             }
                             
                           } catch (SQLException ex) {
                               Logger.getLogger(CashDispenser.class.getName()).log(Level.SEVERE, null, ex);
                           }
                    }
                          
                           
              }
            });
              }
          }
    
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
      txtOut.append(e.getMessage() + "\r\n");
    } 

    getContentPane().add(jcb);

  }

    /**
     * Setting up the database connection
     */
  private void setupDatabaseConnection()
  {
      
    try
    {
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      this.myConnection = DriverManager.getConnection("jdbc:sqlserver://194.47.129.139;user=dv1454_ht13_49;password=password;database=dv1454_ht13_49");
      System.out.println("Successfully Connected to the database!");

    }    
    catch (ClassNotFoundException e) { 
         System.out.println("Could not find the database driver " + e.getMessage());
     } 
    catch (SQLException e) {
         System.out.println("Could not connect to the database " + e.getMessage());
     }
  
    
  }

  /**
   * Add components to the frame
   */
  private void addComponentsToFrame()
  {
    Container cp = this.getContentPane();
    cp.setLayout(new FlowLayout());
    JPanel jpMain = new JPanel();
    jpMain.setLayout(new BoxLayout(jpMain,BoxLayout.Y_AXIS ));
    JPanel jpText = new JPanel();
    jpText.setLayout(new BorderLayout());
    scroll.setVerticalScrollBarPolicy(
    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
    jpText.add(scroll);
    JPanel jpButtons = new JPanel();
    jpButtons.setLayout(new FlowLayout());
    jpText.add(txtOut);
    jpMain.add(jpButtons);
    jpMain.add(jpText);
    jpMain.setBorder(BorderFactory.createEtchedBorder());
    cp.add(jpMain);
    cp.add(cmdClose);
    cp.add(cmdLogOut);
    cp.add(cmdWithdraw);
    //cp.add(c);
    cp.add(t);
  }


    /**
     * Add listeners
     */ 
  private void addListeners()
  {
    this.addWindowListener(new WindowAdapter()
                             {
                                 @Override
      public void windowClosing(WindowEvent w)
      {
        closeFrame();
      }
    });
    cmdClose.addActionListener(new ActionListener()
                                 {
                                     @Override
      public void actionPerformed(ActionEvent a)
      {
        closeFrame();
      }
    });                               
     
    cmdLogOut.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent a)
      {
        logOutButtonPressed();
      }
    });
    
    cmdWithdraw.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent ae){
            String valueFromSelectedItem = String.valueOf(jcb.getSelectedItem());
            
            if(!valueFromSelectedItem.equals("null")) {
                Integer fromAcc = Integer.parseInt(valueFromSelectedItem);
                System.out.println("fromAcc value is " + fromAcc.toString());
                   
                String textFieldValue = t.getText(); 

                if(!textFieldValue.equals("")) {
                    Integer withdrawSum = Integer.parseInt(textFieldValue);
                    System.out.println("withdrawSum is " + withdrawSum.toString());
                    
                    CallableStatement callableStatement = null;

                try {
                        Integer result = 0;

                        callableStatement = myConnection.prepareCall("exec WithdrawMoney ?,?,?");
                        callableStatement.setInt(1, fromAcc);
                        callableStatement.setInt(2, withdrawSum);
                        callableStatement.registerOutParameter(3, Types.NUMERIC);
                        callableStatement.executeUpdate();
                        result = callableStatement.getInt(3);
                                              
                        if(result > 0) {
                            txtOut.setText("");
                            String outputString = "You withdrew $" + withdrawSum.toString();
                            txtOut.append("You withdrew $" + withdrawSum.toString() + "\r\n");
                            timedDialog(outputString);
                        }
                        else {
                            txtOut.setText("");
                            txtOut.append("Result from the query was " + result.toString() + ". \nSomething went wrong. Sue the bank.");
                        }
                 }

                 catch (SQLException ex) {
                        Logger.getLogger(CashDispenser.class.getName()).log(Level.SEVERE, null, ex);
                 }
            }
                
            }
            else {
                txtOut.setText("");
                txtOut.setText("Choose an account");
            }
        }
    });
  }
  
    /**
     * Close the frame, close everything
     */
  private void closeFrame()
  {
    try {
      // close all open resources here
      this.myConnection.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    } 
    System.exit(0);
  }

  /**
   * Log out, close everything
   */
  private void logOutButtonPressed()
  {
      try {
      // close all open resources here
      this.myConnection.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    } 
    System.exit(0);
  }
  
  public void timedDialog(String input) {
     JFrame f = new JFrame();
     JTextField text = new JTextField(input);
        final JDialog dialog = new JDialog(f, "Confirmation of withdrawal", true);
        dialog.setSize(200, 150);
        dialog.add(text);
        dialog.setLocationRelativeTo(null);
        Timer timer = new Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true); // if modal, application will pause here

        System.out.println("Dialog closed");
    }
}
