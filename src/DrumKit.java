
public class DrumKit
{
    static final String[] kitnames = new String[]
            {
                    "1  Standard Kit",
                    "9  Room Kit",
                    "17 Power Kit",
                    "25 Electronic Kit",
                    "26 TR-808 Kit",
                    "33 Jazz Kit",
                    "41 Brush Kit",
                    "49 Orchestra Kit",
                    "57 Sound FX Kit",
                    "128 Percussion"
            };

    static final String[] instrumentNames = new String[]
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

    /**
     * Get idx into instrument array given instrument MIDI code
     * @param instrument the MIDI number
     * @return array index
     */
    public static int getInstrumentNameIndex (int instrument)
    {
        for (int s = 0; s < instrumentNames.length; s++)
        {
            if (readNumber(instrumentNames[s]) == instrument)
            {
                return s;
            }
        }
        System.out.println("wrong instrument");
        return -1;
    }

    /**
     * Read a number from beginning of string
     *
     * @param in String beginning with number
     * @return The number
     */
    public static int readNumber (String in)
    {
        String s = in.substring(0, 3).trim();
        return Integer.parseInt(s);
    }
}
