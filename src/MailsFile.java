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
            l.add(new Message("Bonjour comment allez vous ? ","root"));
            l.add(new Message("Cacao Cacaaaaaaoooooooooooooooooooo","root"));
            l.add(new Message("Au revoir","root"));
            
            l.add(new Message("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod","Chloe"));
            l.add(new Message("tempor incididunt ut labore et dolore magna aliqua","Chloe"));
            l.add(new Message("Ut enim ad minim veniam","Chloe"));
            l.add(new Message("quis nostrud exercitation ullamco laboris nisi ut","Chloe"));
            
            l.add(new Message("aliquip ex ea commodo consequat","Corentin"));
            l.add(new Message("Duis aute irure dolor in reprehenderit in voluptate velit ","Corentin"));
            l.add(new Message("esse cillum dolore eu fugiat nulla pariatur","Corentin"));
            l.add(new Message("Excepteur sint occaecat cupidatat non proident","Corentin"));
            
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
}