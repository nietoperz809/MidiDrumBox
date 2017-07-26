import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Helper
{
    public static Image loadImageFromRessource (String name)
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream(name);
        try
        {
            return ImageIO.read (is);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a save
     *
     * @param filename
     * @param o
     * @throws Exception
     */
    public static void serialize (String filename, Object... o) throws Exception
    {
        FileOutputStream f_out = new FileOutputStream(filename);
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
        for (int s=0; s<o.length; s++)
            obj_out.writeObject(o[s]);
        obj_out.close();
        f_out.close();
    }

    /**
     * Open a save
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static Object[] deSerialize (String filename) throws Exception
    {
        ArrayList<Object> list = new ArrayList<>();
        FileInputStream f_in = new FileInputStream (filename);
        ObjectInputStream obj_in = new ObjectInputStream(f_in);
        try
        {
            while (true)
            {
                Object ox = obj_in.readObject();
                list.add(ox);
            }
        }
        catch (Exception ex)
        {
            System.out.println("No more objects");
        }
        finally
        {
            obj_in.close();
            f_in.close();
        }
        return list.toArray();
    }
}
