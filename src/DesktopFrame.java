// Fig. 22.11: DesktopFrame.java
// Demonstrating JDesktopPane.
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DesktopFrame extends JFrame
{
    private JDesktopPane theDesktop;
    private ArrayList<Drumbox> allBoxes = new ArrayList<>();

    // set up GUI
    public DesktopFrame ()
    {
        super("MIDI Drumbox");

        JMenuBar bar = new JMenuBar(); // create menu bar
        JMenu addMenu = new JMenu("New"); // create Add menu
        JMenuItem newFrame = new JMenuItem("Drum Pattern");

        addMenu.add(newFrame); // add new frame item to Add menu
        bar.add(addMenu); // add Add menu to menu bar
        setJMenuBar(bar); // set menu bar for this application

        theDesktop = new JDesktopPane(); // create desktop pane
        add(theDesktop); // add desktop pane to frame

        newFrame.addActionListener(event -> newDrumbox());
    }

    private void newDrumbox()
    {
        // create internal frame
        JInternalFrame frame = new JInternalFrame(
                "Pattern #" + Drumbox.instanceNumber, true, false, true, true);
        try
        {
            Drumbox drumbox = new Drumbox();
            allBoxes.add(drumbox);
            frame.add(drumbox, BorderLayout.CENTER); // add panel
            frame.pack(); // set internal frame to size of contents
            theDesktop.add(frame); // attach internal frame
            frame.setVisible(true); // show internal frame
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public static void main (String args[])
    {
        UIManager.put("ToggleButton.select", Color.RED);
        DesktopFrame desktopFrame = new DesktopFrame();
        desktopFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        desktopFrame.setSize(600, 480); // set frame size
        desktopFrame.setVisible(true); // display frame
    } // end main
} // end class DesktopFrame

