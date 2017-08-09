import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class DrumLinePanel extends JPanel
{
    final ArrayList<JToggleButton> drumPads = new ArrayList<>();
    private final int lineNumber;
    JComboBox instrumentSelector;
    private final Drumbox drumbox;

    /**
     * Constructor
     * @param line Drumpanel number
     * @param parent Ref to parent Drumbox
     */
    DrumLinePanel (int line, Drumbox parent)
    {
        drumbox = parent;
        lineNumber = line;
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
