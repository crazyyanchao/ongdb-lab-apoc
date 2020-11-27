package data.lab.ongdb.similarity.simhash;

import data.lab.wltea.analyzer.cfg.Configuration;
import data.lab.wltea.analyzer.core.IKSegmenter;
import data.lab.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档分词
 *
 * @author louxuezheng@hotmail.com
 */
public class WordsSegment {
    /**
     * 分词
     *
     * @param query 字符串
     * @return
     */
    public static List<String> getCutWords(String query) {

        List<String> words = new ArrayList<>();

        boolean useSmart = false; // true 用智能分词，false 细粒度

        Configuration cfg = new Configuration(useSmart);
        StringReader input = new StringReader(query.trim());
        IKSegmenter ikSegmenter = new IKSegmenter(input, cfg);

        try {
            for (Lexeme lexeme = ikSegmenter.next(); lexeme != null; lexeme = ikSegmenter.next()) {
                words.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }
}