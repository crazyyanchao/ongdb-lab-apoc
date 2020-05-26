package data.lab.ongdb.similarity;

import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity
 * @Description: TODO
 * @date 2020/5/26 14:14
 */
public class EditDistanceTest {

    /**
     * 主要计算英文短文本相似性
     * cn:0.9
     * en:0.8
     **/

    @Test
    public void main() {
        String str1 = "Google M Inc.";
        String str2 = "Google T Inc.";
        int result = EditDistance.minDistance(str1, str2);
        String res = EditDistance.evaluate(str1, str2);
        System.out.println("Min distance:" + result);
        System.out.println(str1 + "与" + str2 + "相似度为：" + res);
    }

    @Test
    public void isSimilarityThresholdEn() {
        boolean isSimilarity = EditDistance.isSimilarityThreshold("Google M Inc.", "Google T Inc.", 0.9);
        System.out.println(isSimilarity);
    }

    @Test
    public void isSimilarityThresholdCn() {
        boolean isSimilarity = EditDistance.isSimilarityThreshold("阿里巴巴股份有限公司", "阿里股份有限公司", 0.85);
        System.out.println(isSimilarity);
    }
}

