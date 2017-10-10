package splitterdialog;

import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.*;
import java.io.File;

class MidiFileInfo
{
    private static JTextArea m_out;

    public MidiFileInfo (JTextArea output)
    {
        m_out = output;
        m_out.setText("");  // clear
    }

    /**
     * Show file Info
     * @param strSource Source path
     * @throws Exception Smth gone wrong
     */
    public void doIt (String strSource) throws Exception
    {
        File file = new File(strSource);
        MidiFileFormat fileFormat = MidiSystem.getMidiFileFormat(file);
        String strFilename = file.getCanonicalPath();
        Sequence sequence = MidiSystem.getSequence(file);
        int numTracks = sequence.getTracks().length;
        int type = fileFormat.getType();

        out("---------------------------------------------------------------------------");
        out("Source: " + strFilename);
        out("Midi File Type: " + type);
        if (type == 0)
            out ("May contain multiple tracks in track 0");
        out ("Number of Tracks: " + numTracks);
        float fDivisionType = fileFormat.getDivisionType();
        String strDivisionType = null;
        if (fDivisionType == Sequence.PPQ)
        {
            strDivisionType = "PPQ";
        }
        else if (fDivisionType == Sequence.SMPTE_24)
        {
            strDivisionType = "SMPTE, 24 frames per second";
        }
        else if (fDivisionType == Sequence.SMPTE_25)
        {
            strDivisionType = "SMPTE, 25 frames per second";
        }
        else if (fDivisionType == Sequence.SMPTE_30DROP)
        {
            strDivisionType = "SMPTE, 29.97 frames per second";
        }
        else if (fDivisionType == Sequence.SMPTE_30)
        {
            strDivisionType = "SMPTE, 30 frames per second";
        }

        out("DivisionType: " + strDivisionType);

        String strResolutionType;
        if (fileFormat.getDivisionType() == Sequence.PPQ)
        {
            strResolutionType = " ticks per beat";
        }
        else
        {
            strResolutionType = " ticks per frame";
        }
        out("Resolution: " + fileFormat.getResolution() + strResolutionType);

        String strFileLength;
        if (fileFormat.getByteLength() != MidiFileFormat.UNKNOWN_LENGTH)
        {
            strFileLength = "" + fileFormat.getByteLength() + " bytes";
        }
        else
        {
            strFileLength = "unknown";
        }
        out("Length: " + strFileLength);

        out("Length: " + sequence.getTickLength() + " ticks (= " + (double)sequence.getMicrosecondLength()/1000000.0 + " seconds)");
        out("---------------------------------------------------------------------------");
    }

    private static void out(String strMessage)
    {
        m_out.append (strMessage+"\n");
        m_out.setCaretPosition(m_out.getText().length() - 1);
    }
}



/* MidiFileInfo.java */