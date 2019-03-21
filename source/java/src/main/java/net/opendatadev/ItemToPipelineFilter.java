package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.entries.Entry;
import io.transmogrifier.conductor.entries.PipelineEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 * @param <S>
 */
public abstract class ItemToPipelineFilter<T, S>
        implements Filter<T, ManifestState, Pipeline>
{
    /**
     * @param object
     * @param state
     * @return
     * @throws FilterException
     */
    @Override
    public Pipeline perform(final T object,
                            final ManifestState state)
            throws
            FilterException
    {
        final Transmogrifier                     transmogrifier;
        final Scope                              outerScope;
        final Scope                              scope;
        final List<S>                            items;
        final List<Entry>                        entries;
        final Filter<S, ManifestState, Pipeline> filter;
        final Pipeline                           pipeline;

        transmogrifier = state.getTransmogrifier();
        outerScope = state.getScope();
        scope = createPipelineScope(outerScope,
                                    object,
                                    transmogrifier);
        items = getItemsFrom(object);
        entries = new ArrayList<>();
        filter = getFilter();

        for(final S item : items)
        {
            final Scope         itemScope;
            final Pipeline      itemPipeline;
            final ManifestState itemState;
            final PipelineEntry pipelineEntry;

            itemScope = createItemScope(scope,
                                        object,
                                        transmogrifier);
            itemState = new ManifestState(state,
                                          itemScope);
            itemPipeline = transmogrifier.transform(item,
                                                    itemState,
                                                    filter);
            pipelineEntry = new PipelineEntry(itemState,
                                              itemPipeline);
            entries.add(pipelineEntry);
        }

        pipeline = new Pipeline(scope,
                                entries);
        addPipelineListener(pipeline,
                            state);

        return pipeline;
    }

    /**
     * @param outerScope
     * @return
     * @throws FilterException
     */
    protected Scope createPipelineScope(final Scope outerScope,
                                        final T object,
                                        final Transmogrifier transmogrifier)
            throws
            FilterException
    {
        final Scope scope;

        scope = new Scope(outerScope);

        return scope;
    }

    /**
     * @param outerScope
     * @param item
     * @param transmogrifier
     * @return
     * @throws FilterException
     */
    protected Scope createItemScope(final Scope outerScope,
                                    final T item,
                                    final Transmogrifier transmogrifier)
            throws
            FilterException
    {
        final Scope scope;

        scope = new Scope(outerScope);

        return scope;
    }

    /**
     * @param pipeline
     */
    protected void addPipelineListener(final Pipeline pipeline,
                                       final ManifestState state)
    {
    }

    /**
     * @param entry
     */
    protected void addEntryFilter(final PipelineEntry entry)
    {
    }

    /**
     * @param item
     * @return
     */
    protected abstract List<S> getItemsFrom(T item);

    /**
     * @return
     */
    protected abstract Filter<S, ManifestState, Pipeline> getFilter();
}
