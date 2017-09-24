
class EventIdPair
{
    /**
     * Get ID for KeyOn event
     * @return ID
     */
    public long getKeyOnId ()
    {
        return eventOnId;
    }

    /**
     * Get ID for KeyOff event
     * @return ID
     */
    public long getKeyOffId ()
    {
        return eventOffId;
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
     * @param e Event ID
     * @return Col Number
     */
    public static int getColumnNumber (long e)
    {
        return (int) (e % 100)/2;
    }

    public static boolean isKeyOnEvent (long e)
    {
        return e%2 == 0;
    }

    private final long eventOnId;
    private final long eventOffId;

    /**
     * Create EventIdPair
     * @param x Number of Button in every  DrumPadLine (Column)
     * @param y Number of DrumPanels in DrumBox (Row)
     */
    EventIdPair (int x, int y)
    {
        eventOnId = y * 100 + x * 2;
        eventOffId = eventOnId + 1;
    }
}
