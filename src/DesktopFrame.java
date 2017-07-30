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
    private final static String title ="MIDI Drumbox";
    private JTextField patternList;
    private Drumbox currentActiveBox;
    private String currentProjectName = "";

    // set up GUI
    private DesktopFrame ()
    {
        super(title);

        setLayout(new BorderLayout());

        JMenuBar bar = new JMenuBar(); // create menu bar
        JMenu actionMenu = new JMenu("Action"); // create Add menu
        JMenuItem newFrame = new JMenuItem("New Drumbox");
        JMenuItem load = new JMenuItem("Load Drumbox"); // create Add menu
        JMenuItem clone = new JMenuItem("Clone selected Drumbox"); // create Add menu
        JMenuItem delete = new JMenuItem("Delete all"); // create Add menu

        JMenu projMenu = new JMenu("Project"); // create Add menu
        JMenuItem pload = new JMenuItem("Load"); // create Add menu
        JMenuItem psave = new JMenuItem("Save"); // create Add menu
        projMenu.add(pload);
        projMenu.add(psave);
        pload.addActionListener(event -> loadProject());
        psave.addActionListener(event -> saveProject());

        docMenu = new JMenu("Patterns"); // create Add menu

        actionMenu.add(newFrame); // add new frame item to Add menu
        actionMenu.add(load); // add new frame item to Add menu
        actionMenu.add(clone);
        actionMenu.add(new JSeparator());
        actionMenu.add(delete);

        bar.add(actionMenu); // add Add menu to menu bar
        bar.add(projMenu);
        bar.add(docMenu);
        setJMenuBar(bar); // set menu bar for this application

        theDesktop = new JDesktopPane(); // create desktop pane
        theDesktop.setDesktopManager(new DefaultDesktopManager());

        add(theDesktop, BorderLayout.CENTER); // add desktop pane to frame
        add(createControlPanel(), BorderLayout.SOUTH);

        newFrame.addActionListener(event -> newDrumbox());
        load.addActionListener(event -> loadDrumbox());
        clone.addActionListener(event -> cloneDrumbox());
        delete.addActionListener(event -> deleteAll());
    }

    private void deleteAll()
    {
        String[] options = {"Yepp", "Nope"};
        int dialogResult = JOptionPane.showOptionDialog(this,
                "Delete everything?", "Warning",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]);
        if(dialogResult == JOptionPane.YES_OPTION)
        {
            docMenu.removeAll();
            theDesktop.removeAll();
            allBoxes.clear();
            theDesktop.repaint();
        }
    }

    /**
     * Menu click: load
     */
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
                currentProjectName = fc.getSelectedFile().getName();
                setTitle(title + " -- " + currentProjectName);
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

    /**
     * Loads project by file name
     * Creates a drum box with that project
     * @param filename file name of prj
     */
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

    /**
     * Menu click: save
     */
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

    /**
     * Save project in to file given by name
     * @param fname file name
     */
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

    /**
     * Create the Panel at the bottom of desktop
     * @return a new Panel
     */
    private JPanel createControlPanel ()
    {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBorder (BorderFactory.createLineBorder(Color.BLACK));

//        drumKits = new JComboBox<>(DrumKit.kitnames);
//        p.add (drumKits);

        patternList = new JTextField();
        patternList.setBackground(Color.white);
        patternList.setPreferredSize(new Dimension(500, 20));
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
                catch (Exception ignored)
                {

                }
            }
            saveMidi(ar);
        });
        return p;
    }

    private void saveMidi (ArrayList<Integer> ar)
    {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Midi Files",
                "mid");
        fc.setFileFilter(filter);
        File f = new File("midifile_" +
                System.currentTimeMillis() + ".mid");
        fc.setSelectedFile(f);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                f = fc.getSelectedFile();
                try
                {
                    Sequence seq = createMidi(ar);
                    if (seq == null)
                    {
                        JOptionPane.showMessageDialog(this,
                                "Nothing to do", "Drum Tool",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        MidiSystem.write(seq, 1, f);
                        JOptionPane.showMessageDialog(this,
                                "Saved to: " + f.getAbsolutePath(),
                                "Drum Tool", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                catch (IOException e1)
                {
                    JOptionPane.showMessageDialog(this,
                            "Saving fail: " + e1,
                            "Drum Tool", JOptionPane.ERROR_MESSAGE);
                }
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

    /**
     * Create a midi file from all drum boxes and controlled
     * by an array of numbers
     * @param ar sequence of patterns
     */
    private Sequence createMidi(ArrayList<Integer> ar)
    {
        if (ar.isEmpty())
            return null;
        int lastprogram = -1;
        try
        {
            Sequence s_out = new Sequence(0.0f, 960);
            Track t_out = s_out.createTrack();
            long offset = 1;
            for (Integer i : ar)
            {
                Drumbox box = allBoxes.get(i);
                Sequence seq = box.createMIDI();
                Track tr = seq.getTracks()[0];
                for (int s = 0; s < tr.size(); s++)
                {
                    MidiEvent ev = tr.get(s);
                    MidiMessage ms = ev.getMessage();
                    if (ms instanceof ShortMessage)
                    {
                        ShortMessage sm = (ShortMessage)ms;
                        if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE)
                        {
                            int prg = sm.getData1(); // skip multiple prg change to same prg
                            if (prg == lastprogram)
                                continue;
                            lastprogram = prg;
                        }
                    }
                    int status = ms.getStatus();
                    if (status == 255) // end of track
                    {
                        continue;
                    }
                    ev.setTick(ev.getTick() + offset);
                    t_out.add(ev);
                }
                offset += (tr.ticks() + box.getSpeedValue());
            }
            return s_out;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Create a new Drumbox and shows it as MDI frame
     * @return The drum box
     */
    private Drumbox newDrumbox()
    {
        JInternalFrame frame = new JInternalFrame(
                "P #" + allBoxes.size(), true, false, true, true);
        //Container c = frame.getContentPane();
        //GridLayout gl = new GridLayout(4, 1,0,0);
        try
        {
            Drumbox drumbox = new Drumbox(frame);
            frame.addInternalFrameListener(new InternalFrameAdapter()
            {
                @Override
                public void internalFrameActivated (InternalFrameEvent e)
                {
                    currentActiveBox = drumbox; // Store the current active Drumbox
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

    /**
     * Menu click: clone
     */
    private void cloneDrumbox()
    {
        Drumbox template = currentActiveBox;
        Drumbox box = newDrumbox();
        if (box == null)
        {
            System.out.println("Drumbox creation fail");
            return;
        }
        if(template != null)
            box.cloneBox(template);
    }

    /**
     * Load Drumbox from file
     */
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
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        SwingUtilities.invokeLater(() ->
        {
            UIManager.put("ToggleButton.select", Color.RED);
            DesktopFrame desktopFrame = new DesktopFrame();
            desktopFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            desktopFrame.setSize(1000, 600); // set frame size
            desktopFrame.setVisible(true); // display frame
        });
    } // end main
} // end class DesktopFrame

