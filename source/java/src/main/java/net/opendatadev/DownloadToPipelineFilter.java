package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.conductor.Constant;
import io.transmogrifier.conductor.Field;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.PipelineListener;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.Variable;
import io.transmogrifier.conductor.entries.Entry;
import io.transmogrifier.conductor.entries.FilterEntry;
import io.trasnmogrifier.filter.FileFilters.StringToFileFilter;
import io.trasnmogrifier.filter.URLFilters.URLToStringFilter;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DownloadToPipelineFilter
        implements Filter<Download, ManifestState, Pipeline>
{
    /**
     * @param download
     * @param state
     * @return
     * @throws FilterException
     */
    @Override
    public Pipeline perform(final Download download,
                            final ManifestState state)
            throws
            FilterException
    {
        final Scope            arguments;
        final Scope            scope;
        final Dataset          dataset;
        final Constant<URL>    urlConstant;
        final Variable<String> contentsVariable;
        final Field<File>      rawFileField;
        final Entry            downloadEntry;
        final Entry            writeToFileEntry;
        final List<Entry>      entries;
        final Pipeline         pipeline;
        final Manifest         manifest;

        arguments = state.getScope();
        scope = new Scope(arguments);

        try
        {
            urlConstant = scope.addConstant("url",
                                            new URL(download.getSrc()));
        }
        catch(final MalformedURLException ex)
        {
            throw new FilterException(ex.getMessage(),
                                      ex);
        }

        dataset = scope.getValue("dataset");
        rawFileField = scope.getField("rawFile");
        contentsVariable = scope.addVariable("contents",
                                             null);
        downloadEntry = new FilterEntry<>(state,
                                          new URLToStringFilter(),
                                          urlConstant,
                                          contentsVariable);
        writeToFileEntry = new FilterEntry<>(state,
                                             new StringToFileFilter(),
                                             contentsVariable,
                                             rawFileField);

        entries = new ArrayList<>();
        entries.add(downloadEntry);
        entries.add(writeToFileEntry);
        pipeline = new Pipeline(scope,
                                entries);
        manifest = arguments.getValue("manifest");

        pipeline.addPipelineEntryListener(new PipelineListener()
        {
            /**
             *
             */
            @Override
            public void startingPerformance()
            {
                state.sendStartingDownload(manifest,
                                           dataset,
                                           download);
            }

            /**
             *
             */
            @Override
            public void completedPerformance()
            {
                state.sendFinishedDownload(manifest,
                                           dataset,
                                           download,
                                           rawFileField.getValue());
            }
        });

        return pipeline;
    }
}
