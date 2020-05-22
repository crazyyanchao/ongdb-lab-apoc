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
 * @PACKAGE_NAME: data.lab.ongdb.result.NodeFriendCountList
 * @Description: TODO(好友统计结果返回)
 * @date 2020/5/22 10:30
 */
public class NodeFriendCountList {

    public final List<Map<String, Object>> list;

    public NodeFriendCountList(List<Map<String, Object>> value) {
        this.list = value;
    }
}
