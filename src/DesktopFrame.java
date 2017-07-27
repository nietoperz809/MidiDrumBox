// Fig. 22.11: DesktopFrame.java
// Demonstrating JDesktopPane.
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DesktopFrame extends JFrame
{
    private final JDesktopPane theDesktop;
    private final ArrayList<Drumbox> allBoxes = new ArrayList<>();
    private final JMenu docMenu;
    private JTextField patternList;
    private Drumbox currentActiveBox;

    // set up GUI
    private DesktopFrame ()
    {
        super("MIDI Drumbox");

        setLayout(new BorderLayout());

        JMenuBar bar = new JMenuBar(); // create menu bar
        JMenu addMenu = new JMenu("Single"); // create Add menu
        JMenuItem newFrame = new JMenuItem("New");
        JMenuItem load = new JMenuItem("Load"); // create Add menu
        JMenuItem clone = new JMenuItem("Clone"); // create Add menu

        JMenu projMenu = new JMenu("Project"); // create Add menu
        JMenuItem pload = new JMenuItem("Load"); // create Add menu
        JMenuItem psave = new JMenuItem("Save"); // create Add menu
        projMenu.add(pload);
        projMenu.add(psave);
        pload.addActionListener(event -> loadProject());
        psave.addActionListener(event -> saveProject());

        docMenu = new JMenu("Patterns"); // create Add menu

        addMenu.add(newFrame); // add new frame item to Add menu
        addMenu.add(load); // add new frame item to Add menu
        addMenu.add(clone);

        bar.add(addMenu); // add Add menu to menu bar
        bar.add(projMenu);
        bar.add(docMenu);
        setJMenuBar(bar); // set menu bar for this application

        theDesktop = new JDesktopPane(); // create desktop pane
        Object o = theDesktop.getDesktopManager();
        
        theDesktop.setDesktopManager(new DefaultDesktopManager());

        add(theDesktop, BorderLayout.CENTER); // add desktop pane to frame

        add(createControlPanel(), BorderLayout.SOUTH);

        newFrame.addActionListener(event -> newDrumbox());
        load.addActionListener(event -> loadDrumbox());
        clone.addActionListener(event -> cloneDrumbox());
    }

    private void loadProject()
    {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Drum Project",
                "dproj");
        fc.setFileFilter(filter);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                String filename = fc.getSelectedFile().getCanonicalPath();
                loadProject(filename);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void loadProject  (String filename)
    {
        ObjectReader r = new ObjectReader(filename);
        patternList.setText((String)r.getObject());
        for (; ; )
        {
            Drumbox box = newDrumbox();
            if (box == null)
            {
                System.out.println("Drumbox creation fail");
                return;
            }
            if (!box.loadFromStream(r))
            {
                JInternalFrame ji = box.getMdiClient();
                ji.dispose();
                allBoxes.remove(box);
                docMenu.remove(docMenu.getItemCount() - 1);
                break;
            }
        }
        r.close();
    }

    private void saveProject()
    {
        final JFileChooser fc = new JFileChooser();
        File f = new File("Drumproject#" +
                "-" + System.currentTimeMillis() + ".dproj");
        fc.setSelectedFile(f);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                String name = fc.getSelectedFile().getCanonicalPath();
                saveProject(name);
            }
            catch (Exception ex2)
            {
                System.out.println(ex2);
            }
        }
        else
        {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void saveProject (String fname)
    {
        ObjectWriter w = new ObjectWriter(fname);
        w.putObject(patternList.getText());
        for (Drumbox d : allBoxes)
        {
            d.savePattern(w);
        }
        w.close();
    }

    private JPanel createControlPanel ()
    {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        patternList = new JTextField();
        patternList.setBackground(Color.white);
        patternList.setPreferredSize(new Dimension(600, 20));
        p.add(patternList);

        JButton butt = new JButton("Create MIDI");
        p.add(butt);
        butt.addActionListener(e ->
        {
            String str = patternList.getText().replaceAll("\\s+", "");
            String[] nums = str.split(",");
            ArrayList<Integer> ar = new ArrayList<>();
            for (String s : nums)
            {
                try
                {
                    ar.add (Integer.parseInt(s));
                }
                catch (Exception unused)
                {

                }
            }
            createMidi(ar);
        });
        return p;
    }

    private void createMidi(ArrayList<Integer> ar)
    {
        try
        {
            Sequence s_out = new Sequence(0.0f, 960);
            Track t_out = s_out.createTrack();
            long offset = 0;
            for (Integer i : ar)
            {
                Drumbox box = allBoxes.get(i);
                Sequence seq = box.createMIDI();
                Track tr = seq.getTracks()[0];
                for (int s = 0; s < tr.size(); s++)
                {
                    MidiEvent ev = tr.get(s);
                    if (ev.getMessage().getStatus() == 255) // end of track
                    {
                        continue;
                    }
                    ev.setTick(ev.getTick() + offset);
                    t_out.add(ev);
                }
                offset += (tr.ticks() + box.getSliderValue());
            }
            File f = new File("c:\\midfile.mid");
            try
            {
                MidiSystem.write(s_out, 1, f);
                JOptionPane.showMessageDialog(this, "Saved to: " + f.getAbsolutePath(), "Drum Tool", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e1)
            {
                JOptionPane.showMessageDialog(this, "Saving fail: " + e1, "Drum Tool", JOptionPane.ERROR_MESSAGE);
            }

        }
        catch (InvalidMidiDataException e)
        {
            System.out.println(e);
        }
    }

    private Drumbox newDrumbox ()
    {
        // create internal   frame
        JInternalFrame frame = new JInternalFrame(
                "P #" + allBoxes.size(), true, false, true, true);
        try
        {
            Drumbox drumbox = new Drumbox(frame);
            frame.addInternalFrameListener(new InternalFrameAdapter()
            {
                @Override
                public void internalFrameActivated (InternalFrameEvent e)
                {
                    currentActiveBox = drumbox;
                    super.internalFrameActivated(e);
                }
            });
            JMenuItem item = new JMenuItem(frame.getTitle());
            item.addActionListener(e ->
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

    private void cloneDrumbox()
    {
        Drumbox b1 = currentActiveBox;
        Drumbox box = newDrumbox();
        if (box == null)
        {
            System.out.println("Drumbox creation fail");
            return;
        }
        if(b1 != null)
            box.cloneBox(b1);
    }

    private void loadDrumbox ()
    {
        Drumbox box = newDrumbox();
        if (box == null)
        {
            System.out.println("Drumbox creation fail");
            return;
        }
        box.loadWithDialog();
    }

    public static void main (String args[]) throws Exception
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        SwingUtilities.invokeLater(() ->
        {
            UIManager.put("ToggleButton.select", Color.RED);
            DesktopFrame desktopFrame = new DesktopFrame();
            desktopFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            desktopFrame.setSize(800, 600); // set frame size
            desktopFrame.setVisible(true); // display frame
        });
    } // end main
} // end class DesktopFrame

