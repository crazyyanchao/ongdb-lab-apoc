package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.NodeHandle
 * @Description: TODO(操作节点)
 * @date 2020/5/22 10:32
 */
public class NodeHandle {

    /**
     * @param
     * @return
     * @Description: TODO(节点集合排重)
     */
    public List<Node> distinctNodes(List<Node> nodes) {
        return nodes.stream()
                .filter(distinctById(v -> {
                    Node node = v;
                    return node.getId();
                }))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param
     * @return
     * @Description: TODO(对节点集通过ID去重)
     */
    private static <T> Predicate<T> distinctById(Function<? super T, ?> idExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(idExtractor.apply(t), Boolean.TRUE) == null;
    }

}
