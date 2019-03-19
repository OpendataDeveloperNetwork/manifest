package net.opendatadev;

import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.UnaryFilter;
import io.transmogrifier.VerboseTransmogrifier;
import io.trasnmogrifier.filter.FileFilters.UnaryFileToStringFilter;

import java.io.File;

/**
 *
 */
public class Main
{
    /**
     * @param argv
     * @throws FilterException
     */
    public static void main(final String[] argv)
            throws
            FilterException
    {
        final File                      file;
        final File                      rootDir;
        final UnaryFilter<File, String> toJSONStringFilter;
        final ManifestFilter<File>      manifestFilter;
        final Transmogrifier            transmogrifier;

        file = new File(argv[0]);
        rootDir = new File(argv[1]);
        toJSONStringFilter = new UnaryFileToStringFilter();
        transmogrifier = new Transmogrifier();
        manifestFilter = new ManifestFilter<>(transmogrifier,
                                              rootDir);
        transmogrifier.transform(file,
                                 toJSONStringFilter,
                                 manifestFilter);
    }

    /**
     * @param entry
     */
    private static void checkFile(final File entry)
    {
        checkEntry(entry);

        if(!(entry.isFile()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " is not a file");
        }
    }

    /**
     * @param entry
     */
    private static void checkDir(final File entry)
    {
        checkEntry(entry);

        if(!(entry.isDirectory()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " is not a directory");
        }
    }

    /**
     * @param entry
     */
    private static void checkEntry(final File entry)
    {
        if(!(entry.exists()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " does not exist");
        }
    }
}
