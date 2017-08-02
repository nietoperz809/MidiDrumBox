import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import java.awt.*;

public class PlayButton extends JToggleButton
{
    private static final ImageIcon iconPlay = new ImageIcon(Helper.loadImageFromResource("play.png"));
    private static final ImageIcon iconStop = new ImageIcon(Helper.loadImageFromResource("stop.png"));
    private Sequencer sequencer;
    private SequenceProvider sprov;

    public PlayButton (SequenceProvider s_provider)
    {
        super();
        sprov = s_provider;

        setMargin(new Insets(1, 1, 1, 1));
        setPressedIcon(iconStop);
        setIcon(iconPlay);

        this.addActionListener(e ->
        {
            if (isSelected())
                setIcon (iconStop);
            else
                setIcon (iconPlay);
        });

        this.addActionListener(e -> playButtonClicked());

        try
        {
            sequencer = MidiSystem.getSequencer();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void playButtonClicked ()
    {
        if (sequencer.isRunning())
        {
            sequencer.stop();
        }
        else
        {
            try
            {
                Sequence sq = sprov.createMIDI();
                sequencer.addMetaEventListener(meta ->
                {
                    if (meta.getType() == 47) // end of track
                    {
                        setSelected(false);
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
    }

    public void setSelected (boolean b)
    {
        super.setSelected(b);
        if (!b)
            setIcon (iconPlay);
        else
            setIcon(iconStop);
    }
}
