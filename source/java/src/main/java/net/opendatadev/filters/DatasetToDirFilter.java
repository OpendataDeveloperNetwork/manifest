package net.opendatadev.filters;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import net.opendatadev.Manifest.Dataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 */
public class DatasetToDirFilter
        implements Filter<Dataset, File, File>
{
    /**
     * @param dataset
     * @param parent
     * @return
     * @throws FilterException
     */
    @Override
    public File perform(final Dataset dataset,
                        final File parent)
            throws
            FilterException
    {
        final String dirPath;
        final String country;
        final String subdivision;
        final String region;
        final String city;
        final String provider;
        final Path   path;
        final File   dir;

        country = dataset.getCountry();
        subdivision = dataset.getSubdivision();
        region = dataset.getRegion();
        city = dataset.getCity();
        provider = dataset.getProvider();
        dirPath = String.format("%s/%s/%s/%s/%s",
                                country,
                                subdivision,
                                region,
                                city,
                                provider);
        dir = new File(parent,
                       dirPath);
        path = dir.toPath();

        try
        {
            Files.createDirectories(path);
        }
        catch(final IOException ex)
        {
            throw new FilterException(ex.getMessage(),
                                      ex);
        }

        return dir;
    }
}