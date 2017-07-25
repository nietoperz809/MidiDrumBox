package sermidi;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.Serializable;

/**
 * MIDI events contain a MIDI message and a corresponding time-stamp
 * expressed in ticks, and can represent the MIDI event information
 * stored in a MIDI file or a <code>{@link Sequence}</code> object.  The
 * duration of a tick is specified by the timing information contained
 * in the MIDI file or <code>Sequence</code> object.
 * <p>
 * In Java Sound, <code>MidiEvent</code> objects are typically contained in a
 * <code>{@link Track}</code>, and <code>Tracks</code> are likewise
 * contained in a <code>Sequence</code>.
 *
 *
 * @author David Rivas
 * @author Kara Kytle
 */
public class SerMidEvent implements Serializable
{

    static final long serialVersionUID = 1L;
    // Instance variables

    /**
     * The MIDI message for this event.
     */
    private final SerMidMessage message;


    /**
     * The tick value for this event.
     */
    private long tick;


    /**
     * Constructs a new <code>MidiEvent</code>.
     * @param message the MIDI message contained in the event
     * @param tick the time-stamp for the event, in MIDI ticks
     */
    public SerMidEvent(SerMidMessage message, long tick) {

        this.message = message;
        this.tick = tick;
    }

    /**
     * Obtains the MIDI message contained in the event.
     * @return the MIDI message
     */
    public SerMidMessage getMessage() {
        return message;
    }


    /**
     * Sets the time-stamp for the event, in MIDI ticks
     * @param tick the new time-stamp, in MIDI ticks
     */
    public void setTick(long tick) {
        this.tick = tick;
    }


    /**
     * Obtains the time-stamp for the event, in MIDI ticks
     * @return the time-stamp for the event, in MIDI ticks
     */
    public long getTick() {
        return tick;
    }
}
