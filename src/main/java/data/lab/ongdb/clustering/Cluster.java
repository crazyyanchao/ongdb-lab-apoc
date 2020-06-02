//package data.lab.ongdb.clustering;
///*
// *
// * Data Lab - graph database organization.
// *
// */
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Result;
//import org.neo4j.graphdb.Transaction;
//import org.neo4j.procedure.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * @author Yc-Ma
// * @PACKAGE_NAME: data.lab.ongdb.clustering
// * @Description: TODO(聚类过程)
// * @date 2020/5/29 13:38
// */
//public class Cluster {
//
//    /**
//     * 运行环境/上下文
//     */
//    @Context
//    public GraphDatabaseService db;
//
//    /**
//     * @param masterNodeLabelList:对此类标签的节点进行聚类计算
//     * @param relWeightMap:权重分值分配map【权重为-1表示此类节点关系之间不计算相似度直接分类】
//     * @param clusterFocusNodeLabel:生成聚类中心的节点标签
//     * @param threshold:最小相似权重设置(关系权重加总之后的得分不能小于这个值)
//     * @param slavesMarkField:对所有的从节点设置主节点的ID作为分簇标识
//     * @param endemismRelList:特有性较强的关系【决定了点N与哪些点进行计算可以避免遍历超级节点】
//     * @return
//     * @Description: TODO(对指定关系模式的节点进行聚类操作 - 并对所有从节点设置主节点的唯一ID - 默认支持两层关系碰撞做聚类)
//     */
//    @Procedure(name = "olab.cluster.collision", mode = Mode.WRITE)
//    @Description("CALL olab.cluster.collision({masterNodeLabel},{relName1:weightiness,relName2:weightiness...},{clusterFocusNodeLabel},{threshold},{slavesMarkField}) YIELD clusterNum")
//    public Stream<ClusterResult> clusterOptimize(@Name("masterNodeLabel") List<String> masterNodeLabelList,
//                                                 @Name("relWeightMap") Map<String, Number> relWeightMap,
//                                                 @Name("clusterFocusNodeLabel") String clusterFocusNodeLabel,
//                                                 @Name("threshold") Number threshold,
//                                                 @Name("slavesMarkField") String slavesMarkField,
//                                                 @Name("endemismRels") List<String> endemismRelList) {
//
//        final int batch = 1000;
//
//        String endemismRels = packRels(endemismRelList);
//
//        StringBuilder builder = new StringBuilder();
//        for (String label : masterNodeLabelList) {
//            builder.append(label).append(":");
//        }
//        String masterNodeLabel = builder.substring(0, builder.length() - 1);
//        String readCypher;
//        String writeClusterFocusCypher;
//        String writeClusterIdCypher;
//        long count = 0;
//        Result resultCount = db.execute("MATCH (n:" + masterNodeLabel + ") RETURN COUNT(*) AS count");
//        if (resultCount.hasNext()) {
//            count = (long) resultCount.next().get("count");
//        }
//
//        int commit = 0;
//        Transaction tx = db.beginTx();
//        try {
//            for (int i = 0; i < count; i++) {
//                List<Long[]> rawLongList = new ArrayList<>();
//
//                // 与当前点有强关联的点进行计算
//                String cypher = "MATCH (n:" + masterNodeLabel + ") WITH n SKIP " + i + " LIMIT 1 MATCH (n)-" + endemismRels + "->()<-" + endemismRels + "-(m) RETURN id(n) AS idN,COLLECT(ID(m)) AS idList";
//                Map<Long, List<Long>> relTargetListMap = relTargetList(cypher);
//                if (!relTargetListMap.isEmpty()) {
//                    for (Long idNNodeId : relTargetListMap.keySet()) {
//                        List<Long> relTargetList = relTargetListMap.get(idNNodeId);
//                        for (long idMNodeId : relTargetList) {
//                            int num = 0;
//                            List<List<String>> collectList = new ArrayList<>();
//                            long idN = 0;
//                            long idM = 0;
//                            for (; ; ) {
//                                // 拿出节点间所有关系进行分析
//                                readCypher = "MATCH (n:" + masterNodeLabel + ") MATCH (m:" + masterNodeLabel + ") " +
//                                        "WHERE id(n)=" + idNNodeId + " AND id(m)=" + idMNodeId + " \n" +
////                                        "MATCH p=(n)-->()<--(m) \n" +
//                                        "MATCH p=(n)-[*..2]-(m) \n" +
//                                        "WITH [r IN relationships(p) | type(r)] AS relList,n,m\n" +
//                                        "RETURN relList AS relList,id(n) AS idN,id(m) AS idM SKIP " + num + " LIMIT " + batch;
//                                Result result = db.execute(readCypher);
//
//                                List<String> relList = new ArrayList<>();
//                                while (result.hasNext()) {
//                                    Map<String, Object> map = result.next();
//                                    idN = (long) map.get("idN");
//                                    idM = (long) map.get("idM");
//                                    relList = (List<String>) map.get("relList");
//                                    collectList.add(relList);
//                                }
//                                // 收集两点之间的所有路径
//                                if (!relList.isEmpty()) {
//                                    num += batch;
//                                } else {
//                                    break;
//                                }
//                            }
//                            // 目标点和所有结束点 - 加载PATH关系集合LIST并加总相似度权重
//                            if (!collectList.isEmpty() && idN != idM) {
//                                Map<String, Long> mapSimilarity = getWeightinessSimilarity(relWeightMap, collectList);
//                                long similarity = mapSimilarity.get("similarity");
//                                // '-1'-false   '1'-true
//                                long isDirectlySim = mapSimilarity.get("isDirectlySim");
//                                // 相似度阈值符合要求，或者直接相似，即添加到列表
//                                if (similarity > threshold.longValue() || isDirectlySim == 1) {
//                                    rawLongList.add(new Long[]{idN, idM, similarity, isDirectlySim});
//                                }
//                            }
//                        }
//
//                        // 关联的点有相似的实体则进行下一步处理
//                        if (!rawLongList.isEmpty()) {
//                            // 生成直接相似的列表
//                            List<Long[]> isDirectlySimList = rawLongList.stream()
//                                    .filter(v -> v[3] == 1).collect(Collectors.toList());
//
//                            // 通过阈值过滤
//                            List<Long[]> filterLongList = rawLongList.stream()
//                                    .filter(v -> v[3] == -1)
//                                    .filter(v -> v[2] > threshold.longValue())
//                                    .sorted(Comparator.comparing(v -> v[2])).collect(Collectors.toList());
//
//                            // 相似节点列表-一个聚簇中的所有节点列表
//                            List<Long> longList = new ArrayList<>();
//                            if (!filterLongList.isEmpty()) {
//                                longList = filterLongList.stream()
//                                        .map(v -> v[1]).collect(Collectors.toList());
//                                longList.add(filterLongList.get(0)[0]);
//                            }
//                            if (!isDirectlySimList.isEmpty()) {
//                                List<Long> longList2 = isDirectlySimList.stream()
//                                        .map(v -> v[1]).collect(Collectors.toList());
//                                longList2.add(isDirectlySimList.get(0)[0]);
//                                longList.addAll(longList2);
//                            }
//
//                            if (!longList.isEmpty()) {
//                                // 列表去重
//                                longList = longList.stream().distinct().collect(Collectors.toList());
//                                JSONArray longListArray = JSONArray.parseArray(JSON.toJSONString(longList));
//                                // 找到信息量最大的点做为聚类中心并设置标签
//                                writeClusterFocusCypher = "MATCH (n) WHERE id(n) IN " + longListArray.toJSONString() + " REMOVE n:" + clusterFocusNodeLabel + " WITH n,size((n)-[]-()) AS size ORDER BY size DESC LIMIT 1 \n" +
//                                        "SET n:" + clusterFocusNodeLabel + " RETURN id(n) AS id";
//                                Result focusResult = db.execute(writeClusterFocusCypher);
//                                long clusterId = -1;
//                                if (focusResult.hasNext()) {
//                                    clusterId = (long) focusResult.next().get("id");
//                                }
//
//                                // 为聚簇中所有点设置聚簇ID
//                                writeClusterIdCypher = "MATCH (n) WHERE id(n) IN " + longListArray.toJSONString() + " SET n." + slavesMarkField + "=" + clusterId;
//                                db.execute(writeClusterIdCypher);
//                            }
//                        } else {
//                            // 当前点如果没有关联的相似点则设为一个master
//                            String writeCluster = "MATCH (n) WHERE id(n)=" + idNNodeId + " SET n." + slavesMarkField + "=" + idNNodeId + ",n:" + clusterFocusNodeLabel;
//                            db.execute(writeCluster);
//                        }
//                    }
//                } else {
//                    // 当前点如果没有关联的点则设为一个master
//                    String writeClusterId = "MATCH (n:" + masterNodeLabel + ") WITH n SKIP " + i + " LIMIT 1 SET n." + slavesMarkField + "=id(n)" + ",n:" + clusterFocusNodeLabel;
//                    db.execute(writeClusterId);
//                }
//
//                // 批量提交事务
//                commit++;
//                if (commit > batch) {
//                    tx.success();
//                    tx.close();
//                    tx =  db.beginTx();
//                    commit = 0;
//                }
//            }
//        }finally {
//            tx.close();
//        }
//
//        Result result = db.execute("MATCH (n:" + clusterFocusNodeLabel + ") RETURN COUNT(*) AS count");
//        long clusterNum = 0;
//        if (result.hasNext()) {
//            clusterNum = (long) result.next().get("count");
//        }
//        return Stream.of(new ClusterResult(clusterNum));
//    }
//
//    private String packRels(List<String> endemismRelList) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("[");
//        for (String key : endemismRelList) {
//            builder.append(":");
//            builder.append(key);
//            builder.append("|");
//        }
//        builder.append("]");
//        return builder.replace(builder.length() - 2, builder.length() - 1, "").toString();
//    }
//
//    private Map<Long, List<Long>> relTargetList(String cypher) {
//        Map<Long, List<Long>> mapIdList = new HashMap<>();
//        Result result = db.execute(cypher);
//        while (result.hasNext()) {
//            Map<String, Object> map = result.next();
//            List<Long> idList = (List<Long>) map.get("idList");
//            long idN = (long) map.get("idN");
//            List<Long> newLidList = idList.stream().distinct().collect(Collectors.toList());
//            mapIdList.put(idN, newLidList);
//        }
//        return mapIdList;
//    }
//
////    /**
////     * @param masterNodeLabelList:对此类标签的节点进行聚类计算
////     * @param relWeightMap:权重分值分配map【权重为-1表示此类节点关系之间不计算相似度直接分类】
////     * @param clusterFocusNodeLabel:生成聚类中心的节点标签
////     * @param threshold:最小相似权重设置(关系权重加总之后的得分不能小于这个值)
////     * @param slavesMarkField:对所有的从节点设置主节点的ID作为分簇标识
////     * @return
////     * @Description: TODO(对指定关系模式的节点进行聚类操作 - 并对所有从节点设置主节点的唯一ID - 默认支持两层关系碰撞做聚类)
////     */
////    @Procedure(name = "olab.cluster.collision", mode = Mode.WRITE)
////    @Description("CALL olab.cluster.collision({masterNodeLabel},{relName1:weightiness,relName2:weightiness...},{clusterFocusNodeLabel},{threshold},{slavesMarkField}) YIELD clusterNum")
////    public Stream<ClusterResult> cluster(@Name("masterNodeLabel") List<String> masterNodeLabelList,
////                                         @Name("relWeightMap") Map<String, Number> relWeightMap,
////                                         @Name("clusterFocusNodeLabel") String clusterFocusNodeLabel,
////                                         @Name("threshold") Number threshold,
////                                         @Name("slavesMarkField") String slavesMarkField) {
////        StringBuilder builder = new StringBuilder();
////        for (String label : masterNodeLabelList) {
////            builder.append(label).append(":");
////        }
////        String masterNodeLabel = builder.substring(0, builder.length() - 1);
////        String readCypher;
////        String writeClusterFocusCypher;
////        String writeClusterIdCypher;
////        long count = 0;
////        Result resultCount = db.execute("MATCH (n:" + masterNodeLabel + ") RETURN COUNT(*) AS count");
////        if (resultCount.hasNext()) {
////            count = (long) resultCount.next().get("count");
////        }
////        for (int i = 0; i < count; i++) {
////
////            List<Long[]> rawLongList = new ArrayList<>();
////            int num = 0;
////            int batch = 200;
////            for (; ; ) {
////                readCypher = "MATCH (n:" + masterNodeLabel + ") WITH n SKIP " + i + " LIMIT 1\n" +
////                        "MATCH (m:" + masterNodeLabel + ") WHERE n<>m WITH n,m\n" +
////                        "MATCH p=(n)-[*..2]-(m) WHERE n<>m \n" +
////                        "WITH [r IN relationships(p) | type(r)] AS relList,n,m\n" +
////                        "RETURN collect(relList) AS collectList,id(n) AS idN,id(m) AS idM SKIP " + num + " LIMIT " + batch;
////                Result result = db.execute(readCypher);
////
////                // 加载PATH关系集合LIST并加总相似度权重
////                List<Long[]> rawLongListTemp = new ArrayList<>();
////                while (result.hasNext()) {
////                    Map<String, Object> map = result.next();
////                    long idN = (long) map.get("idN");
////                    long idM = (long) map.get("idM");
////                    List<List<String>> collectList = (List<List<String>>) map.get("collectList");
////                    Map<String, Long> mapSimilarity = getWeightinessSimilarity(relWeightMap, collectList);
////                    long similarity = mapSimilarity.get("similarity");
////                    // '-1'-false   '1'-true
////                    long isDirectlySim = mapSimilarity.get("isDirectlySim");
////                    rawLongListTemp.add(new Long[]{idN, idM, similarity, isDirectlySim});
////                }
////
////                if (!rawLongListTemp.isEmpty()) {
////                    rawLongList.addAll(rawLongListTemp);
////                    num += batch;
////                } else {
////                    break;
////                }
////            }
////
////            if (!rawLongList.isEmpty()) {
////                // 生成直接相似的列表
////                List<Long[]> isDirectlySimList = rawLongList.stream()
////                        .filter(v -> v[3] == 1).collect(Collectors.toList());
////
////                // 通过阈值过滤
////                List<Long[]> filterLongList = rawLongList.stream()
////                        .filter(v -> v[3] == -1)
////                        .filter(v -> v[2] > threshold.longValue())
////                        .sorted(Comparator.comparing(v -> v[2])).collect(Collectors.toList());
////
////                // 相似节点列表-一个聚簇中的所有节点列表
////                List<Long> longList = new ArrayList<>();
////                if (!filterLongList.isEmpty()) {
////                    longList = filterLongList.stream()
////                            .map(v -> v[1]).collect(Collectors.toList());
////                    longList.add(filterLongList.get(0)[0]);
////                }
////                if (!isDirectlySimList.isEmpty()) {
////                    List<Long> longList2 = isDirectlySimList.stream()
////                            .map(v -> v[1]).collect(Collectors.toList());
////                    longList2.add(isDirectlySimList.get(0)[0]);
////                    longList.addAll(longList2);
////                }
////
////                if (!longList.isEmpty()) {
////                    // 列表去重
////                    longList = longList.stream().distinct().collect(Collectors.toList());
////                    JSONArray longListArray = JSONArray.parseArray(JSON.toJSONString(longList));
////                    // 找到信息量最大的点做为聚类中心并设置标签
////                    writeClusterFocusCypher = "MATCH (n) WHERE id(n) IN " + longListArray.toJSONString() + " REMOVE n:" + clusterFocusNodeLabel + " WITH n,size((n)-[]-()) AS size ORDER BY size DESC LIMIT 1 \n" +
////                            "SET n:" + clusterFocusNodeLabel + " RETURN id(n) AS id";
////                    Result focusResult = db.execute(writeClusterFocusCypher);
////                    long clusterId = -1;
////                    if (focusResult.hasNext()) {
////                        clusterId = (long) focusResult.next().get("id");
////                    }
////
////                    // 为聚簇中所有点设置聚簇ID
////                    writeClusterIdCypher = "MATCH (n) WHERE id(n) IN " + longListArray.toJSONString() + " SET n." + slavesMarkField + "=" + clusterId;
////                    db.execute(writeClusterIdCypher);
////                }
////            }
////        }
////        Result result = db.execute("MATCH (n:" + clusterFocusNodeLabel + ") RETURN COUNT(*) AS count");
////        long clusterNum = 0;
////        if (result.hasNext()) {
////            clusterNum = (long) result.next().get("count");
////        }
////        return Stream.of(new ClusterResult(clusterNum));
////    }
//
//    private Map<String, Long> getWeightinessSimilarity
//            (Map<String, Number> weightinessMap, List<List<String>> relPathList) {
//        int similarity = 0;
//        // 等1直接判定为相似
//        long isDirectlySim = -1;
//        for (List<String> pathRelList : relPathList) {
//            String relName = pathRelList.get(0);
//            if (pathRelList.size() == 1) {
//                if (weightinessMap.containsKey(relName)) {
//                    int weightiness = weightinessMap.get(relName).intValue();
//                    if (weightiness != -1) {
//                        similarity += weightiness;
//                    } else {
//                        isDirectlySim = 1;
//                        break;
//                    }
//                }
//            } else if (pathRelList.size() == 2 && pathRelList.get(0).equals(pathRelList.get(1))) {
//                if (weightinessMap.containsKey(relName)) {
//                    int weightiness = weightinessMap.get(relName).intValue();
//                    if (weightiness != -1) {
//                        similarity += weightiness;
//                    } else {
//                        isDirectlySim = 1;
//                        break;
//                    }
//                }
//            }
//        }
//        Map<String, Long> map = new HashMap<>();
//        map.put("similarity", (long) similarity);
//        map.put("isDirectlySim", isDirectlySim);
//        return map;
//    }
//
//    public static class ClusterResult {
//        public final Number clusterNum;
//
//        public ClusterResult(Number clusterNum) {
//            this.clusterNum = clusterNum;
//        }
//    }
//}
//
//
