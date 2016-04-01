
import java.io.Serializable;

/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */
public class Message implements Serializable {
    
    String receiver;
    String message;
    
    public Message(String msg, String receiver) {
        message = msg+"\r\n";
        this.receiver = receiver;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getReceiver(){
        return receiver;
    }
}
