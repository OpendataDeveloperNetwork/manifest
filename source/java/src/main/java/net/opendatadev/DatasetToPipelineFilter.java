package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.PipelineListener;
import io.transmogrifier.conductor.Scope;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;
import net.opendatadev.filters.DatasetToDirFilter;

import java.io.File;
import java.util.List;

/**
 *
 */
public class DatasetToPipelineFilter
        extends ItemToPipelineFilter<Dataset, Download>
{
    /**
     * @param outerScope
     * @param dataset
     * @param transmogrifier
     * @return
     * @throws FilterException
     */
    @Override
    protected Scope createPipelineScope(final Scope outerScope,
                                        final Dataset dataset,
                                        final Transmogrifier transmogrifier)
            throws
            FilterException
    {
        final File  rootDir;
        final File  datasetDir;
        final Scope scope;

        rootDir = outerScope.getValue("rootDir");
        datasetDir = transmogrifier.transform(dataset,
                                              rootDir,
                                              new DatasetToDirFilter());
        scope = new Scope(outerScope);
        scope.addConstant("datasetDir",
                          datasetDir);
        scope.addConstant("dataset",
                          dataset);

        return scope;
    }

    /**
     * @param pipeline
     */
    public void addPipelineListener(final Pipeline pipeline,
                                    final ManifestState state)
    {
        pipeline.addPipelineEntryListener(new PipelineListener()
        {
            @Override
            public void startingPerformance()
            {
                final Scope    scope;
                final Manifest manifest;
                final Dataset  dataset;

                scope = pipeline.getScope();
                manifest = scope.getValue("manifest");
                dataset = scope.getValue("dataset");
                state.sendStartingDataset(manifest,
                                          dataset);
            }

            /**
             *
             */
            @Override
            public void completedPerformance()
            {
                final Scope    scope;
                final Manifest manifest;
                final Dataset  dataset;

                scope = pipeline.getScope();
                manifest = scope.getValue("manifest");
                dataset = scope.getValue("dataset");
                state.sendFinishedDataset(manifest,
                                          dataset,
                                          null,
                                          null);
            }
        });
    }

    /**
     * @param dataset
     * @return
     */
    protected List<Download> getItemsFrom(final Dataset dataset)
    {
        return dataset.getDownloads();
    }

    /**
     * @return
     */
    protected Filter<Download, ManifestState, Pipeline> getFilter()
    {
        return new DownloadToPipelineFilter();
    }
}