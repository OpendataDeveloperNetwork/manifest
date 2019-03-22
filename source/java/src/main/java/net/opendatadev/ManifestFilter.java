package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Conductor;
import io.transmogrifier.conductor.Pipeline;
import net.opendatadev.filters.StringToManifestFilter;

import java.io.File;

public class ManifestFilter
        implements Filter<String, ManifestState, Void>
{
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

    /**
     * @param manifestJSON
     * @param state
     * @return
     * @throws FilterException
     */
    @Override
    public Void perform(final String manifestJSON,
                        final ManifestState state)
            throws
            FilterException
    {
        final Transmogrifier transmogrifier;
        final Conductor conductor;
        final Manifest manifest;
        final Pipeline pipeline;

        transmogrifier = state.getTransmogrifier();
        manifest = transmogrifier.transform(manifestJSON,
                                            new StringToManifestFilter());
        pipeline = transmogrifier.transform(manifest,
                                            state,
                                            new ManifestToPipelineFilter());
        conductor = state.getConductor();
        transmogrifier.transform(pipeline,
                                 state,
                                 conductor);
        return null;
    }
}
