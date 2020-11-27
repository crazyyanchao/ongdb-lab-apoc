package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO
 * @date 2020/5/22 13:37
 */
public class OutputWordsCouple {
    public List<String> wordF;
    public List<String> wordT;
    public OutputWordsCouple(List<String> wordF,List<String> wordT) {
        this.wordF = wordF;
        this.wordT = wordT;
    }
}
