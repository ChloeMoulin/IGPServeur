
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */
public class ThreadServerPop3 extends Thread {
    
    private Socket socket;
    private String timestamp;
    private InputStream input;
    private OutputStream output;
    private BufferedInputStream br;
    private BufferedOutputStream bw;
    
    private Status status;
    private List<Message> messages;
    private List<Message> markedMessages;
    List<Message> readMessages;
    
    private final String READY = "+OK POP3 server ready";
    private final String OK = "+OK";
    private final String ERROR = "-ERR";
    
    
    public ThreadServerPop3 (Socket connexionClient) {
        timestamp = ThreadServerPop3.generateTimeStamp();
        markedMessages  = new ArrayList();
        readMessages = new ArrayList();
        status = Status.AUTHORIZATION;
        socket = connexionClient;

        try {
            bw = new BufferedOutputStream(this.socket.getOutputStream());
            br = new BufferedInputStream(this.socket.getInputStream());
        } catch (IOException ex) {
            System.err.println(ex);
        } 
    }
    
    
    public void run() {
        
        String messageToSend;           
        String receivedMessage;
        byte[] receivedChar = new byte[100];
        boolean quit = false;
        String username= "",password= "";

        
        messageToSend = READY+" "+timestamp+" \r\n";
        System.out.println(messageToSend);
        try {
            bw.write(messageToSend.getBytes("UTF-8"));
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
                        } else if (receivedMessage.startsWith("APOP") && receivedMessage.split(" ").length >= 4) {
                            username = receivedMessage.split(" ")[1];
                            
                            Boolean b = MailsFile.getInstance().readReceivers().containsKey(username);
                            password = receivedMessage.split(" ")[2];
                            if(b && ThreadServerPop3.compareHashPassword(password, MailsFile.getInstance().readReceivers().get(username),timestamp)) {
                                messageToSend = OK +" "+ messages.size() +" 369 \r\n";
                                System.out.println("New Client "+username+" Password "+password);

                                try{
                                    messages = MailsFile.getInstance().readMails(username);
                                }   catch( IOException | ClassNotFoundException e){
                                        System.err.println(e.getMessage());
                                }

                                status = Status.TRANSACTION;
                            } else {
                                messageToSend = ERROR +" \r\n";
                            }
                        }

                        bw.write(messageToSend.getBytes("UTF-8"));
                        bw.flush();

                    } catch (Exception e) {
                        System.err.println(e.getMessage()); 
                    }
                }
                    
                case WAITING_PASS :
                {
                    try {
                        receivedChar = new byte[100];
                        br.read(receivedChar);
                        receivedMessage = String.valueOf(receivedChar);
                        if (receivedMessage.startsWith("PASS") && receivedMessage.split(" ").length >= 2) {
                            password = receivedMessage.split(" ")[1];
                            System.out.println("Password for "+username+" : "+password);

                            messageToSend = OK +" "+ messages.size() +" 369 \r\n";
                            bw.write(messageToSend.getBytes("UTF-8"));
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
                        receivedChar = new byte[100];
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

                            bw.write(messageToSend.getBytes("UTF-8"));
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
                            bw.write(messageToSend.getBytes("UTF-8"));
                            bw.flush();
                        } else if (receivedMessage.equals("QUIT")) {
                            messageToSend = OK +" \r\n";
                            bw.write(messageToSend.getBytes("UTF-8"));
                            bw.flush();
                            messages.removeAll(markedMessages);
                            MailsFile.getInstance().updateMessages(messages);
                            quit = true;
                        } else {
                            messageToSend = ERROR +" \r\n";
                            bw.write(messageToSend.getBytes("UTF-8"));
                            bw.flush();
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }      
                }     
            }                
        }
    }
    
    public static boolean compareHashPassword(String hashedPassword, String expectedPassword,String timestamps) {
        String hashed = "";
        hashedPassword = timestamps+hashedPassword;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(expectedPassword.getBytes(),0,expectedPassword.length());
            hashed = new BigInteger(1, md.digest()).toString(16);        
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return (hashed.equals(hashedPassword));
    }
    
    private static String generateTimeStamp() {
        UUID uuid = UUID.randomUUID();
        String processID = uuid.toString();
        long clock = System.currentTimeMillis();
        String host = ManagementFactory.getRuntimeMXBean().getName();
        host = host.substring(host.indexOf("@") + 1);
        String timestamp = "<"+processID+clock+"@"+host+">";
        return timestamp;
    }
    
}

