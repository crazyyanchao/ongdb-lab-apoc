package casia.isiteam.zdr.neo4j.procedures;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class HelloWorld {

    @Context
    public GraphDatabaseService db;

    /**
     * @param world:函数参数
     * @return
     * @Description: TODO(@ Description的内容会在Neo4j浏览器中调用dbms.functions () 时显示)
     */
    @UserFunction(name = "zdr.apoc.hello") // 自定义函数名
    @Description("hello(world) - Say hello!")   // 函数说明
    public String hello(@Name("world") String world) {
        return String.format("Hello, %s", world);
    }
}
