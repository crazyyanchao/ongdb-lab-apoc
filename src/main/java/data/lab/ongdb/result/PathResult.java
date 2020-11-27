package data.lab.ongdb.result;

/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.graphdb.Path;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO
 * @date 2020/5/25 20:52
 */
public class PathResult {
    public Path path;

    public Object pathJ;

    public Number count;

    public PathResult() {
    }

    public PathResult(Path path) {
        this.path = path;
    }

    public PathResult(Object path) {
        this.pathJ = path;
    }

    public PathResult(int count) {
        this.count = count;
    }
}
