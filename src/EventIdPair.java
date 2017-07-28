
class EventIdPair
{
    /**
     * Get ID for KeyOn event
     * @return ID
     */
    public long getKeyOnId ()
    {
        return event_id;
    }

    /**
     * Get ID for KeyOff event
     * @return ID
     */
    public long getKeyOffId ()
    {
        return event_id2;
    }

    /**
     * Get Row number from Event ID
     * @param e Event ID
     * @return Row Number
     */
    public static int getRowNumber (long e)
    {
        return (int) (e / 100);
    }

    /**
     * Get Column number from Event ID
     * @param e
     * @return
     */
    public static int getColumnNumber (long e)
    {
        return (int) (e % 100)/2;
    }

    public static boolean isKeyOnEvent (long e)
    {
        return e%2 == 0;
    }

    private final long event_id;
    private final long event_id2;

    /**
     * Create EventIdPair
     * @param x Number of Button in every  DrumPanel (Column)
     * @param y Number of DrumPanel in DrumBox (Row)
     */
    EventIdPair (int x, int y)
    {
        event_id = y * 100 + x * 2;
        event_id2 = event_id + 1;
    }
}

/*
        for (Long k : noteMap.keySet())
        {
            int linenum = (int) (k / 100);
            int keynum = (int) (k % 100);
            if (keynum % 2 == 0)
            {
                keynum = keynum / 2; // real keynum

 */
