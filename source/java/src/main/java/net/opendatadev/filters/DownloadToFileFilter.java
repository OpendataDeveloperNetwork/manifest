package net.opendatadev.filters;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import net.opendatadev.Manifest.Dataset;
import net.opendatadev.Manifest.Dataset.Download;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadToFileFilter
        implements Filter<Download, File, File>
{
    @Override
    public File perform(final Download download,
                        final File parent)
            throws FilterException
    {
        final String src;
        final String fileName;
        final File   file;

        src = download.getSrc();
        fileName = src.substring(src.lastIndexOf('/') + 1, src.length());
        file = new File(parent,
                        fileName);

        return file;
    }
}