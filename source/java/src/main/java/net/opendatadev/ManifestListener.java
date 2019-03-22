package net.opendatadev;

import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;

import java.io.File;

public interface ManifestListener
{
    default void startingManifest(final Manifest manifest)
    {
    }

    default void finishedManifest(final Manifest manifest)
    {
    }

    default void startingDataset(final Manifest manifest,
                                 final Dataset dataset)
    {
    }

    default void finishedDataset(final Manifest manifest,
                                 final Dataset dataset,
                                 final File convertedFile,
                                 final File... rawFiles)
    {
    }

    default void startingDownload(final Manifest manifest,
                                  final Dataset dataset,
                                  final Download download)
    {
    }

    default void finishedDownload(final Manifest manifest,
                                  final Dataset dataset,
                                  final Download download,
                                  final File rawFile)
    {
    }
}
