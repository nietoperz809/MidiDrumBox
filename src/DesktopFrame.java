// Fig. 22.11: DesktopFrame.java
// Demonstrating JDesktopPane.
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DesktopFrame extends JFrame
{
    private JDesktopPane theDesktop;
    private ArrayList<Drumbox> allBoxes = new ArrayList<>();
    private JMenu docMenu;

    // set up GUI
    public DesktopFrame ()
    {
        super("MIDI Drumbox");

        JMenuBar bar = new JMenuBar(); // create menu bar
        JMenu addMenu = new JMenu("Open"); // create Add menu
        JMenuItem newFrame = new JMenuItem("New Drum Pattern");
        JMenuItem load = new JMenuItem("Load"); // create Add menu
        JMenuItem combine = new JMenuItem("Combine"); // create Add menu

        docMenu = new JMenu("Patterns"); // create Add menu

        addMenu.add(newFrame); // add new frame item to Add menu
        addMenu.add(load); // add new frame item to Add menu
        addMenu.add(combine);

        bar.add(addMenu); // add Add menu to menu bar
        bar.add(docMenu);
        setJMenuBar(bar); // set menu bar for this application

        theDesktop = new JDesktopPane(); // create desktop pane
        add(theDesktop); // add desktop pane to frame

        newFrame.addActionListener(event -> newDrumbox());
        combine.addActionListener(event -> combineAll());
        load.addActionListener(event -> loadDrumbox());
    }

    private void combineAll()
    {
        try
        {
            Sequence s_out = new Sequence(0.0f,960);
            Track t_out = s_out.createTrack();
            long offset = 0;
            for (Drumbox box : allBoxes)
            {
                Sequence seq = box.createMIDI();
                Track tr = seq.getTracks()[0];
                for (int s=0; s<tr.size(); s++)
                {
                    MidiEvent ev = tr.get(s);
                    if (ev.getMessage().getStatus() == 255) // end of track
                    {
                        continue;
                    }
                    ev.setTick(ev.getTick()+offset);
                    t_out.add(ev);
                }
                offset += (tr.ticks()+box.getSliderValue());
            }
            File f = new File("c:\\midfile.mid");
            try
            {
                MidiSystem.write(s_out, 1, f);
                JOptionPane.showMessageDialog(this, "Saved to: "+f.getAbsolutePath(), "Drum Tool", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e1)
            {
                JOptionPane.showMessageDialog(this, "Saving fail: "+e1, "Drum Tool", JOptionPane.ERROR_MESSAGE);
            }

        }
        catch (InvalidMidiDataException e)
        {
            System.out.println(e);
        }
    }

    private void loadDrumbox()
    {
        Drumbox box = newDrumbox();
        if (box == null)
        {
            System.out.println("Drumbox creation fail");
            return;
        }
        box.loadWithDialog();
    }

    private Drumbox newDrumbox()
    {
        // create internal frame
        JInternalFrame frame = new JInternalFrame(
                "Pattern #" + Drumbox.instanceNumber, true, false, true, true);
        try
        {
            Drumbox drumbox = new Drumbox(frame);
            JMenuItem item = new JMenuItem(frame.getTitle());
            item.addActionListener((ActionListener) e ->
            {
                try
                {
                    frame.setIcon(false);
                }
                catch (PropertyVetoException e1)
                {
                    System.out.println(e1);
                }
                frame.toFront();
            });
            docMenu.add(item);
            allBoxes.add(drumbox);
            frame.add(drumbox, BorderLayout.CENTER); // add panel
            frame.pack(); // set internal frame to size of contents
            theDesktop.add(frame); // attach internal frame
            frame.setVisible(true); // show internal frame
            return drumbox;
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        return null;
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

