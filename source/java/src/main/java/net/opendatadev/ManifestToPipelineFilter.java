package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.PipelineListener;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import io.transmogrifier.conductor.entries.Entry;
import net.opendatadev.Manifest.Dataset;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    protected Scope createPipelineScope(final Scope outerScope)
    {
        final Scope scope;

        scope = new Scope(outerScope);
        scope.addConstant("downloadsExecutorService",
                          Executors.newSingleThreadExecutor());

        return scope;
    }

    /**
     * @param pipeline
     */
    public void addPipelineListener(final Pipeline pipeline)
    {
        pipeline.addPipelineEntryListener(new PipelineListener()
        {
            /**
             *
             */
            @Override
            public void startingPerformance()
            {
                System.out.println("startingPerformance");
            }

            /**
             *
             */
            @Override
            public void completedPerformance()
            {
                final Scope           scope;
                final ExecutorService executorService;

                System.out.println("completedPerformance");
                scope = pipeline.getScope();
                executorService = scope.getValue("downloadsExecutorService");
                executorService.shutdown();
            }

            /**
             *
             * @param entry
             */
            @Override
            public void performing(final Entry<?, ?, ?> entry)
            {
                System.out.println("performing " + entry);
            }

            /**
             *
             * @param entry
             */
            @Override
            public void performed(final Entry<?, ?, ?> entry)
            {
                System.out.println("performed " + entry);
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
    protected Filter<Dataset, State, Pipeline> getFilter()
    {
        return new DatasetToPipelineFilter();
    }
}
