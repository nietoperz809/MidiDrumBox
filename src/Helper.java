import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

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
    public static void serialize (String path, String filename, Object o) throws Exception
    {
        new File(path).mkdirs();
        FileOutputStream f_out = new FileOutputStream(path + filename);
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
        obj_out.writeObject(o);
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
    public static Object deSerialize (String path, String filename) throws Exception
    {
        Object ret = null;
        FileInputStream f_in = new FileInputStream(path + filename);
        ObjectInputStream obj_in = new ObjectInputStream(f_in);
        try
        {
            ret = obj_in.readObject();
        }
        finally
        {
            obj_in.close();
            f_in.close();
        }
        return ret;
    }
}
