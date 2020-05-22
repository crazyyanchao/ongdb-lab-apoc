package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.neo4j.result
 * @Description: TODO(好友统计结果返回)
 * @date 2019/3/30 21:55
 */
public class NodeFriendCountList {

    public final List<Map<String, Object>> list;

    public NodeFriendCountList(List<Map<String, Object>> value) {
        this.list = value;
    }
}
