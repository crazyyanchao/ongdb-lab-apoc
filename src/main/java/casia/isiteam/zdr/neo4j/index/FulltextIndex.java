package casia.isiteam.zdr.neo4j.index;
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

import casia.isiteam.zdr.neo4j.message.NodeIndexMessage;
import casia.isiteam.zdr.neo4j.result.ChineseHit;
import casia.isiteam.zdr.wltea.analyzer.cfg.Configuration;
import casia.isiteam.zdr.wltea.analyzer.core.IKSegmenter;
import casia.isiteam.zdr.wltea.analyzer.core.Lexeme;
import casia.isiteam.zdr.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.log4j.PropertyConfigurator;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.index
 * @Description: TODO(基于IK分词的中文全文检索过程)
 * @date 2019/4/9 11:42
 */
public class FulltextIndex {

    private static final String tokenFilter = "casia.isiteam.zdr.wltea.analyzer.lucene.IKAnalyzer";

    private static final Map<String, String> FULL_INDEX_CONFIG = MapUtil
            .stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext", "analyzer", tokenFilter);

    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    /**
     * @param indexName:索引名称
     * @param query:查询（可直接使用索引查询更底层功能）
     * @param limit:限制数据量
     * @return
     * @Description: TODO(查询中文全文索引)
     */
    @Procedure(value = "zdr.index.chineseFulltextIndexSearch", mode = Mode.WRITE)
    @Description("CALL zdr.index.chineseFulltextIndexSearch(String indexName, String query, long limit) YIELD node,weight RETURN node,weight" +
            "执行LUCENE全文检索，返回前{limit个结果}")
    public Stream<ChineseHit> chineseFulltextIndexSearch(@Name("indexName") String indexName,
                                                         @Name("query") String query, @Name("limit") long limit) {
        if (!db.index().existsForNodes(indexName)) {
            log.debug("如果索引不存在则跳过本次查询：`%s`", indexName);
            return Stream.empty();
        }
        QueryContext queryParam = new QueryContext(query).sortByScore().top((int) limit);
        return toWeightedNodeResult(db.index().forNodes(indexName, FULL_INDEX_CONFIG).query(queryParam));
    }

    /**
     * @param
     * @return
     * @Description: TODO(全文检索节点结果输出)
     */
    private Stream<ChineseHit> toWeightedNodeResult(IndexHits<Node> hits) {
        List<ChineseHit> results = new ArrayList<>();
        while (hits.hasNext()) {
            Node node = hits.next();
            results.add(new ChineseHit(node, hits.currentScore()));
        }
        return results.stream();
    }

    /**
     * @param indexName:索引名称
     * @param labelName:标签名称
     * @param propKeys:属性名称列表
     * @return
     * @Description: TODO(创建中文全文索引)
     */
    @Procedure(value = "zdr.index.addChineseFulltextIndex", mode = Mode.WRITE)
    @Description("CALL zdr.index.addChineseFulltextIndex(String indexName, String labelName, List<String> propKeys) YIELD message RETURN message," +
            "为一个标签下的所有节点的指定属性添加索引")
    public Stream<NodeIndexMessage> addChineseFulltextIndex(@Name("indexName") String indexName,
                                                            @Name("labelName") String labelName, @Name("properties") List<String> propKeys) {
        Label label = Label.label(labelName);

        List<NodeIndexMessage> output = new ArrayList<>();

//        // 按照标签找到该标签下的所有节点
        ResourceIterator<Node> nodes = db.findNodes(label);
        System.out.println("nodes:" + nodes.toString());

        int nodesSize = 0;
        int propertiesSize = 0;
        while (nodes.hasNext()) {
            nodesSize++;
            Node node = nodes.next();
            System.out.println("current nodes:" + node.toString());

            // 每个节点上需要添加索引的属性
            Set<Map.Entry<String, Object>> properties = node.getProperties(propKeys.toArray(new String[0])).entrySet();
            System.out.println("current node properties" + properties);

            // 查询该节点是否已有索引，有的话删除
            Index<Node> index = db.index().forNodes(indexName, FULL_INDEX_CONFIG);
            System.out.println("current node index" + index);
            index.remove(node);

            // 为了该节点的每个需要添加索引的属性添加全文索引
            for (Map.Entry<String, Object> property : properties) {
                propertiesSize++;
                index.add(node, property.getKey(), property.getValue());
            }
        }

        String message = "IndexName:" + indexName + ",LabelName:" + labelName + ",NodesSize:" + nodesSize + ",PropertiesSize:" + propertiesSize;
        NodeIndexMessage indexMessage = new NodeIndexMessage(message);
        output.add(indexMessage);
        return output.stream();
    }


    /**
     * @param text:待分词文本
     * @param useSmart:true 用智能分词，false 细粒度分词
     * @return
     * @Description: TODO(支持中英文本分词)
     */
    @UserFunction(name = "zdr.index.iKAnalyzer")
    @Description("Fulltext index iKAnalyzer - RETURN zdr.index.iKAnalyzer({text},true) AS words")
    public List<String> iKAnalyzer(@Name("text") String text, @Name("useSmart") boolean useSmart) {

        PropertyConfigurator.configureAndWatch("dic" + File.separator + "log4j.properties");
        Configuration cfg = new Configuration(useSmart);

        StringReader input = new StringReader(text.trim());
        IKSegmenter ikSegmenter = new IKSegmenter(input, cfg);

        List<String> results = new ArrayList<>();
        try {
            for (Lexeme lexeme = ikSegmenter.next(); lexeme != null; lexeme = ikSegmenter.next()) {
                results.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

}

