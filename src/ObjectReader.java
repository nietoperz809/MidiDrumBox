import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectReader
{
    private FileInputStream f_in;
    private ObjectInputStream obj_in;

    /**
     * Constructor, set file name
     * @param fname file name
     */
    public ObjectReader (String fname)
    {
        try
        {
            f_in = new FileInputStream(fname);
            obj_in = new ObjectInputStream(f_in);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    /**
     * Get one Object
     * @return the object or null
     */
    public Object getObject()
    {
        try
        {
            return obj_in.readObject();
        }
        catch (Exception unused)
        {
            System.out.println("no more objects");
            return null;
        }
    }

    /**
     * Close Object reader
     * must finally be called
     */
    public void close()
    {
        try
        {
            obj_in.close();
            f_in.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
