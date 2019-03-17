package net.opendatadev;

import com.google.common.io.Files;
import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;
import net.opendatadev.filters.DatasetToDirFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DatasetToPipelineFilter
        extends ItemToPipelineFilter<Dataset, Download>
{
    @Override
    protected Scope createScope(final Scope outerScope,
                                final Dataset dataset,
                                final Transmogrifier transmogrifier)
            throws
            IOException,
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

        return scope;
    }

    protected List<Download> getItemsFrom(final Dataset dataset)
    {
        return dataset.getDownloads();
    }

    protected Filter<Download, State, Pipeline> getFilter()
    {
        return new DownloadToPipelineFilter();
    }
}