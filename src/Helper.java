import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Helper
{
    public static BufferedImage loadImageFromResource (String name)
    {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final InputStream is = loader.getResourceAsStream(name);
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
