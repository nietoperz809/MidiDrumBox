import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

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

}
