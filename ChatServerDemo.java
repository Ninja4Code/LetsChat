import javax.swing.JFrame;

public class ChatServerDemo
{ 
   public static void main( String args[] )
   { 
	  ChatServer frmChatServer = new ChatServer(); // create server
      frmChatServer.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frmChatServer.setSize( 500, 500 ); 
      frmChatServer.setVisible( true ); 
      frmChatServer.runServer();      
   }
}