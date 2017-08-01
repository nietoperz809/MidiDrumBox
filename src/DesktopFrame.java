// Fig. 22.11: DesktopFrame.java
// Demonstrating JDesktopPane.
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DesktopFrame extends JFrame
{
    private final JDesktopPane theDesktop;
    private final ArrayList<Drumbox> allBoxes = new ArrayList<>();
    private final JMenu docMenu;
    private final static String title = "Drum Track Creator";
    private JTextField patternList;
    private Drumbox currentActiveBox;
    private String currentProjectName = null;
    private JCheckBox notesOnly;
    private JSlider speedAdjust;
    private String currentProjectPath = null;
    private final FileNameExtensionFilter projectFilter = new FileNameExtensionFilter("Drum Project",
            "dproj");
    private final FileNameExtensionFilter midiFileFilter = new FileNameExtensionFilter("Midi Files",
            "mid");
    // set up GUI
    private DesktopFrame ()
    {
        super(title);

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing (WindowEvent e)
            {
                System.exit(0);
            }
        });

        setLayout(new BorderLayout());

        JMenuBar bar = new JMenuBar(); // create menu bar
        JMenu actionMenu = new JMenu("Action"); // create Add menu
        JMenuItem newFrame = new JMenuItem("New Drumbox");
        JMenuItem load = new JMenuItem("Load Drumbox"); // create Add menu
        JMenuItem clone = new JMenuItem("Clone selected Drumbox"); // create Add menu
        JMenuItem delete = new JMenuItem("Delete all"); // create Add menu
        JMenuItem deleteOne = new JMenuItem("Delete selected"); // create Add menu

        JMenu projMenu = new JMenu("Project"); // create Add menu
        JMenuItem pload = new JMenuItem("Load ..."); // create Add menu
        JMenuItem saveold = new JMenuItem("Save"); // create Add menu
        JMenuItem psave = new JMenuItem("Save as ..."); // create Add menu
        projMenu.add(pload);
        projMenu.add(psave);
        projMenu.add(saveold);
        pload.addActionListener(event -> loadProject());
        psave.addActionListener(event -> saveProject());
        saveold.addActionListener(event -> saveCurrentProject());

        docMenu = new JMenu("Patterns"); // create Add menu

        actionMenu.add(newFrame); // add new frame item to Add menu
        actionMenu.add(load); // add new frame item to Add menu
        actionMenu.add(clone);
        actionMenu.add(new JSeparator());
        actionMenu.add(deleteOne);
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
        deleteOne.addActionListener(event -> deleteOne());
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
        fc.setFileFilter(projectFilter);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                String path = fc.getSelectedFile().getCanonicalPath();
                loadProject(path);
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
        currentProjectPath = filename;
        currentProjectName = filename;
        setTitle(title + " -- " + currentProjectName);

    }

    private void saveCurrentProject()
    {
        if (currentProjectPath == null)
            saveProject();
        else
            saveProject(currentProjectPath);
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
        fc.setFileFilter(projectFilter);
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

        patternList = new JTextField();
        patternList.setToolTipText("Enter Drumbox numbers separated by comma");
        patternList.setBackground(Color.white);
        patternList.setPreferredSize(new Dimension(500, 20));
        p.add(patternList);

        speedAdjust = new JSlider();
        speedAdjust.addChangeListener(e -> speedAdjust.setToolTipText("Speed Adjust: " + getMasterSpeedMultiplier()));
        speedAdjust.setToolTipText("Speed Adjust: " + getMasterSpeedMultiplier());
        speedAdjust.setMinimum(-10);
        speedAdjust.setMaximum(10);
        speedAdjust.setValue(0);
        speedAdjust.setMinorTickSpacing(1);
        speedAdjust.setMajorTickSpacing(2);
        speedAdjust.setPaintTicks(true);
        speedAdjust.setSnapToTicks(true);
        p.add(speedAdjust);

        notesOnly = new JCheckBox();
        notesOnly.setToolTipText("Select this if MIDI file has only note events");
        p.add (notesOnly);

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
        fc.setFileFilter(midiFileFilter);
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

    private float getMasterSpeedMultiplier()
    {
        float speedMult = (float)speedAdjust.getValue()/-3.0f;
        if (speedMult < 0)
            return -1.0f/speedMult;
        return 1.0f + speedMult;
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
        float speedMult = getMasterSpeedMultiplier();
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
                    if (!notesOnly.isSelected())
                    {
                        if (ms instanceof ShortMessage)
                        {
                            ShortMessage sm = (ShortMessage) ms;
                            if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE)
                            {
                                int prg = sm.getData1(); // skip multiple prg change to same prg
                                if (prg == lastprogram)
                                    continue;
                                lastprogram = prg;
                            }
                        }
                    }
                    if (ms.getStatus() == 255) // end of track
                    {
                        continue;
                    }
                    ev.setTick((int)((ev.getTick() + offset)*speedMult));
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

    private void deleteOne()
    {
        JInternalFrame f = currentActiveBox.getMdiClient();
        for (int s=0; s<docMenu.getItemCount(); s++)
        {
            JMenuItem mi = docMenu.getItem(s);
            if (mi.getText().equals(f.getTitle()))
            {
                docMenu.remove(s);
                break;
            }
        }
        theDesktop.remove(f);
        allBoxes.remove(currentActiveBox);
        theDesktop.repaint();
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

