package net.opendatadev.filters;

import io.transmogrifier.Filter;
import net.opendatadev.Manifest.Dataset;

import java.io.File;

/**
 *
 */
public class DatasetToFileFilter
        implements Filter<Dataset, File, File>
{
    /**
     * @param dataset
     * @param parent
     * @return
     */
    @Override
    public File perform(final Dataset dataset,
                        final File parent)
    {
        final String dirPath;
        final String datasetName;
        final File   dir;

        datasetName = dataset.getName();
        dirPath = String.format("%s.json",
                                datasetName);
        dir = new File(parent,
                       dirPath);

        return dir;
    }
}