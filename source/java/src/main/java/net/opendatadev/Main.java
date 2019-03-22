package net.opendatadev;

import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.UnaryFilter;
import io.transmogrifier.conductor.Conductor;
import io.transmogrifier.conductor.Scope;
import io.trasnmogrifier.filter.FileFilters.UnaryFileToStringFilter;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;

import java.io.File;
import java.util.Arrays;

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
        final File                      manifestFile;
        final File                      rootDir;
        final UnaryFilter<File, String> toJSONStringFilter;
        final ManifestFilter            manifestFilter;
        final Transmogrifier            transmogrifier;
        final String                    json;
        final ManifestState             state;
        final Conductor                 conductor;
        final Scope                     scope;

        transmogrifier = new Transmogrifier();
        conductor = new Conductor();
        scope = new Scope();
        rootDir = new File(argv[1]);
        scope.addConstant("rootDir",
                          rootDir);
        state = new ManifestState(transmogrifier,
                                  conductor,
                                  scope);

        state.addManifestListener(new ManifestListener()
        {
            /**
             *
             * @param manifest
             */
            @Override
            public void startingManifest(final Manifest manifest)
            {
                System.out.println("starting " + manifest);
            }

            /**
             *
             * @param manifest
             */
            @Override
            public void finishedManifest(final Manifest manifest)
            {
                System.out.println("finished " + manifest);
            }

            /**
             *
             * @param manifest
             * @param dataset
             */
            @Override
            public void startingDataset(final Manifest manifest,
                                        final Dataset dataset)
            {
                System.out.println("starting " + manifest + " " + dataset.getProvider() + "-" + dataset.getName());
            }

            /**
             *
             * @param manifest
             * @param dataset
             * @param convertedFile
             * @param rawFiles
             */
            @Override
            public void finishedDataset(final Manifest manifest,
                                        final Dataset dataset,
                                        final File convertedFile,
                                        final File... rawFiles)
            {
                System.out.println("finished " + manifest + " " + dataset.getProvider() + "-" + dataset.getName() + " " + convertedFile + " " + Arrays.toString(rawFiles));
            }

            /**
             *
             * @param manifest
             * @param dataset
             * @param download
             */
            @Override
            public void startingDownload(final Manifest manifest,
                                         final Dataset dataset,
                                         final Download download)
            {
                System.out.println("finished " + manifest + " " + dataset.getProvider() + "-" + dataset.getName() + " " + download.getSrc());
            }

            /**
             *
             * @param manifest
             * @param dataset
             * @param download
             * @param rawFile
             */
            @Override
            public void finishedDownload(final Manifest manifest,
                                         final Dataset dataset,
                                         final Download download,
                                         final File rawFile)
            {
                System.out.println("finished " + manifest + " " + dataset.getProvider() + "-" + dataset.getName() + " " + download.getSrc() + " " + rawFile);
            }
        });

        manifestFile = new File(argv[0]);
        toJSONStringFilter = new UnaryFileToStringFilter();
        json = transmogrifier.transform(manifestFile,
                                        toJSONStringFilter);
        manifestFilter = new ManifestFilter();
        transmogrifier.transform(json,
                                 state,
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
