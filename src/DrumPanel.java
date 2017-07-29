import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class DrumPanel extends JPanel
{
    final ArrayList<JToggleButton> toggleButtons = new ArrayList<>();
    private final int linenumber;
    JComboBox combo;
    private Drumbox drumbox;

    DrumPanel (int line, Drumbox parent)
    {
        drumbox = parent;
        linenumber = line;
    }

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
        j.addActionListener(e ->
        {
            for (int s=0; s<toggleButtons.size(); s++)
            {
                EventIdPair ev = new EventIdPair(s, linenumber);
                drumbox.deleteEvent(ev);
                JToggleButton b1 = toggleButtons.get(s);
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
