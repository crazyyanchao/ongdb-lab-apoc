package data.lab.ongdb.similarity;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.similarity.simhash.SimHash;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity
 * @Description: TODO
 * @date 2020/5/22 14:38
 */
public class Similarity {
    /**
     * @param
     * @return
     * @Description: TODO(为文本生成simhash值)
     */
    @UserFunction(name = "olab.simhash")
    @Description("Produce simhash")
    public String produceSimHash(@Name("text") String text) {
        return SimHash.hash().setText(text).getSimHash();
    }
}

