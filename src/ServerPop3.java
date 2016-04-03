
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */

public class ServerPop3 {
    
    private static ServerSocket ss;
    
    public static void main(String[] arg) throws IOException {
        
        
        File f = new File("mail");
        if(!f.exists())
            MailsFile.getInstance().seedMessages();
        
        f = new File("receiver");
        if(!f.exists())
            MailsFile.getInstance().seedReceivers();
        
                
        ServerPop3 Server = new ServerPop3();
        Socket connexionClient = null;
        while(true) { 
            System.out.println("Waiting for client.");
            connexionClient = Server.ss.accept();
            System.out.println("New POP3 Client. Adress "+connexionClient.getInetAddress()+" on "+connexionClient.getPort());
            ThreadServerPop3 st = new ThreadServerPop3(connexionClient);
            st.run();
            st.interrupt();
        }
    }
    
       public ServerPop3() {
        SSLServerSocketFactory sslfactory =(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {
            SSLServerSocket ss = (SSLServerSocket) sslfactory.createServerSocket(44856);
            String[] strings=ss.getSupportedCipherSuites();
            String[] parametres=new String[strings.length];
            int i=0;
            for(String param : strings){
               if(param.contains("anon")){
                   parametres[i]=param;
                   i++;
               }
            }
            ss.setEnabledCipherSuites(strings);
            this.ss = ss;
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
}
