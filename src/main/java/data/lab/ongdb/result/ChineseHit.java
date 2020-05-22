package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.graphdb.Node;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.neo4j.result
 * @Description: TODO(中文全文检索结果)
 * @date 2019/4/11 14:11
 */
public class ChineseHit {
    public final Node node;
    public final double weight;

    public ChineseHit(Node node, double weight) {
        this.weight = weight;
        this.node = node;
    }
}
