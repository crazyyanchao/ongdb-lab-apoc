package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.graphdb.Label;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.util.JSONTool
 * @Description: TODO(JSONTool)
 * @date 2020/4/28 14:37
 */
public class JSONTool {

    /**
     * @param
     * @return
     * @Description: TODO(去掉JSON数据的KEY的双引号)
     */
    public static String removeKeyDoubleQuotationMarkJustEnglish(JSON json) {
        // 仅支持英文
        // DATA PACKAGE中属性KEY不能有双引号
        String dataPackage = null;
        if (json != null) {
            dataPackage = json.toJSONString().replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
        }
        return dataPackage;
    }

    /**
     * @param
     * @return
     * @Description: TODO(去掉JSONArray数据的KEY的双引号)
     */
    public static String removeKeyDoubleQuotationMark(JSONArray array) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int size = array.size();
        for (int i = 0; i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            String objectStr = removeJSONObjKeyDoubleQuotationMark(object);
            if (i == size - 1) {
                builder.append(objectStr);
            } else {
                builder.append(objectStr + ",");
            }
        }
        builder.append("]");
        String dataPackage = builder.toString();
        return dataPackage;
    }

    /**
     * @param
     * @return
     * @Description: TODO(去掉JSONObject数据的KEY的双引号)
     */
    public static String removeJSONObjKeyDoubleQuotationMark(JSONObject object) {

        StringBuilder builder = new StringBuilder();
        builder.append("{");

        int j = 0;
        int outSize = object.size();
        for (Map.Entry entry : object.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            builder.append(key.replace("\"", "") + ":");
            if (value instanceof JSONObject) {
                JSONObject valueObj = (JSONObject) value;
                builder.append("{");
                int i = 0;
                int size = valueObj.size();
                for (Map.Entry entry2 : valueObj.entrySet()) {
                    String key2 = (String) entry2.getKey();
                    Object value2 = entry2.getValue();
                    builder.append(key2.replace("\"", "") + ":");
                    i++;
                    value2 = repalceChars(value2);
                    if (i == size) {
                        builder.append("\"" + value2 + "\"");
                    } else {
                        builder.append("\"" + value2 + "\",");
                    }
                }
                j++;
                if (j == outSize) {
                    builder.append("}");
                } else {
                    builder.append("},");
                }
            } else if (value instanceof JSONArray) {
                builder.append(((JSONArray) value).toJSONString());
            } else {
                j++;
                value = repalceChars(value);
                if (j == outSize) {
                    builder.append("\"" + value + "\"");

                } else {
                    builder.append("\"" + value + "\",");
                }
            }
        }
        builder.append("}");
        String dataPackage = builder.toString();
        return dataPackage;
    }

    /**
     * @param
     * @return
     * @Description: TODO(去掉JSONObject数据的KEY的双引号)
     */
    public static String removeOnlyJSONObjectKeyDoubleQuotation(JSONObject object) {

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        Set<Map.Entry<String, Object>> entries = object.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

        boolean hasNext = iterator.hasNext();
        while (hasNext) {
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            builder.append(key.replace("\"", "") + ":");
            builder.append(value.toString());

            hasNext = iterator.hasNext();
            if (hasNext) {
                builder.append(",");
            }
        }
        builder.append("}");
        String dataPackage = builder.toString();
        return dataPackage;
    }

    /**
     * @param
     * @return
     * @Description: TODO(替换影响数据入库的特殊字符)
     */
    private static Object repalceChars(Object object) {

        if (object instanceof String) {
            String entityName = (String) object;
            if (entityName != null) {

                // 先替换反斜杠
                entityName = entityName.replace("\\", "\\\\");

                // 再替换单引号
                entityName = String.valueOf(entityName).replace("'", "\\'");

                // 再替换双引号
                entityName = String.valueOf(entityName).replace("\"", "\\\"");
                return entityName;
            } else {
                return object;
            }
        } else {
            return object;
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(按照D3格式打包数据)
     */
    public static JSONObject packD3Json(JSONObject result) {
        if (result != null && !result.isEmpty()) {
            JSONObject queryResult = result.getJSONArray("queryResultList").getJSONObject(0);
            result.put("message", queryResult.getBooleanValue("message"));
            result.put("results", queryResult.getJSONArray("results"));
            result.put("totalNodeSize", queryResult.getIntValue("totalNodeSize"));
            result.put("totalRelationSize", queryResult.getIntValue("totalRelationSize"));
            result.put("errors", queryResult.getJSONArray("errors"));
            result.remove("queryResultList");
            // 重新组合RESULTS

            JSONArray results = result.getJSONArray("results");
            if (!results.isEmpty()) {
                JSONObject resultsVri = results.getJSONObject(0);
                JSONArray data = resultsVri.getJSONArray("data");

                JSONArray relationships = new JSONArray();
                JSONArray nodes = new JSONArray();
                JSONArray properties = new JSONArray();

                data.stream().forEach(v -> {
                    JSONObject graph = (JSONObject) v;
                    relationships.addAll(graph.getJSONObject("graph").getJSONArray("relationships"));
                    nodes.addAll(graph.getJSONObject("graph").getJSONArray("nodes"));
                    properties.addAll(graph.getJSONObject("graph").getJSONArray("properties"));
                });

                data.clear();
                JSONObject relaNodes = new JSONObject();
                relaNodes.put("relationships", distinctRelation(relationships));
                relaNodes.put("nodes", distinctAndRemoveNull(nodes));
                relaNodes.put("properties", properties);

                JSONObject graph = new JSONObject();
                graph.put("graph", relaNodes);
                data.add(graph);
            }

            return result;
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: TODO(排重关系)
     */
    private static JSONArray distinctRelation(JSONArray relationships) {
        return relationships.parallelStream().filter(v -> v != null).filter(distinctById(v -> {
            JSONObject object = (JSONObject) v;
            if (object != null && object.containsKey("id")) {
                return object.getString("id");
            } else {
                return null;
            }
        })).collect(Collectors.toCollection(JSONArray::new));
    }

    /**
     * @param
     * @return
     * @Description: TODO(排重节点并去掉标签的null值)
     */
    private static JSONArray distinctAndRemoveNull(JSONArray nodes) {
        nodes.removeIf(v -> v == null);
        if (!nodes.isEmpty()) {
            return nodes.parallelStream().filter(distinctById(v -> {
                JSONObject object = (JSONObject) v;
                if (object != null) {
                    return object.getString("id");
                } else {
                    return null;
                }
            })).map(v -> {
                JSONObject object = (JSONObject) v;
                JSONArray labels = object.getJSONArray("labels");
                labels = labels.parallelStream().filter(obj -> obj != null).collect(Collectors.toCollection(JSONArray::new));
                object.put("labels", labels);
                return object;
            }).sorted((object1, object2) -> {
                // searchEngineWeight排序
                JSONObject nodePro1 = object1.getJSONObject("properties");
                JSONObject nodePro2 = object2.getJSONObject("properties");
                Double dou1 = nodePro1.getDouble("searchEngineWeight");
                Double dou2 = nodePro2.getDouble("searchEngineWeight");
                return weightCompare(dou1, dou2);

            }).collect(Collectors.toCollection(JSONArray::new));
        } else {
            return nodes;
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(权重比较)
     */
    private static int weightCompare(Double d1, Double d2) {
        Optional<Double> dou1 = Optional.ofNullable(d1);
        Optional<Double> dou2 = Optional.ofNullable(d2);
        Integer int1 = 0, int2 = 0;
        if (dou1.orElse(0.0).intValue() == dou2.orElse(0.0).intValue()) {
            if (dou1.orElse(0.0) > dou2.orElse(0.0)) {
                int1 = dou1.orElse(0.0).intValue() + 1;
                int2 = dou2.orElse(0.0).intValue();
            } else if (dou1.orElse(0.0) < dou2.orElse(0.0)) {
                int1 = dou1.orElse(0.0).intValue();
                int2 = dou2.orElse(0.0).intValue() + 1;
            }
        } else {
            int1 = dou1.orElse(0.0).intValue();
            int2 = dou2.orElse(0.0).intValue();
        }
        // SEARCH ENGINE WEIGHT RESULT ASC
        return int2 - int1;
    }

    /**
     * @param
     * @return
     * @Description: TODO(对节点集通过ID去重)
     */
    public static <T> Predicate<T> distinctById(Function<? super T, ?> idExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(idExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
     * @param
     * @return
     * @Description: TODO(对返回的D3格式数据进行排序)
     */

    /**
     * @param resultObject:结果集合
     * @param key:拿到节点或者关系集合    relationships-获取关系列表 nodes-获取节点列表
     * @return
     * @Description: TODO(从结果集解析NODE列表 - 判断结果集是否NODES为空)
     */
    public static JSONArray getNodeOrRelaList(JSONObject resultObject, String key) {
        if (resultObject != null) {
            JSONArray jsonArray = resultObject.getJSONArray("results");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray jsonArray1 = jsonObject.getJSONArray("data");
            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
            JSONObject jsonObject2 = jsonObject1.getJSONObject("graph");
            return jsonObject2.getJSONArray(key);
        }
        return new JSONArray();
    }

    /**
     * @param resultObject:结果集合
     * @param nodesOrRelas:需要被放回的节点列表或关系列表
     * @param key:拿到节点或者关系集合               relationships-获取关系列表 nodes-获取节点列表
     * @return
     * @Description: TODO(从结果集解析NODE列表 - 判断结果集是否NODES为空)
     */
    public static JSONObject putNodeOrRelaList(JSONObject resultObject, JSONArray nodesOrRelas, String key) {
        if (resultObject != null) {
            JSONArray jsonArray = resultObject.getJSONArray("results");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray jsonArray1 = jsonObject.getJSONArray("data");
            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
            JSONObject jsonObject2 = jsonObject1.getJSONObject("graph");
            jsonObject2.put(key, nodesOrRelas);
            return resultObject;
        }
        return resultObject;
    }

    /**
     * @param result:D3格式数据
     * @param reserveLabels:需要保留的标签数组
     * @return
     * @Description: TODO(结果集里面过滤 （ 只保留指定标签的节点 ）)
     */
    public static JSONObject filterD3GraphDataByNodeLabel(JSONObject result, Label[] reserveLabels) {
        List<String> labelList = new ArrayList<>();
        for (int i = 0; i < reserveLabels.length; i++) {
            Label reserveLabel = reserveLabels[i];
            labelList.add(reserveLabel.name());
        }
        JSONArray nodeList = getNodeOrRelaList(result, "nodes");
        JSONArray sortNodeList = nodeList.stream().filter(v -> {
            JSONObject node = (JSONObject) v;
            JSONArray labels = node.getJSONArray("labels");
            if (hasLabel(labels, labelList))
                return true;
            return false;
        }).collect(Collectors.toCollection(JSONArray::new));

        return putNodeOrRelaList(result, sortNodeList, "nodes");
    }

    /**
     * @param labels:原始数据的标签
     * @param reserveLabels:需要被保留的节点标签数组
     * @return
     * @Description: TODO(标签包含判断)
     */
    private static boolean hasLabel(JSONArray labels, List<String> reserveLabels) {
        for (int i = 0; i < labels.size(); i++) {
            String label = (String) labels.get(i);
            if (reserveLabels.contains(label)) {
                return true;
            }
        }
        return false;
    }

    public static JSONObject tansferGenericPara(Object[] config) {
        if (config.length % 2 != 0) throw new IllegalArgumentException();
        JSONObject paraMap = new JSONObject();
        for (int i = 0; i < config.length; i++) {
            Object para = config[i];
            try {
                paraMap.put(String.valueOf(para), config[i + 1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        return paraMap;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取节点集的最大ID)
     */
    public static long getMaxNodeId(JSONArray nodes) {
        Optional optional = nodes.parallelStream().max(Comparator.comparingInt(v -> {
            JSONObject object = (JSONObject) v;
            return object.getInteger("id");
        }));
        return optional.isPresent() ? JSONObject.parseObject(String.valueOf(optional.get())).getLongValue("id") : -1;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取节点集的最小ID)
     */
    // parallelStream里直接去修改变量是非线程安全的，但是采用collect和reduce操作就是满足线程安全的
    public static long getMinNodeId(JSONArray nodes) {
        Optional optional = nodes.parallelStream().min(Comparator.comparingInt(v -> {
            JSONObject object = (JSONObject) v;
            return object.getInteger("id");
        }));
        return optional.isPresent() ? JSONObject.parseObject(String.valueOf(optional.get())).getLongValue("id") : -1;
    }

    /**
     * @param
     * @return
     * @Description: TODO(重新统计节点和关系数量)
     */
    public static JSONObject recountD3NodeRelation(JSONObject traversal) {
        traversal.put("totalNodeSize", getNodeOrRelaList(traversal, "nodes").size());
        traversal.put("totalRelationSize", getNodeOrRelaList(traversal, "relationships").size());
        return traversal;
    }

    /**
     * @param result:原始数据-高版本D3格式封装的数据
     * @return
     * @Description: TODO(转换为d3 - 3.2.8支持的格式数据)
     */
    public static JSONObject transferToOtherD3(JSONObject result) {
        JSONObject data = new JSONObject();
        HashMap<Long, Integer> nodeIndexMap = packNodeIndexMap(getNodeOrRelaList(result, "nodes"));
        JSONArray nodes = getNodeOrRelaList(result, "nodes")
                .parallelStream()
                .map(v -> {
                    JSONObject node = (JSONObject) v;
                    JSONObject properties = node.getJSONObject("properties");
                    node.put("index", nodeIndexMap.get(node.getLongValue("id")));
                    String name = properties.getString("name") == null ? "" : properties.getString("name");
                    String _entity_name = properties.getString("_entity_name") == null ? "" : properties.getString("_entity_name");
                    node.put("name", name + _entity_name);
                    //
//                    node.put("image", "default.png");
                    node.put("image", "node-image-4.png");
//                    node.put("image", "twitter.svg");
                    node.remove("properties");
                    node.remove("labels");
                    return node;
                })
                .collect(Collectors.toCollection(JSONArray::new));
        JSONArray relationships = getNodeOrRelaList(result, "relationships")
                .parallelStream()
                .map(v -> {
                    JSONObject relation = (JSONObject) v;
                    relation.put("sourceId", relation.getLongValue("startNode"));
                    relation.put("targetId", relation.getLongValue("endNode"));
                    relation.put("source", nodeIndexMap.get(relation.getLongValue("startNode")));
                    relation.put("target", nodeIndexMap.get(relation.getLongValue("endNode")));
                    relation.remove("startNode");
                    relation.remove("endNode");
                    relation.remove("properties");
                    return relation;
                })
                .collect(Collectors.toCollection(JSONArray::new));

        data.put("nodes", nodes);
        data.put("links", relationships);
        return data;
    }

    private static HashMap<Long, Integer> packNodeIndexMap(JSONArray nodes) {
        HashMap<Long, Integer> map = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            map.put(node.getLongValue("id"), i);
        }
        return map;
    }

    /**
     * @param filterId:被过滤的节点ID
     * @return
     * @Description: TODO(打包节点IDS)
     */
    public static List<Long> packNodeIds(JSONObject result, long filterId) {
        JSONArray nodes = getNodeOrRelaList(result, "nodes");
        return nodes.parallelStream()
                .map(v -> {
                    JSONObject node = (JSONObject) v;
                    return node.getLongValue("id");
                })
                .filter(v -> v != filterId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param
     * @return
     * @Description: TODO(打包节点IDS)
     */
    public static List<Long> packNodeIds(JSONObject result) {
        JSONArray nodes = getNodeOrRelaList(result, "nodes");
        return nodes.parallelStream()
                .map(v -> {
                    JSONObject node = (JSONObject) v;
                    return node.getLongValue("id");
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static JSONArray resultRetrievePro(JSONObject clusterObject) {
        return clusterObject.getJSONArray("queryResultList").getJSONObject(0).getJSONArray("retrieve_properties");
    }

    /**
     * @param labelsTreeResult:标签树数据
     * @param leafResult:叶子节点
     * @return
     * @Description: TODO(标签树叶子节点MAP)
     */
    public static Map<Label, List<Label>> labelsTreeChildMap(JSONObject labelsTreeResult, JSONObject leafResult) {
        Map<Label, List<Label>> leafLabelsMap = new HashMap<>();
        JSONArray leafLabels = getNodeOrRelaList(leafResult, "nodes");
        for (Object node : leafLabels) {
            JSONObject nodeLeaf = (JSONObject) node;
            JSONObject properties = nodeLeaf.getJSONObject("properties");
            String label = properties.getString("labelName");
            List<Label> fatherLabels = getFatherLabel(labelsTreeResult, nodeLeaf);
            leafLabelsMap.put(Label.label(label), fatherLabels);
        }
        return leafLabelsMap;
    }

    private static List<Label> getFatherLabel(JSONObject labelsTreeResult, JSONObject nodeLeaf) {

        List<Label> fatherLabels = new ArrayList<>();

        long nodeLeafId = nodeLeaf.getLongValue("id");
        JSONArray relations = getNodeOrRelaList(labelsTreeResult, "relationships");
        JSONArray nodes = getNodeOrRelaList(labelsTreeResult, "nodes");

        List<Long> relationIds = getRelationIds(relations, nodeLeafId);
        if (!relationIds.isEmpty()) {
            long relationId = relationIds.get(0);
            fatherLabels.add(getLabelName(nodes, relationId));

            List<Long> relationIds2 = getRelationIds(relations, relationId);
            if (!relationIds2.isEmpty()) {
                long relationId2 = relationIds2.get(0);
                fatherLabels.add(0, getLabelName(nodes, relationId2));
            }
        }

        return fatherLabels;
    }

    private static Label getLabelName(JSONArray nodes, long relationId2) {
        List<String> filterLabel = nodes.parallelStream()
                .filter(v -> {
                    JSONObject node = (JSONObject) v;
                    long id = node.getLongValue("id");
                    return id == relationId2;
                })
                .map(v -> {
                    JSONObject node = (JSONObject) v;
                    return node.getJSONObject("properties").getString("labelName");
                })
                .collect(Collectors.toCollection(ArrayList::new));
        if (!filterLabel.isEmpty()) {
            return Label.label(filterLabel.get(0));
        }
        return null;
    }

    private static List<Long> getRelationIds(JSONArray relations, long nodeLeafId) {
        return relations.parallelStream()
                .filter(v -> {
                    JSONObject relation = (JSONObject) v;
                    long endNodeId = relation.getLongValue("endNode");
                    return endNodeId == nodeLeafId;
                })
                .map(v -> {
                    JSONObject relation = (JSONObject) v;
                    return relation.getLongValue("startNode");
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param leafLabelsMap:标签树叶子节点MAP，KEY是叶子节点，VALUE是叶子节点的所有父级节点
     * @param result:一些检索到的数据
     * @return
     * @Description: TODO(根据标签树补充父级标签)
     */
    public static JSONObject supplyFatherLabels(Map<Label, List<Label>> leafLabelsMap, JSONObject result) {
        JSONArray nodesTransfer = getNodeOrRelaList(result, "nodes")
                .parallelStream()
                .map(v -> {
                    JSONObject node = (JSONObject) v;
                    JSONArray labels = node.getJSONArray("labels");
                    node.put("labels", mergeFatherLabels(leafLabelsMap, labels));
                    return node;
                })
                .collect(Collectors.toCollection(JSONArray::new));
        putNodeOrRelaList(result, nodesTransfer, "nodes");
        return result;
    }

    private static JSONArray mergeFatherLabels(Map<Label, List<Label>> leafLabelsMap, JSONArray labels) {
        if (labels.size() == 1 && leafLabelsMap.containsKey(Label.label(labels.getString(0)))) {
            String childLabel = labels.getString(0);
            JSONArray fatherLabels = leafLabelsMap.get(Label.label(childLabel))
                    .parallelStream()
                    .map(v -> v.name())
                    .collect(Collectors.toCollection(JSONArray::new));
            fatherLabels.add(childLabel);
            return fatherLabels;
        } else {
            return labels;
        }
    }

    /**
     * @param result1:第一次的检索结果
     * @param result2:第二次的检索结果
     * @return
     * @Description: TODO(合并两个检索结果)
     */
    public static JSONObject mergeResult(JSONObject result1, JSONObject result2) {

        if (!valueCheck(result1) || !valueCheck(result2)) {
            throw new IllegalArgumentException();
        }

        final String NODE_KEY = "nodes";
        final String RELATIONSHIP_KEY = "relationships";
        final String NODE_SIZE = "totalNodeSize";
        final String RELATIONSHIP_SIZE = "totalRelationSize";

        // --NODES--
        JSONArray node1 = getNodeOrRelaList(result1, NODE_KEY);
        JSONArray node2 = getNodeOrRelaList(result2, NODE_KEY);
        // MERGE
        JSONArray mergeNodes = mergeDistinct(node1, node2);

        // --RELATIONSHIPS--
        JSONArray relationships1 = getNodeOrRelaList(result1, RELATIONSHIP_KEY);
        JSONArray relationships2 = getNodeOrRelaList(result2, RELATIONSHIP_KEY);

        // MERGE
        JSONArray mergeRelationships = mergeDistinct(relationships1, relationships2);

        // --PUT--
        putNodeOrRelaList(result1, mergeNodes, NODE_KEY);
        putNodeOrRelaList(result1, mergeRelationships, RELATIONSHIP_KEY);
        // --MODIFY STATISTICS--
        if (result1.containsKey(NODE_SIZE)) {
            result1.put(NODE_SIZE, mergeNodes.size());
        }
        if (result1.containsKey(RELATIONSHIP_SIZE)) {
            result1.put(RELATIONSHIP_SIZE, mergeRelationships.size());
        }
        return result1;
    }

    private static boolean valueCheck(JSONObject result) {
        return result != null && !result.isEmpty();
    }

    /**
     * @param
     * @return
     * @Description: TODO(合并且去重两个相似的集合)
     */
    private static JSONArray mergeDistinct(JSONArray assemble1, JSONArray assemble2) {
        assemble1.addAll(assemble2);
        return assemble1.parallelStream().distinct()
                .collect(Collectors.toCollection(JSONArray::new));
    }

    public static boolean isNeoD3ObjEmpty(JSONObject result) {
        JSONArray nodes = getNodeOrRelaList(result, "nodes");
        return nodes == null || nodes.isEmpty();
    }

    public static JSONArray removeNull(JSONArray relationships) {
        return relationships.stream().filter(rela -> {
            JSONObject object = (JSONObject) rela;
            String end = object.getString("endNode");
            String startNode = object.getString("startNode");
            return (end != null && !"".equals(end)) && (startNode != null && !"".equals(startNode));
        }).collect(Collectors.toCollection(JSONArray::new));
    }
}


