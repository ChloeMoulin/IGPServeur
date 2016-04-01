
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */
public class ThreadServerPop3 extends Thread {
    
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private BufferedReader br;
    private BufferedWriter bw;
    
    private Status status;
    private List<Message> messages;
    private List<Message> markedMessages;
    List<Message> readMessages;
    
    private final String READY = "+OK POP3 server Ready \r\n";
    private final String OK = "+OK";
    private final String ERROR = "-ERR";
    
    
    public ThreadServerPop3 (Socket connexionClient) {
        markedMessages  = new ArrayList();
        readMessages = new ArrayList();
        status = Status.AUTHORIZATION;
        socket = connexionClient;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(input));
            bw = new BufferedWriter(new OutputStreamWriter(output));
        } catch (IOException ex) {
            System.err.println(ex);
        } 
    }
    
    
    public void run() {
        
        String messageToSend;           
        String receivedMessage;
        char[] receivedChar = new char[100];
        boolean quit = false;
        String username= "",password= "";

        
        messageToSend = READY;
        try {
            bw.write(messageToSend,0,messageToSend.length());
            bw.flush();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        while(!quit) {
            
            switch(status) {
                case AUTHORIZATION :
                {                    
                    try {
                        br.read(receivedChar);
                        receivedMessage = String.valueOf(receivedChar);
                        System.out.println("Client "+socket.getInetAddress()+" : "+receivedMessage);

                        if(receivedMessage.startsWith("USER") && receivedMessage.split(" ").length >= 2) {
                            username = receivedMessage.split(" ")[1];
                            System.out.println("New Client "+username);
                            status = Status.WAITING_PASS;
                            messageToSend = OK+"\r\n";
                        } else if (receivedMessage.startsWith("APOP") && receivedMessage.split(" ").length >= 3) {
                            username = receivedMessage.split(" ")[1];
                            password = receivedMessage.split(" ")[2];

                            messageToSend = OK +" "+ messages.size() +" 369 \r\n";
                            System.out.println("New Client "+username+" Password "+password);
                            
                            try{
                                messages = MailsFile.getInstance().readMails(username);
                            }   catch( IOException | ClassNotFoundException e){
                                    System.err.println(e.getMessage());
                            }
                                    
                            status = Status.TRANSACTION;
                        }

                        bw.write(messageToSend,0,messageToSend.length());
                        bw.flush();

                    } catch (Exception e) {
                        System.err.println(e.getMessage()); 
                    }
                }
                    
                case WAITING_PASS :
                {
                    try {
                        receivedChar = new char[100];
                        br.read(receivedChar);
                        receivedMessage = String.valueOf(receivedChar);
                        if (receivedMessage.startsWith("PASS") && receivedMessage.split(" ").length >= 2) {
                            password = receivedMessage.split(" ")[1];
                            System.out.println("Password for "+username+" : "+password);

                            messageToSend = OK +" "+ messages.size() +" 369 \r\n";
                            bw.write(messageToSend,0,messageToSend.length());
                            bw.flush();
                            
                            try{
                                messages = MailsFile.getInstance().readMails(username);
                            }   catch( IOException | ClassNotFoundException e){
                                    System.err.println(e.getMessage());
                            }
                            
                            status = Status.TRANSACTION;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }                     
                }

                case TRANSACTION :
                {
                    try {
                        receivedChar = new char[100];
                        br.read(receivedChar);
                        receivedMessage = String.valueOf(receivedChar);
                        System.out.println("New Request : "+receivedMessage);

                        if(receivedMessage.startsWith("RETR") && receivedMessage.split(" ").length >= 2) {
                            String tempString = receivedMessage.split(" ")[1];
                            int num = Integer.parseInt(tempString.split("\r\n")[0]);

                            if (num < 1 || num > messages.size() || markedMessages.contains(messages.get(num-1))) {
                                messageToSend = ERROR +" \r\n";
                            } else {
                                messageToSend = messages.get(num-1).getMessage();
                                messageToSend = OK+" \r\n" + messageToSend;
                                readMessages.add(messages.get(num-1));
                            }

                            bw.write(messageToSend,0,messageToSend.length());
                            bw.flush();
                            
                        } else if (receivedMessage.startsWith("DELE") && receivedMessage.split(" ").length >= 2) {
                            String tempString = receivedMessage.split(" ")[1];
                            int num = Integer.parseInt(tempString.split("\r\n")[0]);
                            if(num < 1 || num > messages.size() || !readMessages.contains(messages.get(num-1)) || markedMessages.contains(messages.get(num-1))) {
                                messageToSend = ERROR +" \r\n";
                            } else {
                                markedMessages.add(messages.get(num-1));
                                messageToSend = OK +" \r\n";

                            }
                            bw.write(messageToSend,0,messageToSend.length());
                            bw.flush();
                        } else if (receivedMessage.equals("QUIT")) {
                            messageToSend = OK +" \r\n";
                            bw.write(messageToSend,0,messageToSend.length());
                            bw.flush();
                            messages.removeAll(markedMessages);
                            MailsFile.getInstance().updateMessages(messages);
                            quit = true;
                        } else {
                            messageToSend = ERROR +" \r\n";
                            bw.write(messageToSend,0,messageToSend.length());
                            bw.flush();
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }      
                }     
            }                
        }
    }
}

