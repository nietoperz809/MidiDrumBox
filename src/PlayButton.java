import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import java.awt.*;

/**
 * Play control button to play midi sequence
 */
public class PlayButton extends JToggleButton
{
    private static final ImageIcon iconPlay = new ImageIcon(Helper.loadImageFromResource("play.png"));
    private static final ImageIcon iconStop = new ImageIcon(Helper.loadImageFromResource("stop.png"));
    private static Sequencer sequencer;
    private final SequenceProvider sprov;

    /**
     * Contsructor
     * @param s_provider Caller-supplied interface where midi data can be fetched
     */
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
            if (sequencer == null)
                sequencer = MidiSystem.getSequencer();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Start/Stop playback of sequence
     */
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
                setSelected(false);
            }
        }
    }

    /**
     * Set Icon regarding to button state
     * @param b true == isSelected
     */
    public void setSelected (boolean b)
    {
        super.setSelected(b);
        if (!b)
            setIcon (iconPlay);
        else
            setIcon(iconStop);
    }
}
