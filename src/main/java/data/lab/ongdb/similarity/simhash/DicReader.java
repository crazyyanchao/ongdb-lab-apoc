package data.lab.ongdb.similarity.simhash;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity.simhash.DicReader
 * @Description: TODO(文档读入工具)
 * @date 2020/5/22 15:15
 */
public class DicReader {

    private static Logger logger = Logger.getLogger(DicReader.class);

    private DicReader() {
    }

    /**
     * 返回BufferedReader
     *
     * @param name 文件名
     * @return
     */
    public static BufferedReader getReader(String name) {

        //BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(idfPath), "utf-8"));
        try {
            InputStream in = new FileInputStream(name);
            //InputStream in = DicReader.class.getResourceAsStream("/" + name);

            return new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (Exception e) {
            logger.error("编码格式不支持：" + e.getMessage());
        }
        return null;
    }

    /**
     * 返回InputStream
     *
     * @param name 文件名
     * @return
     */
    public static InputStream getInputStream(String name) {
        InputStream in = DicReader.class.getResourceAsStream("/" + name);
        return in;
    }
}
