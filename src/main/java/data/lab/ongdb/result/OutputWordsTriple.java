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
 * @date 2020/5/22 13:46
 */
public class OutputWordsTriple {
    public List<String> wordF;
    public List<String> wordT;
    public List<String> wordR;

    public OutputWordsTriple(List<String> wordF, List<String> wordT, List<String> wordR) {
        this.wordF = wordF;
        this.wordT = wordT;
        this.wordR = wordR;
    }
}
