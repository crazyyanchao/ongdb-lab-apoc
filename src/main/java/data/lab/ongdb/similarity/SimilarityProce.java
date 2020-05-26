package data.lab.ongdb.similarity;

import data.lab.ongdb.result.PathResult;
import data.lab.ongdb.similarity.simhash.SimHash;
import data.lab.ongdb.similarity.simhash.TextFingerPrint;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * @param nodeN:节点N
     * @param nodeM:节点M
     * @param nodeNsimhashFieldName:存储simhash值的字段
     * @param nodeMsimhashFieldName:存储simhash值的字段
     * @param relName:生成的关系类型名
     * @param hammingDistanceThreshold:汉明距离阈值
     * @param recordHimmingDistance:是否将汉明距离计算值存储在关系上
     * @return
     * @Description: TODO(计算两个节点的文本属性相似度并生成相似关系)
     */
    @Procedure(name = "olab.simhash.build.rel", mode = Mode.WRITE)
    @Description("CALL olab.simhash.build.rel({nodeN},{nodeM},{nodeNsimhashFieldName},{nodeMsimhashFieldName},{relName}),{hammingDistanceThreshold},{recordHimmingDistance}) YIELD pathJ")
    public Stream<PathResult> simHashSimilarityPathBuild(@Name("nodeN") Node nodeN, @Name("nodeM") Node nodeM,
                                                         @Name("nodeNsimhashFieldName") String nodeNsimhashFieldName,
                                                         @Name("nodeMsimhashFieldName") String nodeMsimhashFieldName,
                                                         @Name("relName") String relName,
                                                         @Name("hammingDistanceThreshold") Number hammingDistanceThreshold,
                                                         @Name("recordHimDistance") boolean recordHimmingDistance) {
        if (nodeN.hasProperty(nodeNsimhashFieldName) && nodeM.hasProperty(nodeMsimhashFieldName)) {
            String fingerPrintN = String.valueOf(nodeN.getProperty(nodeNsimhashFieldName));
            String fingerPrintM = String.valueOf(nodeM.getProperty(nodeMsimhashFieldName));
            if (fingerPrintN != null && fingerPrintM != null
                    && !"".equals(fingerPrintN) && !"".equals(fingerPrintM)) {
                boolean bool = SimHash.isSimilar(new TextFingerPrint(fingerPrintN), new TextFingerPrint(fingerPrintM), hammingDistanceThreshold.intValue());
                if (bool) {
                    long idN = nodeN.getId();
                    long idM = nodeM.getId();
                    if (!isMatchCurrentRel(idN, idM, relName)) {
                        Result resultPath;
                        String mergeQuery;
                        if (recordHimmingDistance) {
                            int himDis = SimHash.hamming(fingerPrintN, fingerPrintM);
                            mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) SET r.recordHimmingDistance=$himDis RETURN p";
                            resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM, "himDis", himDis));
                        } else {
                            mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) RETURN p";
                            resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM));
                        }
                        if (resultPath.hasNext()) {
                            Map<String, Object> map = resultPath.next();
                            Object object = map.get("p");
                            return Stream.of(new PathResult(object));
                        }
                    }
                }
            }
        }
        return Stream.of(new PathResult());
    }

    private boolean isMatchCurrentRel(long idN, long idM, String relName) {
        String matchCypher = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MATCH p=(n)-[r:" + relName + "]-(m) RETURN p";
        Result resultPath = db.execute(matchCypher, map("idN", idN, "idM", idM));
        return resultPath.hasNext();
    }

    /**
     * @param nodeN:节点N
     * @param nodeM:节点M
     * @param nodeNeditDistanceFieldName:N节点的存储被计算文本的字段
     * @param nodeMeditDistanceFieldName:M节点的存储被计算文本的字段
     * @param relName:创建的关系名称
     * @param editDistanceThreshold:编辑距离的阈值
     * @param recordEditDistance:是否记录编辑距离相似度数值
     * @return
     * @Description: TODO(编辑距离计算相似度)
     */
    @Procedure(name = "olab.editDistance.build.rel", mode = Mode.WRITE)
    @Description("CALL olab.editDistance.build.rel({nodeN},{nodeM},{nodeNeditDistanceFieldName},{nodeMeditDistanceFieldName},{relName}),{editDistanceThreshold},{recordEditDistance}) YIELD pathJ")
    public Stream<PathResult> editSimilarityPathBuild(@Name("nodeN") Node nodeN, @Name("nodeM") Node nodeM,
                                                      @Name("nodeNeditDistanceFieldName") String nodeNeditDistanceFieldName,
                                                      @Name("nodeMeditDistanceFieldName") String nodeMeditDistanceFieldName,
                                                      @Name("relName") String relName,
                                                      @Name("editDistanceThreshold") double editDistanceThreshold,
                                                      @Name("recordEditDistance") boolean recordEditDistance) {
        if (nodeN.hasProperty(nodeNeditDistanceFieldName) && nodeM.hasProperty(nodeMeditDistanceFieldName)) {
            String textN = String.valueOf(nodeN.getProperty(nodeNeditDistanceFieldName));
            String textM = String.valueOf(nodeM.getProperty(nodeMeditDistanceFieldName));
            if (textN != null && textM != null
                    && !"".equals(textN) && !"".equals(textM)) {
                Map<String, Object> mapEditDis = EditDistance.isSimilarityThresholdMap(textN, textM, editDistanceThreshold);
                boolean bool = (boolean) mapEditDis.get("isSimilarity");
                if (bool) {
                    long idN = nodeN.getId();
                    long idM = nodeM.getId();
                    if (!isMatchCurrentRel(idN, idM, relName)) {
                        Result resultPath;
                        String mergeQuery;
                        if (recordEditDistance) {
                            double editDisSimilarity = (double) mapEditDis.get("similarityValue");
                            mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) SET r.recordEditDistance=$editDis RETURN p";
                            resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM, "editDis", editDisSimilarity));
                        } else {
                            mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) RETURN p";
                            resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM));
                        }
                        if (resultPath.hasNext()) {
                            Map<String, Object> map = resultPath.next();
                            Object object = map.get("p");
                            return Stream.of(new PathResult(object));
                        }
                    }
                }
            }
        }
        return Stream.of(new PathResult());
    }

    /**
     * @param nodeN:节点N
     * @param nodeM:节点M
     * @param crossRel:需要拿取的关联属性-一般是同类属性
     * @param crossNodeFiledName:关联节点中需要拿取属性字段
     * @param nodeNeditDistanceFieldName:N节点的存储被计算文本的字段
     * @param nodeMeditDistanceFieldName:M节点的存储被计算文本的字段
     * @param relName:创建的关系名称
     * @param editDistanceThreshold:编辑距离的阈值
     * @param recordEditDistance:是否记录编辑距离相似度数值
     * @return
     * @Description: TODO(编辑距离计算相似度)
     */
    @Procedure(name = "olab.editDistance.build.rel.cross", mode = Mode.WRITE)
    @Description("CALL olab.editDistance.build.rel.cross({nodeN},{nodeM},{crossRel},{crossNodeFiledName},{nodeNeditDistanceFieldName},{nodeMeditDistanceFieldName},{relName}),{editDistanceThreshold},{recordEditDistance}) YIELD pathJ")
    public Stream<PathResult> editSimilarityPathBuildCross(@Name("nodeN") Node nodeN, @Name("nodeM") Node nodeM,
                                                           @Name("crossRel") String crossRel,
                                                           @Name("crossNodeFiledName") String crossNodeFiledName,
                                                           @Name("nodeNeditDistanceFieldName") String nodeNeditDistanceFieldName,
                                                           @Name("nodeMeditDistanceFieldName") String nodeMeditDistanceFieldName,
                                                           @Name("relName") String relName,
                                                           @Name("editDistanceThreshold") double editDistanceThreshold,
                                                           @Name("recordEditDistance") boolean recordEditDistance) {
        List<String> nodeNCrossNameList = getNodeCrossNameList(nodeN, crossRel, crossNodeFiledName, nodeNeditDistanceFieldName);
        List<String> nodeMCrossNameList = getNodeCrossNameList(nodeM, crossRel, crossNodeFiledName, nodeMeditDistanceFieldName);
        Map<String, Object> mapEditDis = isSimilarity(nodeNCrossNameList, nodeMCrossNameList, editDistanceThreshold);
        boolean bool = (boolean) mapEditDis.get("isSimilarity");
        if (bool) {
            long idN = nodeN.getId();
            long idM = nodeM.getId();
            if (!isMatchCurrentRel(idN, idM, relName)) {
                Result resultPath;
                String mergeQuery;
                if (recordEditDistance) {
                    double editDisSimilarity = (double) mapEditDis.get("similarityValue");
                    mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) SET r.recordEditDistance=$editDis RETURN p";
                    resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM, "editDis", editDisSimilarity));
                } else {
                    mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) RETURN p";
                    resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM));
                }
                if (resultPath.hasNext()) {
                    Map<String, Object> map = resultPath.next();
                    Object object = map.get("p");
                    return Stream.of(new PathResult(object));
                }
            }
        }
        return Stream.of(new PathResult());
    }

    private Map<String, Object> isSimilarity(List<String> nodeNCrossNameList, List<String> nodeMCrossNameList, double editDistanceThreshold) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (String textN : nodeNCrossNameList) {
            for (String textM : nodeMCrossNameList) {
                Map<String, Object> map = EditDistance.isSimilarityThresholdMap(textN, textM, editDistanceThreshold);
                mapList.add(map);
            }
        }
        return mapList.stream().max((v1, v2) -> {
            Double similarityValue1 = (double) v1.get("similarityValue");
            Double similarityValue2 = (double) v2.get("similarityValue");
            return similarityValue1.compareTo(similarityValue2);
        }).get();
    }
    private Map<String, Object> isSimilarity(List<String> nodeNCrossNameList, List<String> nodeMCrossNameList, double editDistanceThresholdEn,double editDistanceThresholdCn) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (String textN : nodeNCrossNameList) {
            for (String textM : nodeMCrossNameList) {
                Map<String, Object> map = EditDistance.isSimilarityThresholdMap(textN, textM, editDistanceThresholdEn,editDistanceThresholdCn);
                mapList.add(map);
            }
        }
        return mapList.stream().max((v1, v2) -> {
            Double similarityValue1 = (double) v1.get("similarityValue");
            Double similarityValue2 = (double) v2.get("similarityValue");
            return similarityValue1.compareTo(similarityValue2);
        }).get();
    }

    private List<String> getNodeCrossNameList(Node node, String crossRel, String crossNodeFiledName, String nodeEditDistanceFieldName) {
        long id = node.getId();
        String text = String.valueOf(node.getProperty(nodeEditDistanceFieldName));
        List<String> list = new ArrayList<>();
        list.add(text);
        String cypher = "MATCH (n) WHERE id(n)=$id MATCH p=(n)-[r:" + crossRel + "]-(m) RETURN m." + crossNodeFiledName + " AS name";
        Result result = db.execute(cypher, map("id", id));
        while (result.hasNext()) {
            String textCro = String.valueOf(result.next().get("name"));
            list.add(textCro);
        }
        return list;
    }

    /**
     * @param nodeN:节点N
     * @param nodeM:节点M
     * @param crossRel:需要拿取的关联属性-一般是同类属性
     * @param crossNodeFiledName:关联节点中需要拿取属性字段
     * @param nodeNeditDistanceFieldName:N节点的存储被计算文本的字段
     * @param nodeMeditDistanceFieldName:M节点的存储被计算文本的字段
     * @param relName:创建的关系名称
     * @param editDistanceThresholdEn:编辑距离的英文阈值
     * @param editDistanceThresholdCn:编辑距离的中文阈值
     * @param recordEditDistance:是否记录编辑距离相似度数值
     * @return
     * @Description: TODO(编辑距离计算相似度)
     */
    @Procedure(name = "olab.editDistance.build.rel.cross.encn", mode = Mode.WRITE)
    @Description("CALL olab.editDistance.build.rel.cross.encn({nodeN},{nodeM},{crossRel},{crossNodeFiledName},{nodeNeditDistanceFieldName},{nodeMeditDistanceFieldName},{relName}),{editDistanceThresholdEn},{editDistanceThresholdCn},{recordEditDistance}) YIELD pathJ")
    public Stream<PathResult> editSimilarityPathBuildCross(@Name("nodeN") Node nodeN, @Name("nodeM") Node nodeM,
                                                           @Name("crossRel") String crossRel,
                                                           @Name("crossNodeFiledName") String crossNodeFiledName,
                                                           @Name("nodeNeditDistanceFieldName") String nodeNeditDistanceFieldName,
                                                           @Name("nodeMeditDistanceFieldName") String nodeMeditDistanceFieldName,
                                                           @Name("relName") String relName,
                                                           @Name("editDistanceThresholdEn") double editDistanceThresholdEn,
                                                           @Name("editDistanceThresholdCn") double editDistanceThresholdCn,
                                                           @Name("recordEditDistance") boolean recordEditDistance) {
        List<String> nodeNCrossNameList = getNodeCrossNameList(nodeN, crossRel, crossNodeFiledName, nodeNeditDistanceFieldName);
        List<String> nodeMCrossNameList = getNodeCrossNameList(nodeM, crossRel, crossNodeFiledName, nodeMeditDistanceFieldName);
        Map<String, Object> mapEditDis = isSimilarity(nodeNCrossNameList, nodeMCrossNameList, editDistanceThresholdEn, editDistanceThresholdCn);
        boolean bool = (boolean) mapEditDis.get("isSimilarity");
        if (bool) {
            long idN = nodeN.getId();
            long idM = nodeM.getId();
            if (!isMatchCurrentRel(idN, idM, relName)) {
                Result resultPath;
                String mergeQuery;
                if (recordEditDistance) {
                    double editDisSimilarity = (double) mapEditDis.get("similarityValue");
                    mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) SET r.recordEditDistance=$editDis RETURN p";
                    resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM, "editDis", editDisSimilarity));
                } else {
                    mergeQuery = "MATCH (n),(m) WHERE id(n)=$idN AND id(m)=$idM MERGE p=(n)-[r:" + relName + "]->(m) RETURN p";
                    resultPath = db.execute(mergeQuery, map("idN", idN, "idM", idM));
                }
                if (resultPath.hasNext()) {
                    Map<String, Object> map = resultPath.next();
                    Object object = map.get("p");
                    return Stream.of(new PathResult(object));
                }
            }
        }
        return Stream.of(new PathResult());
    }
}

