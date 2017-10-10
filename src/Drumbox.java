import sermidi.SerMidEvent;
import sermidi.SerShortMessage;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Drumbox extends JPanel implements Serializable, SequenceProvider
{
    private static final int LINES = 10;
    private static int instanceNumber = 0;
    private final JSlider noteLengthSlider = new JSlider();
    final JSlider volSlider = new JSlider();
   // private final Sequencer sequencer;
    private final JInternalFrame mdiClient;
    private final JSlider speedSlider = new JSlider();  // Speed for this pattern
    private final JTextField loopCount = new JTextField();
    private final DrumPadLine[] drumPanels = new DrumPadLine[LINES];
    private final FileNameExtensionFilter drumBoxFileFilter = new FileNameExtensionFilter("Drum Pattern",
            "drmp");
    private HashMap<Long, SerMidEvent> eventMap = new HashMap<>();   // The event list
    int drumSteps = 32; // Number of drumSteps
    private JComboBox<String> drumKits;

    /**
     * Constructor: Build complete frame and show it
     *
     * @throws Exception If smth. gone wrong
     */
    public Drumbox (JInternalFrame frame)
    {
        mdiClient = frame;
        GridLayout gl = new GridLayout(LINES + 1, 1, 0, 0);
        this.setLayout(gl);
        //sequencer = MidiSystem.getSequencer();
        for (int s = 0; s < LINES; s++)
        {
            drumPanels[s] = new DrumPadLine(s, this);
            this.add(drumPanels[s]);
        }
        this.add(makeControlPanel());
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
     *
     * @param frame  Parent frame
     * @param reader Objectreader
     * @throws Exception smth failed
     */
    public Drumbox (JInternalFrame frame, ObjectReader reader)
    {
        this(frame);
        loadPattern(reader);
    }

    /**
     * Get the parent frame
     *
     * @return parent frame
     */
    public JInternalFrame getMdiClient ()
    {
        return mdiClient;
    }

    /**
     * Create control panel
     *
     * @return The panel
     */
    private JPanel makeControlPanel ()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setPreferredSize(new Dimension(200, 30));

        JButton bplus = new JButton("+");
        bplus.setMargin(new Insets(0, 5, 0, 5));
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
        bminus.setMargin(new Insets(0, 5, 0, 5));
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

        drumKits = new JComboBox<>(DrumKit.drumKitNames);
        drumKits.addActionListener(e ->
        {
            int kit = DrumKit.readNumber((String) drumKits.getSelectedItem());
            RealtimePlayer.get().setInstrument(kit);
        });
        panel.add(drumKits);

        speedSlider.setMinimum(50);
        speedSlider.setMaximum(1000);
        speedSlider.setMinorTickSpacing(25);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setToolTipText("Track Speed:" + speedSlider.getValue());
        speedSlider.addChangeListener(e ->
                Helper.showToolTip(speedSlider, "Speed"));

        noteLengthSlider.setToolTipText("Event Length");
        loopCount.setPreferredSize(new Dimension(100, 20));
        noteLengthSlider.setMinimum(5);
        noteLengthSlider.setMaximum(1000);
        noteLengthSlider.setMinorTickSpacing(25);
        noteLengthSlider.setMajorTickSpacing(100);
        noteLengthSlider.setPaintTicks(true);
        noteLengthSlider.setSnapToTicks(true);
        noteLengthSlider.addChangeListener(e ->
                Helper.showToolTip(noteLengthSlider, "Note Length"));

        volSlider.setToolTipText("Event Volume");
        volSlider.setMinimum(0);
        volSlider.setMaximum(127);
        volSlider.setMinorTickSpacing(4);
        volSlider.setMajorTickSpacing(16);
        volSlider.setPaintTicks(true);
        volSlider.setSnapToTicks(true);
        volSlider.setValue(127);
        volSlider.addChangeListener(e ->
                Helper.showToolTip(volSlider, "Volume"));

        loopCount.setPreferredSize(new Dimension(20, 20));
        loopCount.setToolTipText("Loop Count");
        loopCount.setText("1");

        JButton bsave = new JButton("Save");
        bsave.setMargin(new Insets(1, 1, 1, 1));
        bsave.setToolTipText("Save Pattern to disk");
        panel.add(bsave);
        bsave.addActionListener(e -> saveWithDialog());

        JButton bload = new JButton("Load");
        bload.setMargin(new Insets(1, 1, 1, 1));
        bload.setToolTipText("Load Pattern from disk");
        panel.add(bload);
        bload.addActionListener(e ->
                loadWithDialog());

        JToggleButton b2 = new PlayButton(this);
        b2.setToolTipText("Play/Stop Pattern");
        panel.add(b2);
        //b2.addActionListener(e -> playButtonClicked(b2));
        panel.add(speedSlider);
        panel.add(noteLengthSlider);
        panel.add(volSlider);
        panel.add(loopCount);

        JButton random = new JButton("RND");
        random.setToolTipText("Random Pattern");
        random.addActionListener(e -> {
            int loops = (int)Math.sqrt(LINES*drumSteps);
            for (int s=0; s<loops; s++)
            {
                int y = (int) (Math.random() * LINES);
                int x = (int) (Math.random() * drumSteps);
                JToggleButton jb = drumPanels[y].drumPads.get(x);
                jb.doClick();
            }
        });
        panel.add (random);

        return panel;
    }


    /**
     * Save button clicked
     */
    private void saveWithDialog ()
    {
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(drumBoxFileFilter);
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
    }

    /**
     * Reads instrument number from selected Combobox item and store into Integer object
     *
     * @param combo      Source
     * @param instrument Destination
     */
    public void getInstrument (JComboBox combo, AtomicInteger instrument)
    {
        int i = DrumKit.readNumber((String) combo.getSelectedItem());
        instrument.set(i);
    }

    /**
     * Save pattern  to a file
     *
     * @param fname file name
     * @throws Exception any failure
     */
    private void savePattern (String fname)
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
     * 6. Volume
     * 7. Drum kit
     */
    public void savePattern (ObjectWriter w)
    {
        w.putObject(eventMap);
        w.putObject(speedSlider.getValue());
        w.putObject(loopCount.getText());
        w.putObject(drumSteps);
        w.putObject(noteLengthSlider.getValue());
        w.putObject(volSlider.getValue());
        w.putObject(drumKits.getSelectedIndex());
    }

    /**
     * Initialize this Drumbox from an objectreader
     *
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
    private void loadPattern (ObjectReader r)
    {
        loadPattern
                ((HashMap<Long, SerMidEvent>) r.getObject(),
                        (Integer) r.getObject(), // speed
                        (String) r.getObject(),  // loop count
                        (Integer) r.getObject(), // no of steps
                        (Integer) r.getObject(), // note length
                        (Integer) r.getObject(),  // volume
                        (Integer) r.getObject()); // drumkits
    }

    /**
     * Loads a pattern given by variables
     *
     * @param eventHashMap      Hashmap containing the events
     * @param speedSliderValue  speed slider value
     * @param loopCounterString loop counter (as string)
     * @param steps             number of Drumsteps
     * @param eventLength       length of midi event
     */
    private void loadPattern (HashMap<Long, SerMidEvent> eventHashMap, int speedSliderValue,
                              String loopCounterString, int steps, int eventLength,
                              int eventVolume, int drumset)
    {
        speedSlider.setValue(speedSliderValue);
        loopCount.setText(loopCounterString);
        drumSteps = steps;
        noteLengthSlider.setValue(eventLength);
        volSlider.setValue(eventVolume);
        drumKits.setSelectedIndex(drumset);
        adjustDrumLineLength(drumSteps);
        // switch buttons off
//        for (DrumPadLine p : drumPanels)
//        {
//            p.clearButton.doClick();
//        }
        eventMap = eventHashMap;
        for (Long k : eventMap.keySet())
        {
            int linenum = EventIdPair.getRowNumber(k);
            int keynum = EventIdPair.getColumnNumber(k);
            if (EventIdPair.isKeyOnEvent(k)) // get instrument from keyon event
            {
                SerMidEvent ev = eventMap.get(k); // get the event
                int instrument = ((SerShortMessage) ev.getMessage()).getData1();
                int volume = ((SerShortMessage) ev.getMessage()).getData2();
                DrumPadLine panel = drumPanels[linenum];
                setInstrument(panel.instrumentSelector, instrument);
                for (JToggleButton toggleButton : panel.drumPads)
                {
                    if (toggleButton.getMnemonic() == keynum)
                    {
                        toggleButton.setSelected(true);
                        toggleButton.setToolTipText(
                                DrumPadLine.createTooltipText(instrument, volume));
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
        for (DrumPadLine p : drumPanels)
        {
            for (int s = 0; s < 32; s++)
            {
                p.drumPads.get(s).setVisible(s < val);
            }
        }
        //mdiClient.pack();
    }

    /**
     * Set combobox of drum line to a specific instrument
     *
     * @param combo      The combobox
     * @param instrument MIDI instrument nmber
     */
    private void setInstrument (JComboBox combo, int instrument)
    {
        combo.setSelectedIndex(DrumKit.getInstrumentNameIndex(instrument));
    }

    /**
     * Initialize this Drumbox from another Drumbox
     *
     * @param src the source drumbox
     */
    public void cloneBox (Drumbox src)
    {
        loadPattern((HashMap<Long, SerMidEvent>) src.eventMap.clone(), src.speedSlider.getValue(),
                src.loopCount.getText(), src.drumSteps, src.noteLengthSlider.getValue(),
                src.volSlider.getValue(), src.drumKits.getSelectedIndex());
    }

    /**
     * Open a file select dialog before loading pattern
     */
    public void loadWithDialog ()
    {
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(drumBoxFileFilter);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                String filename = fc.getSelectedFile().getCanonicalPath();
                loadPattern(filename);
            }
            catch (Exception e1)
            {
                System.out.println(e1);
            }
        }
        else
        {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void loadPattern (String filename)
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
        try
        {
            Sequence seq = new Sequence(0.0f, 960);
            Track tr = seq.createTrack();
            // ---------------------------------
            int kit = DrumKit.readNumber((String) drumKits.getSelectedItem());
            ShortMessage prog = new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                    9, kit - 1, 0);
            tr.add(new MidiEvent(prog, 0));
            //---------------------------------
            for (int s = 0; s < Integer.parseInt(loopCount.getText()); s++)
            {
                for (Map.Entry<Long, SerMidEvent> e : eventMap.entrySet())
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
                        clone.setTick(tick + noteLengthSlider.getValue());
                    }
                    tr.add(clone);
                }
            }
            return seq;
        }
        catch (InvalidMidiDataException e)
        {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Put event into hashmap and track
     *
     * @param key Event key
     * @param ev  The Event himself
     */
    void putEvent (long key, SerMidEvent ev)
    {
        System.out.println("put: " + key);
        eventMap.put(key, ev);
    }

    /**
     * Delete Event from hashmap and track
     *
     * @param key Event key
     */
    private void deleteEvent (long key)
    {
        SerMidEvent e1 = eventMap.remove(key);
        if (e1 != null)
        {
            System.out.println("del: " + key);
        }
    }

    public void deleteEvent (EventIdPair ep)
    {
        deleteEvent(ep.getKeyOnId());
        deleteEvent(ep.getKeyOffId());
    }

    /**
     * Return speed base of this Drumbox
     *
     * @return The speed value
     */
    public int getSpeedValue ()
    {
        return speedSlider.getValue();
    }
}
