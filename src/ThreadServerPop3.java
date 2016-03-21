
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */
public class ThreadServerPop3 extends Thread {
    
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    
    private static String etat;
    private List<Message> messages = new ArrayList();
    
    
    public ThreadServerPop3 (Socket connexionClient) {
        socket = connexionClient;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException ex) {
            System.err.println(ex);
        } 
        		try{
			FileInputStream f = new FileInputStream("mail");
			ObjectInputStream o = new ObjectInputStream(f);
			messages = (List<Message>) o.readObject();
			o.close();	
                        for(Message m : messages) {
                            System.out.println(m.getMessage());
                        }
		}		
		catch( Exception e){
			System.out.println(e);
		}
    }
    
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
        
        
        String msg;

            
        char[] received_char = new char[100];
        int nbMessages = messages.size();
        List<Message> messagesRead = new ArrayList();
        
        etat = "Authorization";
        
        msg = "+OK POP3 server Ready \r\n";
        
        try {
            bw.write(msg,0,msg.length());
            bw.flush();
            
            br.read(received_char);
            msg = String.valueOf(received_char);
            
            String username,password;
            
            System.out.println(msg);
            
            if (etat.equals("Authorization") && msg.startsWith("USER")) {
                username = msg.split(" ")[1];
                msg = "+OK";
                
                try {
                    bw.write(msg,0,msg.length());
                     bw.flush();
                } catch (IOException ex) {
                    System.err.println(ex);
                }
                            
                System.out.println("Nouveau Client "+username);
                
                etat = "AttentePass";
                        
                if(etat.equals("AttentePass")) {
                    
                    received_char = new char[100];
                    br.read(received_char);
                    msg = String.valueOf(received_char);
                    
                    System.out.println(msg);
                    
                    if (msg.startsWith("PASS")) {
                        password = msg.split(" ")[1];
                        msg = "+OK 3 369 \r\n";
                        try {
                            bw.write(msg,0,msg.length());
                            bw.flush();
                        } catch (IOException ex) {
                            System.err.println(ex);
                        }


                        System.out.println("Mot de Passe "+password);
                        etat = "Transaction";
                    }
                }

            }
            else if (msg.startsWith("APOP")) {
                
                username = msg.split(" ")[1];
                password = msg.split(" ")[2];
                
                msg = "+OK maildrop has 1 message (369 octets) \n";
                bw.write(msg,0,msg.length());
                bw.flush();
                
                System.out.println("Nouveau Client "+username+" Mot de passe "+password);
                etat = "Transaction";
                
            }
            
            if (etat.equals("Transaction")) {
                boolean continu = true;
                while(continu == true) {
                    
                    received_char = new char[100];
                    br.read(received_char);                  
                    msg = String.valueOf(received_char);
                    System.out.println(msg);
                    
                    if (msg.toUpperCase().startsWith("RETR")) {
                        String tmp = msg.split(" ")[1];
                        
                        int num = Integer.parseInt(tmp.split("\r\n")[0]);
                        
                        if (num > nbMessages) {
                            msg = "-ERR \r\n";
                            bw.write(msg,0,msg.length());
                            bw.flush();
                        } else {
                            msg = messages.get(num-1).getMessage();
                            msg = "+OK 100 \r\n" + msg;
                            bw.write(msg,0,msg.length());
                            bw.flush();
                            System.out.println("Client a fait RETR "+num);
                            messagesRead.add(messages.get(num -1));
                        }
                            
                    } else if (msg.startsWith("DELE")) {
                        String tmp = msg.split(" ")[1];                       
                        int num = Integer.parseInt(tmp.split("\r\n")[0]);
                        if (num > nbMessages && !messagesRead.contains(messages.get(num -1 ))) {
                            msg = "-ERR \r\n";
                            bw.write(msg,0,msg.length());
                            bw.flush();
                        } else {
                            messages.remove(num -1 );
                            nbMessages--;
                            msg = "+OK \r\n";
                            bw.write(msg,0,msg.length());
                            bw.flush();
                            System.out.println("Client a fait DELE "+num);
                        }
                            
                    } else if (msg.equals("QUIT")) {
                        msg = "+ OK \r\n";
                        bw.write(msg,0,msg.length());
                        bw.flush();
                        continu = false;
                        
                    }
                }
                
                
            }
                
            } catch (IOException ex) {
            System.err.println(ex);
        }

           

    }
}

