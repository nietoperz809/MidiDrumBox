package splitterdialog;

import javax.sound.midi.*;
import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 7/19/2017.
 */
public class Splitter
{
    private final Sequence template;
    private final HashMap<Integer, Sequence> hm = new HashMap<>();
    private final SplitterConfig cfg;

    /**
     * Contructor
     *
     * @param temp Parent sequence that is used as template
     * @param cfg  Configuration values
     */
    public Splitter (Sequence temp, SplitterConfig cfg)
    {
        template = temp;
        this.cfg = cfg;
    }

    /**
     * Inserts one event into its channel
     * @param evt The event
     * @param channel The channel
     * @throws Exception If smth. gone wrong
     */
    public void insert (MidiEvent evt, int channel) throws Exception
    {
        if (cfg.onlyDrums && channel != 10)
            return;
        Sequence s = hm.get(channel);
        if (s == null)
        {
            Sequence newSeq = new Sequence(template.getDivisionType(), template.getResolution());
            Track t = newSeq.createTrack();
            t.add(evt);
            hm.put(channel, newSeq);
        }
        else
        {
            s.getTracks()[0].add(evt);
        }
    }

    /**
     * Stores all Tracks/Channels as separate MIDI files
     * @param directory Directory where all files will be stored
     * @throws Exception
     */
    public void save (String directory) throws Exception
    {
        int tracknum = 0;
        for (Sequence sequence : hm.values())
        {
            if (cfg.rebase)
                adjustTimebase(sequence);
            if (cfg.speedFactor != 1.0)
                changeSpeed(sequence);
            if (cfg.transpose != 0)
                transposeTrack(sequence);
            if (cfg.chord)
                makeChord(sequence, true);
            else if (cfg.dur)
                makeChord(sequence, false); 
            tracknum++;
            File f = new File(directory + "\\miditrack" + tracknum
                    + "-" + System.currentTimeMillis() + "-.mid");
            MidiSystem.write(sequence, 1, f);
        }
    }

    /**
     * Makes a track twice as fast
     * @param s Sequence that has the track
     */
    private void changeSpeed(Sequence s)
    {
        Track t = s.getTracks()[0];
        for (int i = 0; i < t.size(); i++)
        {
            MidiEvent me = t.get(i);
            double tick = me.getTick();
            me.setTick((long)(tick/cfg.speedFactor));
        }
    }

    /**
     * Make a track beginning at base 0 (time)
     * @param s Sequence that has the track
     */
    private void adjustTimebase(Sequence s)
    {
        long diff = 0;
        Track t = s.getTracks()[0];
        for (int i = 0; i < t.size(); i++)
        {
            MidiEvent me = t.get(i);
            long tick = me.getTick();
            if (i == 0)
                diff = tick;
            me.setTick(tick - diff);
        }
    }

    /**
     * Transpose a track n octaves up or down
     * @param s Sequence that has the track
     * @throws Exception on failure
     */
    private void transposeTrack (Sequence s) throws Exception
    {
        int val = cfg.transpose*12;
        Track t = s.getTracks()[0];
        for (int i = 0; i < t.size(); i++)
        {
            MidiMessage msg = t.get(i).getMessage();
            if (msg instanceof ShortMessage)
            {
                ShortMessage sm = (ShortMessage)msg;
                sm.setMessage(sm.getStatus(),sm.getChannel(),
                        sm.getData1()+val, sm.getData2());
            }
        }
    }

    private void makeChord (Sequence s, boolean moll) throws Exception
    {
        Track t = s.getTracks()[0];
        Track t1 = s.createTrack();
        s.deleteTrack(t);
        for (int i = 0; i < t.size(); i++)
        {
            MidiEvent me = t.get(i);
            MidiMessage msg = me.getMessage();
            if (msg instanceof ShortMessage)
            {
                ShortMessage sm = (ShortMessage)msg;
                long tick = me.getTick();
                int st1 = sm.getStatus();
                int chan = sm.getChannel();
                int dat2 = sm.getData2();
                int d2 = sm.getData1()+3;
                int d3 = d2+3;
                ShortMessage msgd2 = (ShortMessage) sm.clone();
                ShortMessage msgd3 = (ShortMessage) sm.clone();
                if (moll)
                {
                    msgd2.setMessage(st1, chan, sm.getData1() + 3, dat2);
                    msgd3.setMessage(st1, chan, sm.getData1() + 7, dat2);
                }
                else
                {
                    msgd2.setMessage(st1, chan, sm.getData1() + 4, dat2);
                    msgd3.setMessage(st1, chan, sm.getData1() + 7, dat2);
                }
                MidiEvent m2 = new MidiEvent(msgd2, tick);
                MidiEvent m3 = new MidiEvent(msgd3, tick);
                t1.add(me);
                t1.add(m2);
                t1.add(m3);
            }
        }
    }
}
