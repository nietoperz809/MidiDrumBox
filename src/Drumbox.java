import sermidi.SerMidEvent;
import sermidi.SerShortMessage;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class DrumPanel extends JPanel
{
    final ArrayList<JToggleButton> toggleButtons = new ArrayList<>();
    JButton clearButton;
    JComboBox combo;

    Component addToggleButton (JToggleButton j)
    {
        toggleButtons.add(j);
        return add(j);
    }


    Component addComboBox (JComboBox j)
    {
        combo = j;
        return add(j);
    }

    Component addClearButton (JButton j)
    {
        clearButton = j;
        return add(j);
    }
}

public class Drumbox extends JPanel implements Serializable
{
    private static final ImageIcon iconPlay = new ImageIcon(Helper.loadImageFromRessource("play.png"));
    private static final ImageIcon iconStop = new ImageIcon(Helper.loadImageFromRessource("stop.png"));
    private static final int LINES = 10;
    private static final String[] instrumentNames = new String[]
            {
                    "27 High Q (GM2)",
                    "28 Slap (GM2)",
                    "29 Scratch Push (GM2)",
                    "30 Scratch Pull (GM2)",
                    "31 Sticks (GM2)",
                    "32 Square Click (GM2)",
                    "33 Metronome Click (GM2)",
                    "34 Metronome Bell (GM2)",
                    "35 Bass Drum 2",
                    "36 Bass Drum 1",
                    "37 Side Stick",
                    "38 Snare Drum 1",
                    "39 Hand Clap",
                    "40 Snare Drum 2",
                    "41 Low Tom 2",
                    "42 Closed Hi-hat",
                    "43 Low Tom 1",
                    "44 Pedal Hi-hat",
                    "45 Mid Tom 2",
                    "46 Open Hi-hat",
                    "47 Mid Tom 1",
                    "48 High Tom 2",
                    "49 Crash Cymbal 1",
                    "50 High Tom 1",
                    "51 Ride Cymbal 1",
                    "52 Chinese Cymbal",
                    "53 Ride Bell",
                    "54 Tambourine",
                    "55 Splash Cymbal",
                    "56 Cowbell",
                    "57 Crash Cymbal 2",
                    "58 Vibra Slap",
                    "59 Ride Cymbal 2",
                    "60 High Bongo",
                    "61 Low Bongo",
                    "62 Mute High Conga",
                    "63 Open High Conga",
                    "64 Low Conga",
                    "65 High Timbale",
                    "66 Low Timbale",
                    "67 High Agogo",
                    "68 Low Agogo",
                    "69 Cabasa",
                    "70 Maracas",
                    "71 Short Whistle",
                    "72 Long Whistle",
                    "73 Short Guiro",
                    "74 Long Guiro",
                    "75 Claves",
                    "76 High Wood Block",
                    "77 Low Wood Block",
                    "78 Mute Cuica",
                    "79 Open Cuica",
                    "80 Mute Triangle",
                    "81 Open Triangle",
                    "82 Shaker (GM2)",
                    "83 Jingle Bell (GM2)",
                    "84 Belltree (GM2)",
                    "85 Castanets (GM2)",
                    "86 Mute Surdo (GM2)",
                    "87 Open Surdo (GM2)"
            };
    static int instanceNumber = 0;
    private final JSlider noteLength = new JSlider();
    private final Sequencer sequencer;
    private final JInternalFrame mdiClient;
    private final JSlider speedSlider = new JSlider();  // Speed for this pattern
    private final JTextField loopCount = new JTextField();
    private final DrumPanel[] drumPanels = new DrumPanel[LINES];
    private HashMap<Long, SerMidEvent> noteMap = new HashMap<>();   // The event list
    private int drumSteps = 32; // Number of drumSteps
    /**
     * Constructor: Build complete frame and show it
     *
     * @throws Exception If smth. gone wrong
     */
    public Drumbox (JInternalFrame frame) throws Exception
    {
        mdiClient = frame;
        sequencer = MidiSystem.getSequencer();
        setLayout(new GridLayout(LINES + 1, 1));
        for (int s = 0; s < LINES; s++)
        {
            drumPanels[s] = makeDrumLinePanel(s);
            add(drumPanels[s]);
        }
        add(makeControlPanel());
        setVisible(true);
        instanceNumber++;
    }

