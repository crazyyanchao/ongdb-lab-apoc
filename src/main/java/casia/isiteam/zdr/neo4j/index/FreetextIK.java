package casia.isiteam.zdr.neo4j.index;/**
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

import casia.isiteam.zdr.wltea.analyzer.cfg.Configuration;
import casia.isiteam.zdr.wltea.analyzer.core.IKSegmenter;
import casia.isiteam.zdr.wltea.analyzer.core.Lexeme;
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
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.index
 * @Description: TODO(分词函数)
 * @date 2019/4/23 9:49
 */
public class FreetextIK {

    /**
     * @param text:待分词文本
     * @param useSmart:true 用智能分词，false 细粒度分词
     * @return
     * @Description: TODO(支持中英文本分词)
     */
    @UserFunction(name = "zdr.index.iKAnalyzer")
    @Description("Fulltext index iKAnalyzer - RETURN zdr.index.iKAnalyzer({text},true) AS words")
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
