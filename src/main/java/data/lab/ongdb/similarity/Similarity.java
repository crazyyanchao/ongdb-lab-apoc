package data.lab.ongdb.similarity;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.similarity.simhash.SimHash;
import org.neo4j.procedure.*;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity.Similarity
 * @Description: TODO
 * @date 2020/5/22 18:15
 */
public class Similarity {

    /**
     * @param text:函数参数
     * @return
     * @Description: TODO(为文本生成simhash值)
     */
    @UserFunction(name = "olab.simhash")
    @Description("produce sim hash!")
    public String produceSimHash(@Name("text") String text) {
        return SimHash.hash().setText(text).getSimHash();
    }
}

