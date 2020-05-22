package data.lab.ongdb.index;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.lucene.queryparser.classic.ParseException;
import org.neo4j.graphdb.DependencyResolver;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.internal.kernel.api.IndexReference;
import org.neo4j.internal.kernel.api.exceptions.schema.IndexNotFoundKernelException;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.api.impl.fulltext.FulltextAdapter;
import org.neo4j.kernel.api.impl.fulltext.FulltextProcedures;
import org.neo4j.kernel.api.impl.fulltext.ScoreEntityIterator;
import org.neo4j.kernel.impl.api.KernelTransactionImplementation;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.neo4j.storageengine.api.EntityType;
import org.neo4j.storageengine.api.schema.IndexDescriptor;
import org.neo4j.util.FeatureToggles;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.neo4j.procedure.Mode.READ;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.index.CustomizeFullTextSearcher
 * @Description: TODO(自定义全文检索得分算法)
 * @date 2020/5/22 10:23
 */
public class CustomizeFullTextSearcher {

    private static final long INDEX_ONLINE_QUERY_TIMEOUT_SECONDS = FeatureToggles.getInteger(
            FulltextProcedures.class, "INDEX_ONLINE_QUERY_TIMEOUT_SECONDS", 30);

    @Context
    public KernelTransaction tx;

    @Context
    public GraphDatabaseService db;

    @Context
    public DependencyResolver resolver;

    @Context
    public FulltextAdapter accessor;

    /**
     * @param name:索引名
     * @param query:查询STRING
     * @return
     * @Description: TODO(使用SimHash计算得分)
     */
    @Description("Query the given fulltext index. Returns the matching nodes and their lucene query score, ordered by score.")
    @Procedure(name = "olab.index.fulltext.queryNodesBySimHash", mode = READ)
    public Stream<NodeOutput> queryFulltextForNodesBySimHash(@Name("indexName") String name, @Name("queryString") String query)
            throws ParseException, IndexNotFoundKernelException, IOException {
        IndexReference indexReference = getValidIndexReference(name);
        awaitOnline(indexReference);
        EntityType entityType = indexReference.schema().entityType();
        if (entityType != EntityType.NODE) {
            throw new IllegalArgumentException("The '" + name + "' index (" + indexReference + ") is an index on " + entityType +
                    ", so it cannot be queried for nodes.");
        }
        ScoreEntityIterator resultIterator = accessor.query(tx, name, query);
//        return resultIterator.stream()
//                .map(result -> NodeOutput.forExistingEntityOrNull(db, result))
//                .filter(Objects::nonNull);
        return null;
    }

    private IndexReference getValidIndexReference(@Name("indexName") String name) {
        IndexReference indexReference = tx.schemaRead().indexGetForName(name);
        if (indexReference == IndexReference.NO_INDEX) {
            throw new IllegalArgumentException("There is no such fulltext schema index: " + name);
        }
        return indexReference;
    }

    private void awaitOnline(IndexReference indexReference) throws IndexNotFoundKernelException {
        // We do the isAdded check on the transaction state first, because indexGetState will grab a schema read-lock, which can deadlock on the write-lock
        // held by the index populator. Also, if we index was created in this transaction, then we will never see it come online in this transaction anyway.
        // Indexes don't come online until the transaction that creates them has committed.
        if (!((KernelTransactionImplementation) tx).txState().indexDiffSetsBySchema(indexReference.schema()).isAdded((IndexDescriptor) indexReference)) {
            // If the index was not created in this transaction, then wait for it to come online before querying.
            Schema schema = db.schema();
            IndexDefinition index = schema.getIndexByName(indexReference.name());
            schema.awaitIndexOnline(index, INDEX_ONLINE_QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
        // If the index was created in this transaction, then we skip this check entirely.
        // We will get an exception later, when we try to get an IndexReader, so this is fine.
    }

    public static final class NodeOutput {
        public final Node node;
        public final double score;

        protected NodeOutput(Node node, double score) {
            this.node = node;
            this.score = score;
        }

//        public static FulltextProcedures.NodeOutput forExistingEntityOrNull(GraphDatabaseService db, ScoreEntityIterator.ScoreEntry result )
//        {
//            try
//            {
//                return new FulltextProcedures.NodeOutput( db.getNodeById( result.entityId() ), result.score() );
//            }
//            catch ( NotFoundException ignore )
//            {
//                // This node was most likely deleted by a concurrent transaction, so we just ignore it.
//                return null;
//            }
//        }
    }

}

