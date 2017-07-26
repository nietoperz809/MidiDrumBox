import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectReader
{
    private FileInputStream f_in;
    private ObjectInputStream obj_in;

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

    public Object getObject()
    {
        try
        {
            return obj_in.readObject();
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

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
