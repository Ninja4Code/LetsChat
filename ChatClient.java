import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.SwingUtilities;

public class ChatClient extends JFrame 
{ 
   private GridBagLayout layout; 
   private GridBagConstraints constraints;
   private ObjectOutputStream output; 
   private ObjectInputStream input; 
   private String message = ""; 
   private String chatServer; 
   private Socket client; 
   private JButton btnSubmission;
   private JLabel lblMessage;
   private JLabel lblSideBar;
   private JLabel lblSideBarRight;
   private JLabel lblFiller;
   private JLabel lblHeader;
   private JLabel lblCopyright;
   private JTextArea txtSubmission;
   private JTextArea txtDiscussion;
   private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
   private JScrollPane scrollPane = null;
   
   public ChatClient(String host)
   {
      super( "Let's Chat! Client" );
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
      txtDiscussion.setBorder(border);
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
      addComponent(txtSubmission, 3, 1, 2, 1 );             
      constraints.weightx = 1000;  
      constraints.weighty = 2;     
      constraints.fill = GridBagConstraints.BOTH;
      addComponent( scrollPane, 2, 1, 2, 1 ); 
      constraints.weightx = 1000;  
      constraints.weighty = 0.4;   
      constraints.fill = GridBagConstraints.BOTH;
      addComponent( lblCopyright, 6, 2, 1, 1 );
      addComponent( lblHeader, 0, 2, 1, 1 );  
      
      chatServer = host; 

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
      addWindowListener(new WindowAdapter() 
      {
        public void windowClosing( java.awt.event.WindowEvent e ) 
        {
          closeConnection(); 
          System.exit(0);
        }
      });      
      setVisible( true ); 
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
   public void runClient() 
   {
      try 
      {
         connectToServer(); 
         getStreams(); 
         processConnection(); 
      } 
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nClient terminated connection" );
      } 
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } 
      finally 
      {
         closeConnection();
      } 
   }
   private void connectToServer() throws IOException
   {      
      displayMessage( "Attempting connection\n" );
      client = new Socket( InetAddress.getByName( chatServer ), 12345 );
      displayMessage( "Connected to: " + client.getInetAddress().getHostName() );
   }
   private void getStreams() throws IOException
   {
      output = new ObjectOutputStream( client.getOutputStream() );      
      output.flush();
      input = new ObjectInputStream( client.getInputStream() );
      //displayMessage( "\nGot I/O streams\n" );
   }
   private void processConnection() throws IOException
   {
      setTextFieldEditable( true );
      do 
      { 
         try 
         {
            message = ( String ) input.readObject(); // read new message
        	displayMessage( "\n" + message ); // display message
         } 
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } 

      } while ( !message.equals( "SERVER>>> TERMINATE" ) );
   }
   private void closeConnection() 
   {
      displayMessage( "\nClosing connection" );
      setTextFieldEditable( false ); 
      try 
      {
    	 if(output != null)
    	 {    		 
    		 output.close(); 
    	 }
    	 if(input != null)
    	 {
    		 input.close();
    	 }
    	 if(client != null)
    	 {
    		 client.close(); 
    	 }
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
         output.writeObject( "CLIENT>>> " + dateStampWithMessage );
         output.flush();
         displayMessage( "\nCLIENT>>> " + dateStampWithMessage);
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
               txtDiscussion.append( messageToDisplay );
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