import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Drumbox extends JPanel
{
    private static final ImageIcon iconPlay = new ImageIcon(Helper.loadImageFromRessource("play.png"));
    private static final ImageIcon iconStop = new ImageIcon(Helper.loadImageFromRessource("stop.png"));
    private final Sequencer sequencer;
    private final JInternalFrame mdiClient;
    private final JSlider speedSlider = new JSlider();  // Speed for this pattern
    private final JTextField loopCount = new JTextField();
    private int drumSteps = 32; // Number of drumSteps
    private static final int LINES = 10;
    private JPanel[] drumPanels = new JPanel[LINES];
    private static final int NOTELENGTH = 40;
    static int instanceNumber = 0;
    int thisInstNumber;
    private final HashMap<Long,MidiEvent> noteMap = new HashMap<>();
    static private final String[] instrumentNames = new String[]
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

    public int getInstanceNumber ()
    {
        return thisInstNumber;
    }
    public int getSliderValue()
    {
        return speedSlider.getValue();
    }


    /**
     * Constructor: Build complete frame and show it
     * @throws Exception If smth. gone wrong
     */
    public Drumbox (JInternalFrame frame) throws Exception
    {
        mdiClient = frame;
        sequencer = MidiSystem.getSequencer();
        setLayout(new GridLayout(11,1));
        for (int s=0; s<LINES; s++)
        {
            drumPanels[s] = makeDrumLinePanel(s);
            add(drumPanels[s]);
        }
        add (makeControlPanel());
        setVisible(true);
        thisInstNumber = instanceNumber;
        instanceNumber++;
    }

    private void hideShowButtonRow (int s, boolean show)
    {
        for (JPanel p : drumPanels)
        {
            Component[] comps = p.getComponents();
            for (Component c : comps)
            {
                if (c instanceof JToggleButton)
                {
                    JToggleButton tb = (JToggleButton) c;
                    if (tb.getMnemonic() == s)
                    {
                        tb.setVisible(show);
                        break;
                    }
                }
            }
        }
        mdiClient.pack();
    }

    private JToggleButton createToggleButton (int buttonNumber,
                                              int lineNumber,
                                              int instrument)
    {
        JToggleButton jb = new JToggleButton();
        jb.setMnemonic(buttonNumber);
        jb.setPreferredSize(new Dimension(20,20));
        jb.addActionListener(e ->
        {
            long event_id = lineNumber*100+jb.getMnemonic()*2;
            long event_id2 = event_id+1;
            if (jb.isSelected())
            {
                try
                {
                    ShortMessage on = new ShortMessage(ShortMessage.NOTE_ON,
                            9, instrument, 127);
                    ShortMessage off = new ShortMessage(ShortMessage.NOTE_OFF,
                            9, instrument, 0);
                    putEvent (event_id, new MidiEvent(on, jb.getMnemonic()));
                    putEvent (event_id2, new MidiEvent(off, jb.getMnemonic()));
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
     * Create Panel for one Instrument
     * @param lineNumber Y-coordinate of panel
     * @return The panel
     */
    private JPanel makeDrumLinePanel (int lineNumber)
    {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));

        AtomicInteger instrument = new AtomicInteger(-1);
        JComboBox<String> combo = new JComboBox<>(instrumentNames);
        combo.setSelectedIndex(lineNumber);
        readFirstTwo(combo, instrument);
        panel.add(combo);
        combo.addActionListener(e ->
        {
            readFirstTwo(combo, instrument);
        });

        JButton but = new JButton("Clear");
        panel.add(but);
        but.addActionListener(e ->
        {
            for (int i = 2; i<(drumSteps +2); i++)
            {
                JToggleButton b1 = (JToggleButton)panel.getComponent(i);
                if (b1.isSelected())
                    b1.doClick();
            }
        });

        for (int buttonNo = 0; buttonNo < drumSteps; buttonNo++)
        {
            panel.add(createToggleButton (buttonNo, lineNumber, instrument.get()));
        }
        return panel;
    }

    /**
     * Create control panel
     * @return The panel
     */
    private JPanel makeControlPanel ()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        speedSlider.setMinimum(100);
        speedSlider.setMaximum(1000);
        speedSlider.setMinorTickSpacing(25);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);

        loopCount.setPreferredSize(new Dimension(30,30));
        loopCount.setText("1");

        JButton b1 = new JButton("Midi");
        panel.add(b1);
        b1.addActionListener(e ->
        {
            Sequence sq = createMIDI();
            File f = new File("c:\\midfile.mid");
            try
            {
                MidiSystem.write(sq, 1, f);
                JOptionPane.showMessageDialog(this, "Saved to: "+f.getAbsolutePath(), "Drum Tool", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e1)
            {
                JOptionPane.showMessageDialog(this, "Saving fail: "+e1, "Drum Tool", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton b2 = new JButton();
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
                    sequencer.addMetaEventListener(new MetaEventListener()
                    {
                        @Override
                        public void meta (MetaMessage meta)
                        {
                            if (meta.getType() == 47)
                            {
                                b2.setIcon(iconPlay);
                            }
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

        panel.add (new JLabel("Loop:"));
        panel.add (loopCount);

        JButton bplus = new JButton("+");
        bplus.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent e)
            {
                if (drumSteps < 32)
                {
                    hideShowButtonRow(drumSteps, true);
                    drumSteps++;
                }
            }
        });
        JButton bminus = new JButton("-");
        bminus.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent e)
            {
                if (drumSteps > 1)
                {
                    drumSteps--;
                    hideShowButtonRow(drumSteps, false);
                }
            }
        });
        panel.add (bminus);
        panel.add (bplus);

        return panel;
    }

    /**
     * Reads instrument number from selected Combobox item and store into Integer object
     * @param combo Source
     * @param instrument Destination
     */
    private void readFirstTwo(JComboBox combo, AtomicInteger instrument)
    {
        String s = ((String)combo.getSelectedItem()).substring(0,2);
        instrument.set(Integer.parseInt(s));
    }

    /**
     * Put event into hashmap and track
     * @param key Event key
     * @param ev The Event himself
     */
    private void putEvent (long key, MidiEvent ev)
    {
        System.out.println("put: "+key);
        noteMap.put(key, ev);
    }

    /**
     * Delete Event from hashmap and track
     * @param key Event key
     */
    private void deleteEvent (long key)
    {
        MidiEvent e1 = noteMap.remove(key);
        if (e1 != null)
        {
            System.out.println("del: "+key);
        }
    }

    /**
     * Create a new Sequence that can be played or saved
     * @return The sequence
     */
    public Sequence createMIDI()
    {
        Sequence seq = null;
        try
        {
            seq = new Sequence(0.0f,960);
        }
        catch (InvalidMidiDataException e)
        {
            return null;
        }
        Track tr = seq.createTrack();
        for (int s=0; s<Integer.parseInt(loopCount.getText()); s++)
        {
            for (Map.Entry<Long, MidiEvent> e : noteMap.entrySet())
            {
                MidiEvent ev = e.getValue();
                ShortMessage msg = (ShortMessage) ev.getMessage();
                MidiEvent clone = new MidiEvent(msg, 0);
                long tick = (ev.getTick() + s * drumSteps) * speedSlider.getValue();
                if (msg.getCommand() == ShortMessage.NOTE_ON)
                {
                    clone.setTick(tick);
                }
                else
                {
                    clone.setTick(tick + NOTELENGTH);
                }
                tr.add(clone);
            }
        }
        return seq;
    }
}
