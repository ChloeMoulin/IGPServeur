
import java.io.Serializable;

/**
 *
 * @author BITEAU Corentin p1410081
 *         MOULIN Chloé p1205600
 */
public class Message implements Serializable {
    
    
    String message;
    
    public Message(String msg) {
        message = msg+"\r\n";
    }
    
    public String getMessage() {
        return message;
    }
}
