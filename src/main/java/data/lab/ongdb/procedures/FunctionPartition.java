package data.lab.ongdb.procedures;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.procedures
 * @Description: TODO
 * @date 2020/11/18 10:58
 */
public class FunctionPartition {
    /**
     * @param fields:字段列表
     * @param items:数据集合列表
     * @return
     * @Description: TODO(CSV格式转为mapList)
     */
    @UserFunction(name = "olab.structure.mergeToListMap")
    @Description("【CSV格式转为mapList】【数据封装格式转换】@olab.structure.mergeToListMap(['area_code','author'],[['001','HORG001'],['002','HORG002']])")
    public String structureMergeToListMap(@Name("fields") List<Object> fields, @Name("items") List<List<Object>> items) {
        List<Map<Object, Object>> mapList = new ArrayList<>();
        int size = fields.size();
        for (List<Object> list : items) {
            Map<Object, Object> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                map.put(fields.get(i), list.get(i));
            }
            mapList.add(map);
        }
        return JSONArray.parseArray(JSON.toJSONString(mapList)).toJSONString();
    }
}


