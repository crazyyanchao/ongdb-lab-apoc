package data.lab.ongdb.similarity;

import data.lab.ongdb.result.OutputId;
import data.lab.ongdb.similarity.simhash.SimHash;
import data.lab.ongdb.similarity.simhash.TextFingerPrint;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.Map;
import java.util.stream.Stream;

import static org.neo4j.helpers.collection.MapUtil.map;

public class SimilarityProce {

    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    public static Label Customer = Label.label("Customer");

    /**
     * @param nodeN:节点N
     * @param nodeM:节点M
     * @param nodeNsimhashFieldName:存储simhash值的字段
     * @param nodeMsimhashFieldName:存储simhash值的字段
     * @param relName:生成的关系类型名
     * @param hammingDistance:汉明距离阈值
     * @return
     * @Description: TODO(计算两个节点的文本属性相似度并生成相似关系)
     */
    @Procedure(name = "olab.simhash.build.rel", mode = Mode.WRITE)
    @Description("CALL olab.simhash.build.rel({node1},{node2},{simhashFieldName},{relName}),{hammingDistance} YIELD relId")
    public Stream<OutputId> createCustomer(@Name("nodeN") Node nodeN, @Name("nodeM") Node nodeM,
                                             @Name("nodeNsimhashFieldName") String nodeNsimhashFieldName,
                                             @Name("nodeMsimhashFieldName") String nodeMsimhashFieldName,
                                             @Name("relName") String relName,
                                             @Name("hammingDistance") Number hammingDistance) {
        long relId = -1;
        if (nodeN.hasProperty(nodeNsimhashFieldName) && nodeM.hasProperty(nodeMsimhashFieldName)) {
            String fingerPrintN = String.valueOf(nodeN.getProperty(nodeNsimhashFieldName));
            String fingerPrintM = String.valueOf(nodeN.getProperty(nodeMsimhashFieldName));
            if (fingerPrintN != null && fingerPrintM != null
                    && !"".equals(fingerPrintN) && !"".equals(fingerPrintM)) {
                boolean bool = SimHash.isSimilar(new TextFingerPrint(fingerPrintN), new TextFingerPrint(fingerPrintM), hammingDistance.intValue());
                if (bool) {
                    long idN = nodeN.getId();
                    long idM = nodeM.getId();
                    String mergeQuery="MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE (n)-[r:"+relName+"]->(m) RETURN id(r) AS relId";
                    Result resultPath = db.execute(mergeQuery, map("idN", idN,"idM", idM));
                    while (resultPath.hasNext()){
                        Map<String,Object> map = resultPath.next();
                        relId = (long) map.get("relId");
                    }
                }
            }
        }
        return Stream.of(new OutputId(relId));
    }

}

