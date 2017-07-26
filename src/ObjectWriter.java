import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectWriter
{
    private FileOutputStream f_out;
    private ObjectOutputStream obj_out;

    public ObjectWriter (String fname)
    {
        try
        {
            f_out = new FileOutputStream(fname);
            obj_out = new ObjectOutputStream(f_out);
        }
        catch (Exception e)
        {
            System.out.println(e);;
        }
    }

    public void putObject (Object o)
    {
        try
        {
            obj_out.writeObject(o);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    public void close()
    {
        try
        {
            obj_out.close();
            f_out.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
