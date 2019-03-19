package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.UnaryFilter;
import io.transmogrifier.conductor.Conductor;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import net.opendatadev.filters.StringToManifestFilter;

import java.io.File;

public class ManifestFilter<T>
        implements Filter<T, UnaryFilter<T, String>, Void>
{
    private final Transmogrifier transmogrifier;
    private final File           rootDir;

    public ManifestFilter(final Transmogrifier t,
                          final File dir)
    {
        checkDir(dir);
        transmogrifier = t;
        rootDir = dir;
    }

    /**
     * @param entry
     */
    private static void checkDir(final File entry)
    {
        if(!(entry.exists()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " does not exist");
        }

        if(!(entry.isDirectory()))
        {
            throw new RuntimeException(entry.getAbsolutePath() + " is not a directory");
        }
    }

    @Override
    public Void perform(final T manifestSource,
                        final UnaryFilter<T, String> toJSONStringFilter)
            throws
            FilterException
    {
        final Scope     scope;
        final Conductor conductor;
        final State     state;
        final String    json;
        final Manifest  manifest;
        final Pipeline  pipeline;

        json = transmogrifier.transform(manifestSource,
                                        toJSONStringFilter);
        manifest = transmogrifier.transform(json,
                                            new StringToManifestFilter());
        scope = new Scope();
        scope.addConstant("rootDir",
                          rootDir);
        conductor = new Conductor();
        state = new State(transmogrifier,
                          conductor,
                          scope);
        pipeline = transmogrifier.transform(manifest,
                                            state,
                                            new ManifestToPipelineFilter());
        transmogrifier.transform(pipeline,
                                 state,
                                 conductor);
        return null;
    }
}
