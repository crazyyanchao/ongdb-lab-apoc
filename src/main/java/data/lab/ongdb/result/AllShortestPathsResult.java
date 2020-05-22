package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result.AllShortestPathsResult
 * @Description: TODO(全源最短路径结果对象)
 * @date 2020/5/22 10:30
 */
public class AllShortestPathsResult {
    public long source;
    public long target;
    public double distance;
    public Number tightnessSort;

    public AllShortestPathsResult(long source, long target, double distance, Number tightnessSort) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.tightnessSort = tightnessSort;
    }

    public Number getTightnessSort() {
        return tightnessSort;
    }

    public void setTightnessSort(Number tightnessSort) {
        this.tightnessSort = tightnessSort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AllShortestPathsResult result = (AllShortestPathsResult) o;
        return (source == result.source &&
                target == result.target &&
                Double.compare(result.distance, distance) == 0 &&
                Objects.equals(tightnessSort, result.tightnessSort)) || (source == result.target &&
                target == result.source &&
                Double.compare(result.distance, distance) == 0 &&
                Objects.equals(tightnessSort, result.tightnessSort));
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, distance, tightnessSort);
    }

}

