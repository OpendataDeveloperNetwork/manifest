package net.opendatadev.filters;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import net.opendatadev.Manifest.Dataset.Download;

/**
 *
 */
public class DownloadToFilenameFilter
        implements Filter<Download, String, String>
{
    /**
     * @param download
     * @param datasetName
     * @return
     * @throws FilterException
     */
    @Override
    public String perform(final Download download,
                          final String datasetName)
    {
        final String src;
        final int    slashIndex;
        final int    dotIndex;
        final String fileName;
        final String extension;
        final String rawFileName;

        src = download.getSrc();
        slashIndex = src.lastIndexOf('/');
        fileName = src.substring(slashIndex + 1);
        dotIndex = fileName.indexOf('.');
        extension = fileName.substring(dotIndex + 1);
        rawFileName = datasetName + "-raw." + extension;

        return rawFileName;
    }
}