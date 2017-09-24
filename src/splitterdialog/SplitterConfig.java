package splitterdialog;

public class SplitterConfig
{
    final boolean rebase;
    final double speedFactor;
    final int transpose;
    final boolean onlyDrums;
    final String inputFile;
    final String outputDir;
    final boolean chord;
    final boolean dur;

    public SplitterConfig (boolean rebase, double speedFactor,
                           int transposeValue, boolean onlyDrums,
                           String inputFile, String outputDir,
                           boolean chord, boolean dur)
    {
        this.onlyDrums = onlyDrums;
        this.rebase = rebase;
        this.speedFactor = speedFactor;
        this.transpose = transposeValue;
        this.inputFile = inputFile;
        this.outputDir = outputDir;
        this.chord = chord;
        this.dur = dur;
    }
}
