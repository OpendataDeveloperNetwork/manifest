package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.State;
import net.opendatadev.Manifest.Dataset;

import java.util.List;

/**
 *
 */
public class ManifestToPipelineFilter
        extends ItemToPipelineFilter<Manifest, Dataset>
{
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
