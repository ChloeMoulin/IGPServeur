import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class InitMail {

	public static void main(String[] args) {
		
		ArrayList<Message> l = new ArrayList<Message>();
		l.add(new Message("Bonjour"));
                l.add(new Message("Cacao"));
                l.add(new Message("Au revoir"));		
		try{
			FileOutputStream f = new FileOutputStream("mail");
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(l);
			o.close();			
		}		
		catch( Exception e){
			System.out.println(e);
		}
	}
}