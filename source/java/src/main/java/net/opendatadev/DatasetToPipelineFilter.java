package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Field;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.PipelineListener;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.entries.BackgroundPipelineEntry;
import io.transmogrifier.conductor.entries.Entry;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;
import net.opendatadev.filters.DownloadToFilenameFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class DatasetToPipelineFilter
        implements Filter<Dataset, ManifestState, Pipeline>
{
    /**
     * @param dataset
     * @param state
     * @return
     * @throws FilterException
     */
    @Override
    public Pipeline perform(final Dataset dataset,
                            final ManifestState state)
            throws
            FilterException
    {
        final List<Download>  downloads;
        final ExecutorService executorService;
        final Transmogrifier  transmogrifier;
        final Scope           arguments;
        final Field<Manifest> manifestField;
        final Field<Dataset>  datasetField;
        final Field<File>     datasetDirField;
        final Field<File>     datasetFileField;
        final List<Entry>     entries;
        final Pipeline        pipeline;
        final Manifest        manifest;
        final List<File>      rawFiles;

        downloads = dataset.getDownloads();
        executorService = Executors.newSingleThreadScheduledExecutor();
        transmogrifier = state.getTransmogrifier();
        arguments = state.getScope();
        manifestField = arguments.getField("manifest");
        datasetField = arguments.getField("dataset");
        datasetDirField = arguments.getField("datasetDir");
        datasetFileField = arguments.getField("datasetFile");
        entries = new ArrayList<>(downloads.size());
        rawFiles = new ArrayList<>(downloads.size());   // we assume 1 download per dataset

        for(final Download download : downloads)
        {
            final Pipeline                downloadPipeline;
            final Scope                   downloadScope;
            final ManifestState           downloadState;
            final BackgroundPipelineEntry backgroundPipelineEntry;
            final String                  rawFileName;
            final File                    datasetDir;
            final File                    rawFile;

            rawFileName = transmogrifier.transform(download,
                                                   dataset.getName(),
                                                   new DownloadToFilenameFilter());
            datasetDir = datasetDirField.getValue();
            rawFile = new File(datasetDir,
                               rawFileName);
            rawFiles.add(rawFile);
            downloadScope = new Scope();
            downloadScope.addConstant(manifestField);
            downloadScope.addConstant(datasetField);
            downloadScope.addConstant(datasetDirField);
            downloadScope.addConstant(datasetFileField);
            downloadScope.addConstant("rawFile",
                                      rawFile);
            downloadState = new ManifestState(state,
                                              downloadScope);
            downloadPipeline = transmogrifier.transform(download,
                                                        downloadState,
                                                        new DownloadToPipelineFilter());
            backgroundPipelineEntry = new BackgroundPipelineEntry(state,
                                                                  downloadPipeline,
                                                                  executorService);
            entries.add(backgroundPipelineEntry);
        }

        pipeline = new Pipeline(arguments,
                                entries);

        manifest = manifestField.getValue();
        pipeline.addPipelineEntryListener(new PipelineListener()
        {
            /**
             *
             */
            @Override
            public void startingPerformance()
            {
                state.sendStartingDataset(manifest,
                                          dataset);
            }

            /**
             *
             */
            @Override
            public void completedPerformance()
            {
                executorService.shutdown();

                try
                {
                    executorService.awaitTermination(1,
                                                     TimeUnit.MINUTES);
                }
                catch(final InterruptedException ex)
                {
                    ex.printStackTrace();
                }

                state.sendFinishedDataset(manifest,
                                          dataset,
                                          null,
                                          rawFiles.stream().toArray(File[]::new));
            }
        });

        return pipeline;
    }
}
