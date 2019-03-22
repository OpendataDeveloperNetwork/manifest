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
import net.opendatadev.filters.DatasetToDirFilter;
import net.opendatadev.filters.DatasetToFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ManifestToPipelineFilter
        implements Filter<Manifest, ManifestState, Pipeline>
{
    /**
     * @param manifest
     * @param state
     * @return
     * @throws FilterException
     */
    @Override
    public Pipeline perform(final Manifest manifest,
                            final ManifestState state)
            throws
            FilterException
    {
        final List<Dataset>   datasets;
        final ExecutorService executorService;
        final Transmogrifier  transmogrifier;
        final Scope           scope;
        final Field<File>     rootDir;
        final List<Entry>     entries;
        final Pipeline        pipeline;

        datasets = manifest.getDatasets();
        executorService = Executors.newSingleThreadScheduledExecutor();
        transmogrifier = state.getTransmogrifier();
        scope = state.getScope();
        rootDir = scope.getField("rootDir");
        entries = new ArrayList<>(datasets.size());

        for(final Dataset dataset : datasets)
        {
            final Pipeline                datasetPipeline;
            final Scope                   datasetScope;
            final ManifestState           datasetState;
            final File                    datasetDir;
            final File                    datasetFile;
            final BackgroundPipelineEntry backgroundPipelineEntry;

            datasetDir = transmogrifier.transform(dataset,
                                                  rootDir.getValue(),
                                                  new DatasetToDirFilter());
            datasetFile = transmogrifier.transform(dataset,
                                                   datasetDir,
                                                   new DatasetToFileFilter());

            datasetScope = new Scope();
            datasetScope.addConstant("manifest",
                                     manifest);
            datasetScope.addConstant("dataset",
                                     dataset);
            datasetScope.addConstant("datasetDir",
                                     datasetDir);
            datasetScope.addConstant("datasetFile",
                                     datasetFile);
            datasetState = new ManifestState(state,
                                             datasetScope);
            datasetPipeline = transmogrifier.transform(dataset,
                                                       datasetState,
                                                       new DatasetToPipelineFilter());
            backgroundPipelineEntry = new BackgroundPipelineEntry(state,
                                                                  datasetPipeline,
                                                                  executorService);
            entries.add(backgroundPipelineEntry);
        }

        pipeline = new Pipeline(state.getScope(),
                                entries);
        pipeline.addPipelineEntryListener(new PipelineListener()
        {
            /**
             *
             */
            @Override
            public void startingPerformance()
            {
                state.sendStartingManifest(manifest);
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

                state.sendFinishedManifest(manifest);
            }
        });

        return pipeline;
    }
}