    /**
     * Constructor that loads a pattern from disk
     *
     * @param frame       Parent frame
     * @param patternPath Path to patern file
     * @throws Exception smth gone wrong
     */
    public Drumbox (JInternalFrame frame, String patternPath) throws Exception
    {
        this(frame);
        loadPattern(patternPath);
    }

    /**
     * Constructor that loads pattern fron an open Objectreader
     * @param frame Parent frame
     * @param reader Objectreader
     * @throws Exception smth failed
     */
    public Drumbox (JInternalFrame frame, ObjectReader reader) throws Exception
    {
        this(frame);
        loadPattern(reader);
    }

    /**
     * returns the parent frame
     * @return
     */
    public JInternalFrame getMdiClient ()
    {
        return mdiClient;
    }

    /**
     * Create Panel for one Instrument
     *
     * @param lineNumber Y-coordinate of panel
     * @return The panel
     */
    private DrumPanel makeDrumLinePanel (int lineNumber)
    {
        DrumPanel panel = new DrumPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

        AtomicInteger instrument = new AtomicInteger(-1);

        JComboBox<String> combo = new JComboBox<>(instrumentNames);
        combo.setSelectedIndex(lineNumber);
        getInstrument(combo, instrument);
        combo.addActionListener(e ->
        {
            getInstrument(combo, instrument);
            System.out.println("Instrument: " + instrument.get());
        });
        panel.addComboBox(combo);

        JButton but = new JButton("Clear");
        but.addActionListener(e ->
        {
            for (JToggleButton b1 : panel.toggleButtons)
            {
                b1.setSelected(false);
            }
        });
        panel.addClearButton(but);

        for (int buttonNo = 0; buttonNo < drumSteps; buttonNo++)
        {
            panel.addToggleButton(createToggleButton(buttonNo, lineNumber, instrument));
        }
        return panel;
    }

