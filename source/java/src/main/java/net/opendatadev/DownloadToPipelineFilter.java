package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Constant;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.Variable;
import io.transmogrifier.conductor.entries.BackgroundPipelineEntry;
import io.transmogrifier.conductor.entries.Entry;
import io.transmogrifier.conductor.entries.FilterEntry;
import io.trasnmogrifier.filter.FileFilters.StringToFileFilter;
import io.trasnmogrifier.filter.URLFilters.URLToStringFilter;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;
import net.opendatadev.filters.DownloadToFilenameFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
        // [url DownloadToString] -> str
        // [str ProcessEntry] -> str
        // [str WriteToFile: filename]
        // or
        // [url DownloadToString -> str]
        // [str ProcessEntry -> str]
        // [str WriteToFile: filename]
        // or
        // [url DownloadToString] -> [ProcessEntry] -> [WriteToFile: filename]

        try
        {
            final Transmogrifier          transmogrifier;
            final Scope                   outerScope;
            final Scope                   scope;
            final Constant<URL>           urlConstant;
            final Dataset                 dataset;
            final String                  datasetName;
            final String                  fileName;
            final File                    datasetDir;
            final File                    file;
            final Constant<File>          fileConstant;
            final Variable<String>        contentsVariable;
            final Entry                   downloadEntry;
            final Entry                   writeToFileEntry;
            final List<Entry>             entries;
            final BackgroundPipelineEntry backgroundPipelineEntry;
            final Pipeline                backgroundPipeline;
            final Pipeline                pipeline;
            final ExecutorService         executorService;

            transmogrifier = state.getTransmogrifier();
            outerScope = state.getScope();
            scope = new Scope(outerScope);
            urlConstant = scope.addConstant("url",
                                            new URL(download.getSrc()));
            contentsVariable = scope.addVariable("str",
                                                 null);
            datasetDir = scope.getValue("datasetDir");
            dataset = scope.getValue("dataset");
            datasetName = dataset.getName();
            fileName = transmogrifier.transform(download,
                                                datasetName,
                                                new DownloadToFilenameFilter());
            file = new File(datasetDir,
                            fileName);
            fileConstant = scope.addConstant("rawFile",
                                             file);
            entries = new ArrayList<>();
            downloadEntry = new FilterEntry<>(state,
                                              new URLToStringFilter(),
                                              urlConstant,
                                              null,
                                              contentsVariable);
            entries.add(downloadEntry);
            writeToFileEntry = new FilterEntry<>(state,
                                                 new StringToFileFilter(),
                                                 contentsVariable,
                                                 fileConstant);

            entries.add(writeToFileEntry);
            System.out.println(scope);

            /*
            entries.add(new FilterEntry<>(state,
                                          (entry, ignore) ->
                                          {
                                              System.out.println(entry);

                                              return null;
                                          },
                                          contentsVariable));
            */
            backgroundPipeline = new Pipeline(scope,
                                              entries);
            executorService = outerScope.getValue("downloadsExecutorService");
            backgroundPipelineEntry = new BackgroundPipelineEntry(state,
                                                                  backgroundPipeline,
                                                                  executorService);
            pipeline = new Pipeline(scope,
                                    backgroundPipelineEntry);

            return pipeline;
        }
        catch(final MalformedURLException ex)
        {
            throw new FilterException(ex.getMessage(),
                                      ex);
        }
    }
}
