import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MailsFile {
    
    File file;
    FileOutputStream fOutPut;
    ObjectOutputStream oOutPut;
    FileInputStream fInPut;
    ObjectInputStream oInPut;
    
    private static MailsFile instance = null;
    
    public MailsFile() {
        try {
            file = new File("mail");
            fOutPut = new FileOutputStream(file);
            oOutPut  = new ObjectOutputStream(fOutPut);
            fInPut = new FileInputStream(file);
            oInPut = new ObjectInputStream(fInPut);
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public static MailsFile getInstance() {
            if (instance == null) {
                    instance = new MailsFile();
            }
            return instance;
    }

	public void seedMessages() {
            ArrayList<Message> l = new ArrayList<Message>();
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <root@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"+
                                "Bonjour \r\n"+
                                "comment allez vous ? \r\n"+
                                ".\r\n","root"));
            
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <root@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                + "Cacao Cacaaaaaaoooooooooooooooooooo"+
                                ".\r\n","root"));
            
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <root@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                + "Au revoir"+
                                ".\r\n","root"));
            
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <chloe@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod"+
                                ".\r\n","Chloe"));
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <chloe@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"tempor incididunt ut labore et dolore magna aliqua"+
                                ".\r\n","Chloe"));
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <chloe@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"Ut enim ad minim veniam"+
                                ".\r\n","Chloe"));
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <chloe@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"quis nostrud exercitation ullamco laboris nisi ut"+
                                ".\r\n","Chloe"));
            
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <corentin@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"aliquip ex ea commodo consequat"+
                                ".\r\n","Corentin"));
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <corentin@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"Duis aute irure dolor in reprehenderit in voluptate velit "+
                                ".\r\n","Corentin"));
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <corentin@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"esse cillum dolore eu fugiat nulla pariatur"+
                                ".\r\n","Corentin"));
            l.add(new Message("From: Moi <moi@meme.com>\r\n" +
                                "To: Root <corentin@root.root>\r\n" +
                                "Subject: IGP\r\n" +
                                "Date: Sat, 2 Apr 2016 19:11:00\r\n"+
                                "\r\n"
                                +"Excepteur sint occaecat cupidatat non proident"+
                                ".\r\n","Corentin"));
            
            try{		
                oOutPut.writeObject(l);		
            }		
            catch( Exception e){
                    System.out.println(e);
            }
	}
        
        public void seedReceivers() {
            HashMap<String,String> receivers = new HashMap();
            receivers.put("root", "root");
            receivers.put("Chloe", "chocolat");
            receivers.put("Corentin", "clafoutis");
            
            try {
                File fileReceiver = new File("receiver");
                FileOutputStream f = new FileOutputStream(fileReceiver);
                ObjectOutputStream o  = new ObjectOutputStream(f);
                o.writeObject(receivers);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }
        
        
        
        public void updateMessages(List<Message> messages) {
            try{                  
                    file.delete();
                    file = new File("mail");
                    oOutPut.writeObject(messages);
                    			
		}		
		catch( Exception e){
                    System.out.println(e);
		}
        }
        
        public List<Message> readMails(String username) throws IOException, ClassNotFoundException {
            List<Message> messages = (List<Message>) oInPut.readObject();
            for (Message m : messages) {
                if (m.getReceiver() != username)
                        messages.remove(m);
            }
            return messages;
        }
        
        public HashMap<String,String> readReceivers() throws IOException, ClassNotFoundException {
            File fileReceiver = new File("receiver");
            FileInputStream f = new FileInputStream(fileReceiver);
            ObjectInputStream o  = new ObjectInputStream(f);
            HashMap<String,String> receivers = (HashMap<String,String>) o.readObject();
            return receivers;
        }
}