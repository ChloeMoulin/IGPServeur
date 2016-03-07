
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */

public class ServerPop3 {
    
    private static ServerSocket ss;
    
    public static void main(String[] arg) throws IOException {
        ServerPop3 Server = new ServerPop3();
        Socket connexionClient = null;
        while(true) { 
            System.out.println("Waiting for client.");
            connexionClient = Server.ss.accept();
            System.out.println("New POP3 Client. Adress "+connexionClient.getInetAddress()+" on "+connexionClient.getPort());
            ThreadServerPop3 st = new ThreadServerPop3(connexionClient);
            st.run();
        }
    }
    
    public ServerPop3() {
        ss = null;
        try {
            ss = new ServerSocket(110);        
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    
}
