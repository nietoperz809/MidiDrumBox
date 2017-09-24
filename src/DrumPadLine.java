import sermidi.SerMidEvent;
import sermidi.SerShortMessage;

import javax.sound.midi.ShortMessage;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class DrumPadLine extends JPanel
{
    final ArrayList<JToggleButton> drumPads = new ArrayList<>();
    private final int lineNumber;
    JComboBox instrumentSelector;
    private final Drumbox drumbox;

    /**
     * Constructor
     * @param line Drumpanel line number
     * @param parent Ref to parent Drumbox
     */
    DrumPadLine (int line, Drumbox parent)
    {
        drumbox = parent;
        lineNumber = line;

        this.setBorder(BorderFactory.createEmptyBorder());
        this.setBackground(Color.BLACK);
        FlowLayout la = new FlowLayout(FlowLayout.LEFT, 0, 0);
        this.setLayout(la);

        AtomicInteger instrument = new AtomicInteger(-1);

        JComboBox<String> combo = new JComboBox<>(DrumKit.instrumentNames);
        combo.setSelectedIndex(lineNumber + 8); // begin with base drum
        parent.getInstrument(combo, instrument);
        combo.addActionListener(e ->
        {
            parent.getInstrument(combo, instrument);
            System.out.println("Instrument: " + instrument.get());
        });
        this.addInstrumentSelector(combo);

        JButton clearButton = new JButton("Clear");
        clearButton.setMargin(new Insets(0, 0, 0, 0));
        this.addClearButton(clearButton);

        for (int buttonNo = 0; buttonNo < parent.drumSteps; buttonNo++)
        {
            this.addDrumPad(createToggleButton(buttonNo, lineNumber, instrument));
        }
    }

    /**
     * Create a drum ToggleButton
     *
     * @param buttonNumber Number of button in line (ascending, begins at 0)
     * @param lineNumber   Number of butten line (also 0-based)
     * @param instrument   Instrument number used by this drum line
     * @return The toggle button
     */
    private JToggleButton createToggleButton (int buttonNumber,
                                             int lineNumber,
                                             AtomicInteger instrument)
    {
        JToggleButton jb = new JToggleButton();
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.setMnemonic(buttonNumber);
        jb.setPreferredSize(new Dimension(20, 20));
        jb.addActionListener(e ->
        {
            EventIdPair ev = new EventIdPair(jb.getMnemonic(), lineNumber);
            if (jb.isSelected())
            {
                try
                {
                    int instr = instrument.get();
                    RealtimePlayer.get().play(instr);
                    SerShortMessage on = new SerShortMessage(ShortMessage.NOTE_ON,
                            9, instr, drumbox.volSlider.getValue());
                    SerShortMessage off = new SerShortMessage(ShortMessage.NOTE_OFF,
                            9, instr, 0);
                    drumbox.putEvent(ev.getKeyOnId(), new SerMidEvent(on, buttonNumber));
                    drumbox.putEvent(ev.getKeyOffId(), new SerMidEvent(off, buttonNumber));
                    jb.setToolTipText(DrumPadLine.createTooltipText(instr, drumbox.volSlider.getValue()));
                }
                catch (Exception e1)
                {
                    System.out.println(e1);
                }
            }
            else
            {
                drumbox.deleteEvent(ev);
                jb.setToolTipText(null);
            }
        });
        jb.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        return jb;
    }

    Component addDrumPad (JToggleButton j)
    {
        drumPads.add(j);
        return add(j);
    }


    Component addInstrumentSelector (JComboBox j)
    {
        instrumentSelector = j;
        return add(j);
    }

    Component addClearButton (JButton j)
    {
        j.addActionListener(e ->
        {
            for (int s = 0; s< drumPads.size(); s++)   // Do for all buttons of this line
            {
                EventIdPair ev = new EventIdPair(s, lineNumber);
                drumbox.deleteEvent(ev);
                JToggleButton b1 = drumPads.get(s);
                b1.setSelected(false);
                b1.setToolTipText(null);
            }
        });
        return add(j);
    }

    static String createTooltipText (int instr, int volume)
    {
        return "<html>"+
                DrumKit.instrumentNames[DrumKit.getInstrumentNameIndex(instr)]+
                "<br>Volume: "+volume+
                "</html>";
    }
}
