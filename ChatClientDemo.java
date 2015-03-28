import javax.swing.JFrame;

public class ChatClientDemo
{ 
   public static void main( String args[] )
   { 
      ChatClient frmChatClient;   
      if ( args.length == 0 )
      {
         frmChatClient = new ChatClient( "192.168.1.5" ); // connect to localhost
      }
      else
      {
         frmChatClient = new ChatClient( args[ 0 ] ); // use args to connect
      }
      frmChatClient.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frmChatClient.setSize( 500, 500 ); 
      frmChatClient.setVisible( true ); 
      frmChatClient.runClient(); // run 
      
   }
}