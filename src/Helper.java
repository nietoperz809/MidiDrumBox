import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

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

    public static void showToolTip (JSlider tip, String txt)
    {
        showToolTip(tip, txt, tip::getValue);
    }

    public static void showToolTip (JSlider tip, String txt, Supplier<Number> act)
    {
        tip.setToolTipText(txt+": " + act.get());
        KeyEvent ke = new KeyEvent(tip, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), InputEvent.CTRL_MASK,
                KeyEvent.VK_F1, KeyEvent.CHAR_UNDEFINED);
        tip.dispatchEvent(ke);
    }

}
