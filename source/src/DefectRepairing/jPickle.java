package DefectRepairing;
import java.io.*;
import java.util.Date;

public class jPickle {
    public static void dump(Object o,String FilePath){
        try {
        	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FilePath));
			out.writeObject(o);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public static Object load(String FilePath){
    	Object o = null;
        try {
        	ObjectInputStream in = new ObjectInputStream(new FileInputStream(FilePath));
            o =in.readObject();
            in.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
        return o;
    }
}

