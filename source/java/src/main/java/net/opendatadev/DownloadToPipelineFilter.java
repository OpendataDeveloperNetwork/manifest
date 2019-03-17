package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Constant;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import io.transmogrifier.conductor.Variable;
import io.transmogrifier.conductor.entries.Entry;
import io.transmogrifier.conductor.entries.FilterEntry;
import io.trasnmogrifier.filter.FileFilters.StringToFileFilter;
import io.trasnmogrifier.filter.URLFilters.URLToStringFilter;
import net.opendatadev.Manifest.Dataset.Download;
import net.opendatadev.filters.DownloadToFileFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DownloadToPipelineFilter
        implements Filter<Download, State, Pipeline>
{
    /**
     * @param download
     * @param state
     * @return
     * @throws FilterException
     */
    @Override
    public Pipeline perform(final Download download,
                            final State state)
            throws
            FilterException
    {
        System.out.println("C " + download.getSrc());

        // [url DownloadToString -> str]
        // [str ProcessEntry -> str]
        // [str WriteToFile: filename]
        // or
        // [url DownloadToString] -> [ProcessEntry] -> [WriteToFile: filename]

        try
        {
            final Transmogrifier       transmogrifier;
            final Scope                outerScope;
            final Scope                scope;
            final Constant<URL>        urlConstant;
            final File                 datasetDir;
            final File                 file;
            final Constant<File>       fileConstant;
            final Variable<String>     contentsVariable;
            final List<Entry<?, ?, ?>> entries;
            final Pipeline             pipeline;

            transmogrifier = state.getTransmogrifier();
            outerScope = state.getScope();
            scope = new Scope(outerScope);
            urlConstant = scope.addConstant("url",
                                            new URL(download.getSrc()));
            contentsVariable = scope.addVariable("str",
                                                 null);
            datasetDir = scope.getValue("datasetDir");
            file = transmogrifier.transform(download,
                                            datasetDir,
                                            new DownloadToFileFilter());
            fileConstant = scope.addConstant("file",
                                             file);
            System.out.println(scope);

            entries = new ArrayList<>();
            entries.add(new FilterEntry<>(state,
                                          new URLToStringFilter(),
                                          urlConstant,
                                          null,
                                          contentsVariable));
            entries.add(new FilterEntry<>(state,
                                          new StringToFileFilter(),
                                          contentsVariable,
                                          fileConstant));
            pipeline = new Pipeline(scope,
                                    entries);

            return pipeline;
        }
        catch(final MalformedURLException ex)
        {
            throw new FilterException(ex.getMessage(),
                                      ex);
        }
    }
}
