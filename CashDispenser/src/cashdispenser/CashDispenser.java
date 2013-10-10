/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cashdispenser;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


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
  //private JTextField txtIn = new JTextField(20);
  private JTextArea txtOut = new JTextArea(10,30);
  //private JScrollPane scroll = new JScrollPane();
  private JScrollPane txtOutscroll = new JScrollPane();
  private JButton cmdClose = new JButton("Close");
  private JButton cmdLogOut = new JButton("Log Out");
  private JButton cmdTransfer = new JButton("Transfer Sum");
  //private JComboBox c = new JComboBox();
  private Connection myConnection;
  private JTextField t = new JTextField(15);
  private Vector v = new Vector();
  private JComboBox jcb = new JComboBox(v);
  
  
  // BEHAVIOUR
  //
  //
  //
  public CashDispenser()
  {
    super();
    init();
  }

  //
  //
  //
  public CashDispenser(String title)
  {
    super();
    this.setTitle(title);
    init();
  }


  //
  //
  //
  private void init()
  {
    addComponentsToFrame();
    addListeners();
    setupDatabaseConnection();
    txtOut.append("Ok, logged in\r\n");
    txtOut.add(txtOutscroll);
    
    try
    {
          try (Statement stmt = myConnection.createStatement(); 
                  ResultSet res = stmt.executeQuery("SELECT * FROM AccountHolder")) {

              while (res.next())
              {
                  txtOut.append("Welcome " + res.getString("name") + "\r\n");
                  v.add(res.getString("name"));
                  //txtOut.append(res.getString("name") + "\r\n");
                  //txtOut.append(res.getString("street") + "\r\n");
                  
                   jcb.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                txtOut.setText("index: " + jcb.getSelectedIndex() + "   "
                    + ((JComboBox) e.getSource()).getSelectedItem());
                               txtOut.setText("");
                               txtOut.append(v.toString());
                          
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
      
    //this.setLocationRelativeTo(null);
  }

  //
  //
  //
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
  //
  //
  private void addComponentsToFrame()
  {
    Container cp = this.getContentPane();
    cp.setLayout(new FlowLayout());
    JPanel jpMain = new JPanel();
    jpMain.setLayout(new BoxLayout(jpMain,BoxLayout.Y_AXIS ));
    JPanel jpText = new JPanel();
    jpText.setLayout(new FlowLayout());
    JPanel jpButtons = new JPanel();
    jpButtons.setLayout(new FlowLayout());
    jpText.add(txtOut);
    jpMain.add(jpButtons);
    jpMain.add(jpText);
    jpMain.setBorder(BorderFactory.createEtchedBorder());
    cp.add(jpMain);
    cp.add(cmdClose);
    cp.add(cmdLogOut);
    cp.add(cmdTransfer);
    //cp.add(c);
    cp.add(t);
  }


  //
  //
  //
  private void addListeners()
  {
    this.addWindowListener(new WindowAdapter()
                             {
      public void windowClosing(WindowEvent w)
      {
        closeFrame();
      }
    });
    cmdClose.addActionListener(new ActionListener()
                                 {
      public void actionPerformed(ActionEvent a)
      {
        closeFrame();
      }
    });
    //cmdLogIn.addActionListener(new ActionListener()
                                      {
     
    cmdLogOut.addActionListener(new ActionListener()
                                      {
      public void actionPerformed(ActionEvent a)
      {
        logOutButtonPressed();
      }
    });
    
    cmdTransfer.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            String textFieldValue = t.getText();
            txtOut.append(textFieldValue);
        }
    });
    
    };
  }

  
  //
  //
  //
  private void closeFrame()
  {
    try
    {
      // close all open resources here
      this.myConnection.close();
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    } 
    System.exit(0);
  }


   private void logOutButtonPressed()
  {
      try
    {
      // close all open resources here
      this.myConnection.close();
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    } 
    System.exit(0);
  }
}