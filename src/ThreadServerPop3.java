
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */
public class ThreadServerPop3 {
    
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    
    public ThreadServerPop3 (Socket connexionClient) {
        socket = connexionClient;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException ex) {
            System.err.println(ex);
        }       
    }
    
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
        
        String msg = "+OK POP3 server Ready";
        try {
            bw.write(msg,0,msg.length());
            bw.flush();
            char[] bwah = new char[100];
            int i = br.read(bwah);
            
            System.out.println(bwah);
            if (msg.startsWith("USER")) {
                String username = msg.split(" ")[1];
                msg = "+OK";
                bw.write(msg,0,msg.length());
                bw.flush();
                
                System.out.println("Nouveau Client "+username);
                
                msg = br.readLine();
                if (msg.startsWith("PASS")) {
                    String password = msg.split(" ")[1];
                    msg = "+OK";
                    bw.write(msg,0,msg.length());
                    bw.flush();
                    
                    System.out.println("Mot de Passe"+password);
                }
                
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

           

    }
}

