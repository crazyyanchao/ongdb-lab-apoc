package data.lab.ongdb.index;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.wltea.analyzer.cfg.Configuration;
import data.lab.wltea.analyzer.core.IKSegmenter;
import data.lab.wltea.analyzer.core.Lexeme;
import org.apache.log4j.PropertyConfigurator;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.index.FreetextIK
 * @Description: TODO(分词函数)
 * @date 2020/5/22 10:25
 */
public class FreetextIK {

    /**
     * @param text:待分词文本
     * @param useSmart:true 用智能分词，false 细粒度分词
     * @return
     * @Description: TODO(支持中英文本分词)
     */
    @UserFunction(name = "olab.index.iKAnalyzer")
    @Description("Fulltext index iKAnalyzer - RETURN olab.index.iKAnalyzer({text},true) AS words")
    public List<String> iKAnalyzer(@Name("text") String text, @Name("useSmart") boolean useSmart) {

        PropertyConfigurator.configureAndWatch("dic" + File.separator + "log4j.properties");
        Configuration cfg = new Configuration(useSmart);

        StringReader input = new StringReader(text.trim());
        IKSegmenter ikSegmenter = new IKSegmenter(input, cfg);

        List<String> results = new ArrayList<>();
        try {
            for (Lexeme lexeme = ikSegmenter.next(); lexeme != null; lexeme = ikSegmenter.next()) {
                results.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

}
