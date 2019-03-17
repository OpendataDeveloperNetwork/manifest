package net.opendatadev;

import io.transmogrifier.Filter;
import io.transmogrifier.FilterException;
import io.transmogrifier.Transmogrifier;
import io.transmogrifier.conductor.Pipeline;
import io.transmogrifier.conductor.Scope;
import io.transmogrifier.conductor.State;
import io.transmogrifier.conductor.entries.Entry;
import io.transmogrifier.conductor.entries.PipelineEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemToPipelineFilter<T, S>
        implements Filter<T, State, Pipeline>
{
    @Override
    public Pipeline perform(final T object,
                            final State state)
            throws
            FilterException
    {
        final Transmogrifier             transmogrifier;
        final Scope                      outerScope;
        final Scope                      scope;
        final List<S>                    items;
        final List<Entry<?, ?, ?>>       entries;
        final Filter<S, State, Pipeline> filter;
        final Pipeline                   pipeline;

        transmogrifier = state.getTransmogrifier();
        outerScope = state.getScope();
        scope = new Scope(outerScope);
        items = getItemsFrom(object);
        entries = new ArrayList<>();
        filter = getFilter();

        for(final S item : items)
        {
            try
            {
                final Scope    itemScope;
                final Pipeline itemPipeline;
                final State    itemState;

                itemScope = createScope(scope,
                                        object,
                                        transmogrifier);
                itemState = new State(state,
                                      itemScope);
                itemPipeline = transmogrifier.transform(item,
                                                        itemState,
                                                        filter);
                entries.add(new PipelineEntry(itemState,
                                              itemPipeline));
            }
            catch(final IOException ex)
            {
                throw new FilterException(ex.getMessage(),
                                          ex);
            }
        }

        pipeline = new Pipeline(scope,
                                entries);

        return pipeline;
    }

    protected Scope createScope(final Scope outerScope,
                                final T item,
                                final Transmogrifier transmogrifier)
            throws
            IOException,
            FilterException
    {
        final Scope scope;

        scope = new Scope(outerScope);

        return scope;
    }

    protected abstract List<S> getItemsFrom(T item);

    protected abstract Filter<S, State, Pipeline> getFilter();
}
