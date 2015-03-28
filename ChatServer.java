import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class ChatServer extends JFrame 
{ 
   private GridBagLayout layout; 
   private GridBagConstraints constraints;
   private ObjectOutputStream output; 
   private ObjectInputStream input; 
   private JButton btnSubmission;
   private JLabel lblMessage;
   private JLabel lblSideBar;
   private JLabel lblSideBarRight;
   private JLabel lblFiller;   
   private JLabel lblHeader;
   private JLabel lblCopyright;
   private JTextArea txtSubmission;
   private JTextArea txtDiscussion;
   private ServerSocket server; 
   private Socket connection; 
   private int counter = 1;
   private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
   private JScrollPane scrollPane = null;
   
   public ChatServer()
   {
	  super( "Let's Chat! Server" );
      layout = new GridBagLayout();
      
      setLayout( layout ); 
      getContentPane().setBackground(new Color(0xFA, 0xF7, 0xF0) );
      constraints = new GridBagConstraints(); 
      Border border = BorderFactory.createLineBorder(new Color(0x09, 0x8A, 0xBD));
      lblSideBar = new JLabel("          ");
      lblSideBarRight = new JLabel("          ");
      lblFiller = new JLabel("          ");
      lblHeader = new JLabel("Let's Chat!");
      lblCopyright = new JLabel( "Copyright 2015 Ninjas4Code" );
      txtSubmission = new JTextArea();
      txtSubmission.setBorder(border);
      lblMessage = new JLabel();
      
      Icon myIcon = new ImageIcon(getClass().getResource("chat-icon.png"));
      lblHeader.setIcon(myIcon); 
     
      txtDiscussion = new JTextArea();
      txtDiscussion.setBackground(new Color(0x09, 0x8A, 0xBD));
      txtDiscussion.setForeground(Color.WHITE); 
      txtDiscussion.setBorder(border);
      txtDiscussion.setLineWrap(true);
      btnSubmission = new JButton("         Send         ");     
      scrollPane = new JScrollPane(txtDiscussion,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);      
      
      addComponent( btnSubmission, 5, 1, 2, 1 );
      constraints.fill = GridBagConstraints.BOTH;
      addComponent( lblSideBar, 1, 0, 1, 4 ); 
      addComponent( lblSideBarRight, 1, 3, 1, 4 ); 
      addComponent( lblMessage, 1, 1, 2, 1 );
      addComponent( lblFiller, 4, 1, 2, 1 ); 
      constraints.weightx = 1000;  
      constraints.weighty = 0.2;     
      constraints.fill = GridBagConstraints.BOTH;
      addComponent( txtSubmission, 3, 1, 2, 1 );             
      
      constraints.weightx = 1000;  
      constraints.weighty = 2;     
      constraints.fill = GridBagConstraints.BOTH;
      addComponent( scrollPane, 2, 1, 2, 1 ); 
      constraints.weightx = 1000;  
      constraints.weighty = 0.4;   
      constraints.fill = GridBagConstraints.BOTH;
      addComponent( lblCopyright, 6, 2, 1, 1 );
      addComponent( lblHeader, 0, 2, 1, 1 );
      
      btnSubmission.addActionListener(
         new ActionListener() 
         {
            public void actionPerformed( ActionEvent event )
            {
               sendData(txtSubmission.getText());
               txtSubmission.setText( "" );
            } 
         } 
      );     
   }
   private void addComponent( Component component,
      int row, int column, int width, int height )
   {
      constraints.gridx = column;
      constraints.gridy = row; 
      constraints.gridwidth = width; 
      constraints.gridheight = height; 
      layout.setConstraints( component, constraints ); 
      add( component );
   } 
   public void runServer()
   {
      try 
      {
         server = new ServerSocket(12345, 100 ); 
        
         while ( true ) 
         {
            try 
            {
               waitForConnection(); 
               getStreams(); 
               processConnection(); 
            } 
            catch ( EOFException eofException ) 
            {
               displayMessage( "\nServer terminated connection" );
            } 
            finally 
            {
               closeConnection(); 
               counter++;
            } 
         } 
      } 
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } 
   } 
   private void waitForConnection() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      connection = server.accept();             
      displayMessage( "Connection " + counter + " received.");
   } 
   private void getStreams() throws IOException
   {
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); 
      input = new ObjectInputStream( connection.getInputStream() );

     // displayMessage( "\nGot I/O streams\n" );
   } 
   private void processConnection() throws IOException
   {
      String message = "Connection successful";
      sendData( message ); 
      setTextFieldEditable( true );

      do 
      { 
         try 
         {
            message = ( String ) input.readObject(); // read new message
            displayMessage( "\n" + message ); 
         } 
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } 

      } while ( !message.equals( "CLIENT>>> TERMINATE" ) );
   }  
   private void closeConnection() 
   {
      displayMessage( "\nTerminating connection\n" );
      setTextFieldEditable( false ); 

      try 
      {
         output.close(); 
         input.close(); 
         connection.close(); 
      } 
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } 
   }
   private void sendData( String message )
   {
      try 
      {
    	 String dateStampWithMessage = "[" + dateFormat.format(new Date()) + "] " + message;
         output.writeObject( "SERVER>>> " + dateStampWithMessage );
         output.flush(); 
         displayMessage( "\nSERVER>>> " + dateStampWithMessage);
      } 
      catch ( IOException ioException ) 
      {
         txtDiscussion.append( "\nError writing object" );
      } 
   }
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() 
            {
               txtDiscussion.append( messageToDisplay); 
               txtSubmission.requestFocus();
            } 
         } 
      ); 
   } 
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() 
            {
               txtSubmission.setEditable( editable );
            } 
         }  
      ); 
   }
}