package net.opendatadev;

import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Conductor;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManifestState
        extends State
{
    private final CopyOnWriteArrayList<ManifestListener> listeners;

    /**
     * @param t
     * @param c
     */
    public ManifestState(final Transmogrifier t,
                         final Conductor c)
    {
        this(t,
             c,
             new Scope());
    }

    /**
     * @param state
     * @param scp
     */
    public ManifestState(final ManifestState state,
                         final Scope scp)
    {
        this(state.getTransmogrifier(),
             state.getConductor(),
             scp,
             state.listeners);
    }

    /**
     * @param t
     * @param c
     * @param scp
     */
    public ManifestState(final Transmogrifier t,
                         final Conductor c,
                         final Scope scp)
    {
        this(t,
             c,
             scp,
             new CopyOnWriteArrayList<>());
    }

    /**
     * @param t
     * @param c
     * @param scp
     * @param lists
     */
    private ManifestState(final Transmogrifier t,
                          final Conductor c,
                          final Scope scp,
                          final CopyOnWriteArrayList<ManifestListener> lists)
    {
        super(t,
              c,
              scp);

        listeners = lists;
    }

    public void addManifestListener(final ManifestListener listener)
    {
        listeners.add(listener);
    }

    public void sendStartingManifest(final Manifest manifest)
    {
        listeners.forEach((listener) -> listener.startingManifest(manifest));
    }

    public void sendFinishedManifest(final Manifest manifest)
    {
        listeners.forEach((listener) -> listener.finishedManifest(manifest));
    }

    public void sendStartingDataset(final Manifest manifest,
                                    final Dataset dataset)
    {
        listeners.forEach((listener) -> listener.startingDataset(manifest,
                                                                 dataset));
    }

    public void sendFinishedDataset(final Manifest manifest,
                                    final Dataset dataset,
                                    final File convertedFile,
                                    final File... rawFiles)
    {
        listeners.forEach((listener) -> listener.finishedDataset(manifest,
                                                                 dataset,
                                                                 convertedFile,
                                                                 rawFiles));
    }

    public void sendStartingDownload(final Manifest manifest,
                                     final Dataset dataset,
                                     final Download download)
    {
        listeners.forEach((listener) -> listener.startingDownload(manifest,
                                                                  dataset,
                                                                  download));
    }

    public void sendFinishedDownload(final Manifest manifest,
                                     final Dataset dataset,
                                     final Download download,
                                     final File rawFile)
    {
        listeners.forEach((listener) -> listener.finishedDownload(manifest,
                                                                  dataset,
                                                                  download,
                                                                  rawFile));
    }
}
