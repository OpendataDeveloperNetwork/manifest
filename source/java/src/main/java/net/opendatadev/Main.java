package net.opendatadev;

import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Conductor;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import io.trasnmogrifier.filter.FileFilters.FileToStringFilter;
import net.opendatadev.filters.StringToManifestFilter;

import java.io.File;

public class Main
{
    public static void main(final String[] argv)
            throws
            FilterException
    {
        final Scope          scope;
        final Transmogrifier transmogrifier;
        final Conductor      conductor;
        final State          state;
        final File           file;
        final String         json;
        final File           rootDir;
        final Manifest       manifest;
        final Pipeline       pipeline;

        file = new File(argv[0]);
        rootDir = new File(argv[1]);

        checkFile(file);
        checkDir(rootDir);

        scope = new Scope();
        scope.addConstant("rootDir", rootDir);
        transmogrifier = new Transmogrifier();
        conductor = new Conductor();
        state = new State(transmogrifier,
                          conductor,
                          scope);

        json = transmogrifier.transform(file,
                                        new FileToStringFilter());
        manifest = transmogrifier.transform(json,
                                            new StringToManifestFilter());
        pipeline = transmogrifier.transform(manifest,
                                            state,
                                            new ManifestToPipelineFilter());
        transmogrifier.transform(pipeline,
                                 state,
                                 conductor);
    }

    private static void checkFile(final File entry)
    {
        checkEntry(entry);

        if(!(entry.isFile()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " is not a file");
        }
    }

    private static void checkDir(final File entry)
    {
        checkEntry(entry);

        if(!(entry.isDirectory()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " is not a directory");
        }
    }

    private static void checkEntry(final File entry)
    {
        if(!(entry.exists()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " does not exist");
        }
    }
}
