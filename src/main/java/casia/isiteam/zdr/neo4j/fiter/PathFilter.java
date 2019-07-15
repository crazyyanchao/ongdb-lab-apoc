package casia.isiteam.zdr.neo4j.fiter;
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

import org.neo4j.graphdb.*;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.*;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.fiter
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/7/12 12:07
 */
public class PathFilter {
    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    /**
     * @param node:目标节点列表
     * @param filterLabels:目标节点必须满足的标签（任意满足一个即可）
     * @return 路径中必须要有这些标签filterLabels类型的节点
     * @Description: TODO(通过关系和节点标签过滤路径 - 寻找满足条件的点)
     */
    @UserFunction(name = "casia.apoc.targetPathFilterByNodeLabels")
    @Description("Filter target path by node labels")
    public boolean targetPathFilterByNodeLabels(@Name("node") List<Node> node, @Name("conditionLabels") List<String> filterLabels) {

        List<Boolean> booleanList = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            Node n = node.get(i);
            boolean isContainLabel = isPathNode(n, filterLabels);
            if (isContainLabel) {
                booleanList.add(isContainLabel);
            }
        }

        if (booleanList.size() >= filterLabels.size()) {
            return true;
        }
        return false;
    }

    /**
     * @param node:当前节点
     * @param filterLabels:当前节点标签包含在此标签列表即可
     * @return
     * @Description: TODO(过滤节点)
     */
    protected boolean isPathNode(Node node, List<String> filterLabels) {
        if (filterLabels == null) {
            return true;
        }
        Iterable<Label> labels = node.getLabels();
        for (Iterator<Label> iterator = labels.iterator(); iterator.hasNext(); ) {
            Label label = iterator.next();
            if (filterLabels.contains(label.name())) {
                return true;
            }
        }
        return false;
    }

}

