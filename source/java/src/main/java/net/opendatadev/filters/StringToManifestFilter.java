package net.opendatadev.filters;

import com.google.gson.Gson;
import io.transmogrifier.UnaryFilter;
import net.opendatadev.Manifest;

/**
 *
 */
public class StringToManifestFilter
        implements UnaryFilter<String, Manifest>
{
    /**
     * @param input
     * @return
     */
    @Override
    public Manifest perform(final String input)
    {
        final Gson     gson;
        final Manifest manifest;

        gson = new Gson();
        manifest = gson.fromJson(input,
                                 Manifest.class);

        return manifest;
    }
}
