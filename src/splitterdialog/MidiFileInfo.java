package splitterdialog;/*
 *	MidiFileInfo.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999, 2000 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.*;
import java.io.File;


/**	<titleabbrev>MidiFileInfo</titleabbrev>
 <title>Getting information about a MIDI file</title>

 <formalpara><title>Purpose</title>
 <para>Displays general information about a MIDI file:
 MIDI file type, division type, timing resolution, length (in ticks)
 and duration (in &mu;s)</para>
 </formalpara>

 <formalpara><title>Usage</title>
 <para>
 <cmdsynopsis><command>java MidiFileInfo</command>
 <group>
 <arg><option>-f</option></arg>
 <arg><option>-u</option></arg>
 <arg><option>-s</option></arg>
 </group>
 <arg><option>-i</option></arg>
 <arg><replaceable>midifile</replaceable></arg>
 </cmdsynopsis>
 </para>
 </formalpara>

 <formalpara><title>Parameters</title>
 <variablelist>
 <varlistentry>
 <term><option>-s</option></term>
 <listitem><para>use standard input as source for the MIDI file. If this option is given, <replaceable class="parameter">midifile</replaceable> is not required.</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-f</option></term>
 <listitem><para>interpret <replaceable class="parameter">midifile</replaceable> as filename. This is the default. If this option is given, <replaceable class="parameter">midifile</replaceable> is required.</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-u</option></term>
 <listitem><para>interpret <replaceable class="parameter">midifile</replaceable> as URL. If this option is given, <replaceable class="parameter">midifile</replaceable> is required.</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-i</option></term>
 <listitem><para>display additional information</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><replaceable class="parameter">midifile</replaceable></term>
 <listitem><para>the filename  or URL of the MIDI
 file that should be used</para></listitem>
 </varlistentry>
 </variablelist>
 </formalpara>

 <formalpara><title>Bugs, limitations</title>
 <para>
 Some combination of options do not work. With Sun's
 implementation tick and microsecond length are only shown with option <option>-i</option>. With <ulink url="http://www.tritonus.org/">Tritonus</ulink>, length and duration cannot be displayed at all.
 </para></formalpara>

 <formalpara><title>Source code</title>
 <para>
 <ulink url="MidiFileInfo.java.html">MidiFileInfo.java</ulink>
 </para>
 </formalpara>

 */
public class MidiFileInfo
{
    private static JTextArea m_out;

    public MidiFileInfo (JTextArea output)
    {
        m_out = output;
        m_out.setText("");  // clear
    }

    public void doIt (String strSource) throws Exception
    {
        Sequence sequence = null;
        File file = new File(strSource);
        MidiFileFormat fileFormat = MidiSystem.getMidiFileFormat(file);
        String strFilename = file.getCanonicalPath();
        sequence = MidiSystem.getSequence(file);

        out("---------------------------------------------------------------------------");
        out("Source: " + strFilename);
        out("Midi File Type: " + fileFormat.getType());
        out ("Number of Tracks: " + sequence.getTracks().length);
        float	fDivisionType = fileFormat.getDivisionType();
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

        String strResolutionType = null;
        if (fileFormat.getDivisionType() == Sequence.PPQ)
        {
            strResolutionType = " ticks per beat";
        }
        else
        {
            strResolutionType = " ticks per frame";
        }
        out("Resolution: " + fileFormat.getResolution() + strResolutionType);

        String strFileLength = null;
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



/*** MidiFileInfo.java ***/