package data.lab.ongdb.index;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import org.neo4j.kernel.api.impl.index.collector.ValuesIterator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.index
 * @Description: TODO(Iterator over entity ids together with their respective score.)
 * @date 2019/8/16 16:41
 */
public class CustomizeScoreEntityIterator implements Iterator<CustomizeScoreEntityIterator.ScoreEntry> {
    private final ValuesIterator iterator;
    private final Predicate<ScoreEntry> predicate;
    private CustomizeScoreEntityIterator.ScoreEntry next;

    CustomizeScoreEntityIterator( ValuesIterator sortedValuesIterator )
    {
        this.iterator = sortedValuesIterator;
        this.predicate = null;
    }

    private CustomizeScoreEntityIterator( ValuesIterator sortedValuesIterator, Predicate<CustomizeScoreEntityIterator.ScoreEntry> predicate )
    {
        this.iterator = sortedValuesIterator;
        this.predicate = predicate;
    }

    public Stream<ScoreEntry> stream()
    {
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( this, Spliterator.ORDERED ), false );
    }

    @Override
    public boolean hasNext()
    {
        while ( next == null && iterator.hasNext() )
        {
            long entityId = iterator.next();
            float score = iterator.currentScore();
            CustomizeScoreEntityIterator.ScoreEntry tmp = new CustomizeScoreEntityIterator.ScoreEntry( entityId, score );
            if ( predicate == null || predicate.test( tmp ) )
            {
                next = tmp;
            }
        }
        return next != null;
    }

    @Override
    public CustomizeScoreEntityIterator.ScoreEntry next()
    {
        if ( hasNext() )
        {
            CustomizeScoreEntityIterator.ScoreEntry tmp = next;
            next = null;
            return tmp;
        }
        else
        {
            throw new NoSuchElementException( "The iterator is exhausted" );
        }
    }

    CustomizeScoreEntityIterator filter( Predicate<CustomizeScoreEntityIterator.ScoreEntry> predicate )
    {
        if ( this.predicate != null )
        {
            predicate = this.predicate.and( predicate );
        }
        return new CustomizeScoreEntityIterator( iterator, predicate );
    }

    /**
     * Merges the given iterators into a single iterator, that maintains the aggregate descending score sort order.
     *
     * @param iterators to concatenate
     * @return a {@link CustomizeScoreEntityIterator} that iterates over all of the elements in all of the given iterators
     */
    static CustomizeScoreEntityIterator mergeIterators( List<CustomizeScoreEntityIterator> iterators )
    {
        return new CustomizeScoreEntityIterator.ConcatenatingScoreEntityIterator( iterators );
    }

    private static class ConcatenatingScoreEntityIterator extends CustomizeScoreEntityIterator
    {
        private final List<? extends CustomizeScoreEntityIterator> iterators;
        private final CustomizeScoreEntityIterator.ScoreEntry[] buffer;
        private boolean fetched;
        private CustomizeScoreEntityIterator.ScoreEntry nextHead;

        ConcatenatingScoreEntityIterator( List<? extends CustomizeScoreEntityIterator> iterators )
        {
            super( null );
            this.iterators = iterators;
            this.buffer = new CustomizeScoreEntityIterator.ScoreEntry[iterators.size()];
        }

        @Override
        public boolean hasNext()
        {
            if ( !fetched )
            {
                fetch();
            }
            return nextHead != null;
        }

        private void fetch()
        {
            int candidateHead = -1;
            for ( int i = 0; i < iterators.size(); i++ )
            {
                CustomizeScoreEntityIterator.ScoreEntry entry = buffer[i];
                //Fill buffer if needed.
                if ( entry == null && iterators.get( i ).hasNext() )
                {
                    entry = iterators.get( i ).next();
                    buffer[i] = entry;
                }

                //Check if entry might be candidate for next to return.
                if ( entry != null && (nextHead == null || entry.score > nextHead.score) )
                {
                    nextHead = entry;
                    candidateHead = i;
                }
            }
            if ( candidateHead != -1 )
            {
                buffer[candidateHead] = null;
            }
            fetched = true;
        }

        @Override
        public CustomizeScoreEntityIterator.ScoreEntry next()
        {
            if ( hasNext() )
            {
                fetched = false;
                CustomizeScoreEntityIterator.ScoreEntry best = nextHead;
                nextHead = null;
                return best;
            }
            else
            {
                throw new NoSuchElementException( "The iterator is exhausted" );
            }
        }
    }

    /**
     * A ScoreEntry consists of an entity id together with its score.
     */
    static class ScoreEntry
    {
        private final long entityId;
        private final float score;

        long entityId()
        {
            return entityId;
        }

        float score()
        {
            return score;
        }

        ScoreEntry( long entityId, float score )
        {
            this.entityId = entityId;
            this.score = score;
        }

        @Override
        public String toString()
        {
            return "ScoreEntry[entityId=" + entityId + ", score=" + score + "]";
        }
    }
}
