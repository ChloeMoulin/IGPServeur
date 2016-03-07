
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
        BufferedReader bf = new BufferedReader(new InputStreamReader(input));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
        
        String msg1 = "+OK POP3 server Ready";
        try {
            bw.write(msg1,0,msg1.length());
            bw.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
    }
    
}

