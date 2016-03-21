
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
            char[] received_char = new char[100];
            String msg_send;

            int i = br.read(received_char);
            msg = String.valueOf(received_char);
            
            String username,password;
            boolean connected = false, waitingForPass = false;
            
            System.out.println(msg);
            if (msg.startsWith("USER")) {
                username = msg.split(" ")[1];
                msg = "+OK";
                
                try {
                    bw.write(msg,0,msg.length());
                     bw.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ThreadServerPop3.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                
                System.out.println("Nouveau Client "+username);
                waitingForPass = true;
                if(waitingForPass) {
                    br.read(received_char);
                    msg = String.valueOf(received_char);
                    System.out.println(msg);
                    if (msg.startsWith("PASS")) {
                        password = msg.split(" ")[1];
                        msg = "+OK maildrop has 1 message (369 octets)";
                        try {
                            bw.write(msg,0,msg.length());
                            bw.flush();
                        } catch (IOException ex) {
                            Logger.getLogger(ThreadServerPop3.class.getName()).log(Level.SEVERE, null, ex);
                        }


                        System.out.println("Mot de Passe "+password);
                        connected = true;
                    }
                }

            }
            else if (msg.startsWith("APOP")) {
                username = msg.split(" ")[1];
                password = msg.split(" ")[2];
                msg = "+OK maildrop has 1 message (369 octets)";
                bw.write(msg,0,msg.length());
                bw.flush();
                
                System.out.println("Nouveau Client "+username+" Mot de passe "+password);
                connected = true;
                
            }
            
            if (connected) {
                while(true) {
                    br.read(received_char);
                    msg = String.valueOf(received_char);
                    System.out.println(msg);
                    if (msg.startsWith("RETR 1")) {
                        msg = "+OK 369 octets message : cyril t'es nul à chier";
                        bw.write(msg,0,msg.length());
                        bw.flush();
                    }
                }
            }
                
            } catch (IOException ex) {
            System.err.println(ex);
        }

           

    }
}