    /**
     * Create control panel
     *
     * @return The panel
     */
    private JPanel makeControlPanel ()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        speedSlider.setToolTipText("Track Speed");
        speedSlider.setMinimum(100);
        speedSlider.setMaximum(1000);
        speedSlider.setMinorTickSpacing(25);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);

        noteLength.setToolTipText("Event Length");
        noteLength.setMinimum(50);
        noteLength.setMaximum(1000);
        noteLength.setMinorTickSpacing(25);
        noteLength.setMajorTickSpacing(100);
        noteLength.setPaintTicks(true);
        noteLength.setSnapToTicks(true);

        loopCount.setToolTipText("Loop Count");
        loopCount.setPreferredSize(new Dimension(30, 30));
        loopCount.setText("1");

        JButton b1 = new JButton("Save");
        b1.setToolTipText("Save Pattern to disk");
        panel.add(b1);
        b1.addActionListener(e ->
        {
            final JFileChooser fc = new JFileChooser();
            File f = new File("Drumpattern#" +
                    instanceNumber + "-" +
                    System.currentTimeMillis() + ".drmp");
            fc.setSelectedFile(f);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    String name = fc.getSelectedFile().getCanonicalPath();
                    savePattern(name);
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
        });

        JButton bload = new JButton("Load");
        bload.setToolTipText("Load Pattern from disk");
        panel.add(bload);
        bload.addActionListener(e ->
                loadWithDialog());

        JToggleButton b2 = new JToggleButton();
        b2.setToolTipText("Play/Stop pattern");
        b2.setPressedIcon(iconStop);
        b2.setIcon(iconPlay);
        panel.add(b2);
        b2.addActionListener(e ->
        {
            if (sequencer.isRunning())
            {
                sequencer.stop();
                b2.setIcon(iconPlay);
            }
            else
            {
                b2.setIcon(iconStop);
                Sequence sq = createMIDI();
                try
                {
                    sequencer.addMetaEventListener(meta ->
                    {
                        if (meta.getType() == 47) // All played
                        {
                            b2.setIcon(iconPlay);
                            b2.setSelected(false);
                        }
                    });
                    sequencer.open();
                    sequencer.setSequence(sq);
                    Thread.sleep(100);
                    sequencer.start();
                }
                catch (Exception ex)
                {
                    System.out.println(ex);
                }
            }
        });

        panel.add(speedSlider);
        panel.add(noteLength);

        panel.add(loopCount);

        JButton bplus = new JButton("+");
        bplus.setToolTipText("Increase Pattern");
        bplus.addActionListener(e ->
        {
            if (drumSteps < 32)
            {
                drumSteps++;
                adjustDrumLineLength(drumSteps);
            }
        });
        panel.add(bplus);

        JButton bminus = new JButton("-");
        bminus.setToolTipText("Make Pattern smaller");
        bminus.addActionListener(e ->
        {
            if (drumSteps > 1)
            {
                drumSteps--;
                adjustDrumLineLength(drumSteps);
            }
        });
        panel.add(bminus);

        return panel;
    }

    /**
     * Reads instrument number from selected Combobox item and store into Integer object
     *
     * @param combo      Source
     * @param instrument Destination
     */
    private void getInstrument (JComboBox combo, AtomicInteger instrument)
    {
        int i = readFirstTwo((String) combo.getSelectedItem());
        instrument.set(i);
    }

    /**
     * Create a drum checkbox
     *
     * @param buttonNumber Number of button in line (ascending, begins at 0)
     * @param lineNumber   Number of butten line (also 0-based)
     * @param instrument   Instrument number used by this drum line
     * @return
     */
    private JToggleButton createToggleButton (int buttonNumber,
                                              int lineNumber,
                                              AtomicInteger instrument)
    {
        JToggleButton jb = new JToggleButton();
        jb.setMnemonic(buttonNumber);
        jb.setPreferredSize(new Dimension(20, 20));
        jb.addActionListener(e ->
        {
            long event_id = lineNumber * 100 + jb.getMnemonic() * 2; // key_on is even
            long event_id2 = event_id + 1;  // key_off is odd
            if (jb.isSelected())
            {
                try
                {
                    SerShortMessage on = new SerShortMessage(ShortMessage.NOTE_ON,
                            9, instrument.get(), 127);
                    SerShortMessage off = new SerShortMessage(ShortMessage.NOTE_OFF,
                            9, instrument.get(), 0);
                    putEvent(event_id, new SerMidEvent(on, buttonNumber));
                    putEvent(event_id2, new SerMidEvent(off, buttonNumber));
                }
                catch (Exception e1)
                {
                    System.out.println(e1);
                }
            }
            else
            {
                deleteEvent(event_id);
                deleteEvent(event_id2);
            }
        });
        jb.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        return jb;
    }

    /**
     * Save pattern  to a file
     * @param fname file name
     * @throws Exception any failure
     */
    private void savePattern (String fname) throws Exception
    {
        ObjectWriter w = new ObjectWriter(fname);
        savePattern(w);
        w.close();
    }

    /**
     * Saves one pattern to disk
     * 1. the note map containing all events
     * 2. speed value
     * 3. Loop value
     * 4. drum steps (size of line)
     * 5. note Length
     *
     * @throws Exception smth gone wrong
     */
    public void savePattern (ObjectWriter w)
    {
        w.putObject(noteMap);
        w.putObject(speedSlider.getValue());
        w.putObject(loopCount.getText());
        w.putObject(drumSteps);
        w.putObject(noteLength.getValue());
    }

    /**
     * Initialize this Drumbox from an objectreader
     * @param r the reader
     * @return false if that failed
     */
    public boolean loadFromStream (ObjectReader r)
    {
        try
        {
            loadPattern(r);
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Loads one pattern from disk and initializes the drumbox
     *
     * @throws Exception if smth gone wrong
     */
    private void loadPattern (ObjectReader r) throws Exception
    {
        loadPattern
                ((HashMap<Long, SerMidEvent>) r.getObject(),
                        (Integer) r.getObject(),
                        (String) r.getObject(),
                        (Integer) r.getObject(),
                        (Integer) r.getObject());
    }

    /**
     * Loads a pattern given by variables
     * @param h Hashmap containing the events
     * @param spsl speed slider value
     * @param lpc loop counter (as string)
     * @param steps number of Drumsteps
     * @param nl length of midi event
     */
    private void loadPattern (HashMap<Long, SerMidEvent> h, int spsl,
                              String lpc, int steps, int nl)
    {
        noteMap = h;
        speedSlider.setValue(spsl);
        loopCount.setText(lpc);
        drumSteps = steps;
        noteLength.setValue(nl);
        adjustDrumLineLength(drumSteps);
        // switch buttons on
        //long event_id = lineNumber * 100 + jb.getMnemonic() * 2; // key_on is even
        //long event_id2 = event_id + 1;  // key_off is odd
        for (DrumPanel p : drumPanels)
        {
            p.clearButton.doClick();
        }
        for (Long k : noteMap.keySet())
        {
            int linenum = (int) (k / 100);
            int keynum = (int) (k % 100);
            if (keynum % 2 == 0)
            {
                keynum = keynum / 2; // real keynum
                SerMidEvent ev = noteMap.get(k); // get the event
                int instrument = ((SerShortMessage) ev.getMessage()).getData1();
                DrumPanel p = drumPanels[linenum];
                setInstrument(p.combo, instrument);
                for (JToggleButton c : p.toggleButtons)
                {
                    if (c.getMnemonic() == keynum)
                    {
                        c.setSelected(true);
                        break;
                    }
                }
            }
        }

    }

    /**
     * Disables buttons after val and enables all before val
     *
     * @param val last button which is enabled
     */
    private void adjustDrumLineLength (int val)
    {
        for (DrumPanel p : drumPanels)
        {
            for (int s = 0; s < 32; s++)
            {
                JToggleButton b = p.toggleButtons.get(s);
                if (s < val)
                {
                    b.setVisible(true);
                }
                else
                {
                    b.setVisible(false);
                }
            }
        }
        mdiClient.pack();
    }

    /**
     * Set combobox of drum line to a specific instrument
     *
     * @param combo      The combobox
     * @param instrument MIDI instrument nmber
     */
    private void setInstrument (JComboBox combo, int instrument)
    {
        for (int s = 0; s < instrumentNames.length; s++)
        {
            if (readFirstTwo(instrumentNames[s]) == instrument)
            {
                combo.setSelectedIndex(s);
                break;
            }
        }
    }

    /**
     * Read a number from beginning of string
     *
     * @param in String beginning with number
     * @return The number
     */
    private int readFirstTwo (String in)
    {
        String s = in.substring(0, 2);
        return Integer.parseInt(s);
    }

    /**
     * Initialize this Drumbox from another Drumbox
     * @param src the source drumbox
     */
    public void cloneBox (Drumbox src)
    {
        loadPattern((HashMap<Long, SerMidEvent>)src.noteMap.clone(), src.speedSlider.getValue(),
                    src.loopCount.getText(), src.drumSteps, src.noteLength.getValue());
    }

    public void loadWithDialog ()
    {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Drum Pattern",
                "drmp");
        fc.setFileFilter(filter);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                String filename = fc.getSelectedFile().getCanonicalPath();
                loadPattern(filename);
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

    private void loadPattern (String filename) throws Exception
    {
        ObjectReader r = new ObjectReader(filename);
        loadPattern(r);
        r.close();
    }

    /**
     * Create a new Sequence that can be played or saved
     *
     * @return The sequence
     */
    public Sequence createMIDI ()
    {
        Sequence seq;
        try
        {
            seq = new Sequence(0.0f, 960);
        }
        catch (InvalidMidiDataException e)
        {
            return null;
        }
        Track tr = seq.createTrack();
        for (int s = 0; s < Integer.parseInt(loopCount.getText()); s++)
        {
            for (Map.Entry<Long, SerMidEvent> e : noteMap.entrySet())
            {
                SerMidEvent ev = e.getValue();
                SerShortMessage msg = (SerShortMessage) ev.getMessage();
                MidiEvent clone = new MidiEvent(msg.toShortMessage(), 0);
                long tick = (ev.getTick() + s * drumSteps) * speedSlider.getValue();
                if (msg.getCommand() == ShortMessage.NOTE_ON)
                {
                    clone.setTick(tick);
                }
                else
                {
                    clone.setTick(tick + noteLength.getValue());
                }
                tr.add(clone);
            }
        }
        return seq;
    }

    /**
     * Put event into hashmap and track
     *
     * @param key Event key
     * @param ev  The Event himself
     */
    private void putEvent (long key, SerMidEvent ev)
    {
        System.out.println("put: " + key);
        noteMap.put(key, ev);
    }

    /**
     * Delete Event from hashmap and track
     *
     * @param key Event key
     */
    private void deleteEvent (long key)
    {
        SerMidEvent e1 = noteMap.remove(key);
        if (e1 != null)
        {
            System.out.println("del: " + key);
        }
    }

    /**
     * Return speed base of this Drumbox
     *
     * @return The speed value
     */
    public int getSliderValue ()
    {
        return speedSlider.getValue();
    }
}
