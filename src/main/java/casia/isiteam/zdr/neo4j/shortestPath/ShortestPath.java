package casia.isiteam.zdr.neo4j.shortestPath;
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

import casia.isiteam.zdr.neo4j.result.AllShortestPathsResult;
import org.neo4j.kernel.api.exceptions.InvalidArgumentsException;
import org.neo4j.procedure.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.shortestPath
 * @Description: TODO(最短路径相关)
 * @date 2019/4/8 18:39
 */
public class ShortestPath {

    @Procedure(name = "zdr.shortestPath.allPathsTightness", mode = Mode.READ)
    @Description("algo.allShortestPaths.stream result analysis")
    public Stream<AllShortestPathsResult> allPathsTightness(@Name("sourceList") List<Long> sourceList, @Name("targetList") List<Long> targetList, @Name("distanceList") List<Double> distanceList) throws InvalidArgumentsException, IOException {

        List<AllShortestPathsResult> results = new ArrayList<>();
        int size = sourceList.size();
        if (targetList.size() == size && distanceList.size() == size) {

            for (int i = 0; i < size; i++) {
                AllShortestPathsResult result = new AllShortestPathsResult(sourceList.get(i), targetList.get(i), distanceList.get(i), 0);
                if (!results.contains(result)) {
                    results.add(result);
                }else {
                    for (int j = 0; j < results.size(); j++) {
                        AllShortestPathsResult allShortestPathsResult =  results.get(j);
                        if (allShortestPathsResult.equals(result)){
                            Number number = allShortestPathsResult.getTightnessSort().intValue()+1;
                            allShortestPathsResult.setTightnessSort(number);
                        }
                    }

                }
            }
        }
        return results.stream();
    }

}


