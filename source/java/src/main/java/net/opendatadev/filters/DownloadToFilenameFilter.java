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
        final String fileName;
        final String rawFileName;

        src = download.getSrc();
        slashIndex = src.lastIndexOf('/');
        fileName = src.substring(slashIndex + 1);
        rawFileName = String.format("%s-%s",
                                    datasetName,
                                    fileName);

        return rawFileName;
    }
}