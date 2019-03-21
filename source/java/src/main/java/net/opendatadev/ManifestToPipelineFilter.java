package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.PipelineListener;
import io.transmogrifier.conductor.Scope;
import net.opendatadev.Manifest.Dataset;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ManifestToPipelineFilter
        extends ItemToPipelineFilter<Manifest, Dataset>
{
    /**
     * @param outerScope
     * @return
     * @throws FilterException
     */
    @Override
    protected Scope createPipelineScope(final Scope outerScope,
                                        final Manifest manifest,
                                        final Transmogrifier transmogrifier)
    {
        final Scope scope;

        scope = new Scope(outerScope);
        scope.addConstant("downloadsExecutorService",
                          Executors.newSingleThreadExecutor());
        scope.addConstant("manifest",
                          manifest);

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

                scope = pipeline.getScope();
                manifest = scope.getValue("manifest");
                state.sendStartingManifest(manifest);
            }

            /**
             *
             */
            @Override
            public void completedPerformance()
            {
                final Scope           scope;
                final ExecutorService executorService;

                scope = pipeline.getScope();
                executorService = scope.getValue("downloadsExecutorService");
                executorService.shutdown();

                try
                {
                    final Manifest manifest;

                    executorService.awaitTermination(1,
                                                     TimeUnit.MINUTES);
                    manifest = scope.getValue("manifest");
                    state.sendFinishedManifest(manifest);
                }
                catch(final InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * @param manifest
     * @return
     */
    protected List<Dataset> getItemsFrom(final Manifest manifest)
    {
        return manifest.getDatasets();
    }

    /**
     * @return
     */
    protected Filter<Dataset, ManifestState, Pipeline> getFilter()
    {
        return new DatasetToPipelineFilter();
    }
}
