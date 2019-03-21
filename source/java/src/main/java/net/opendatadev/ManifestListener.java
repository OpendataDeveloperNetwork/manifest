package net.opendatadev;

import net.opendatadev.Manifest.Dataset;

import java.io.File;

public interface ManifestListener
{
    default void starting(final Manifest manifest)
    {
    }

    default void finished(final Manifest manifest)
    {
    }

    default void starting(final Manifest manifest,
                          final Dataset dataset)
    {
    }

    default void finished(final Manifest manifest,
                          final Dataset dataset,
                          final File rawFile,
                          final File convertedFile)
    {
    }
}
