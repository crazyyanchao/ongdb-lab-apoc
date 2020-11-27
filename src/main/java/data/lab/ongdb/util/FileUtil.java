package data.lab.ongdb.util;

/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.github.houbb.opencc4j.util.ZhConverterUtil;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.FileUtil
 * @Description: TODO(文件操作)
 * @date 2020/11/11 17:26
 */
public class FileUtil {

    private static final Map<String, String> ENCODE_ENC_CNC = new HashMap<>();

    static {
        // a b c d e f g h i j k l m n o p q r s t u v w x y z
        ENCODE_ENC_CNC.put("a", "啊");
        ENCODE_ENC_CNC.put("b", "吧");
        ENCODE_ENC_CNC.put("c", "从");
        ENCODE_ENC_CNC.put("d", "的");
        ENCODE_ENC_CNC.put("e", "额");
        ENCODE_ENC_CNC.put("f", "分");
        ENCODE_ENC_CNC.put("g", "个");
        ENCODE_ENC_CNC.put("h", "好");
        ENCODE_ENC_CNC.put("i", "哎");
        ENCODE_ENC_CNC.put("j", "就");
        ENCODE_ENC_CNC.put("k", "看");
        ENCODE_ENC_CNC.put("l", "了");
        ENCODE_ENC_CNC.put("m", "吗");
        ENCODE_ENC_CNC.put("n", "你");
        ENCODE_ENC_CNC.put("o", "哦");
        ENCODE_ENC_CNC.put("p", "跑");
        ENCODE_ENC_CNC.put("q", "去");
        ENCODE_ENC_CNC.put("r", "人");
        ENCODE_ENC_CNC.put("s", "是");
        ENCODE_ENC_CNC.put("t", "他");
        ENCODE_ENC_CNC.put("u", "呕");
        ENCODE_ENC_CNC.put("v", "卫");
        ENCODE_ENC_CNC.put("w", "万");
        ENCODE_ENC_CNC.put("x", "许");
        ENCODE_ENC_CNC.put("y", "燕");
        ENCODE_ENC_CNC.put("z", "置");
    }

    private static final Pattern PATTERN_MATCH_CnEn = Pattern.compile("[\\u4e00-\\u9fa5a-zA-Z]");

    /**
     * 从文件中读取最后处理的id
     *
     * @param lastIdFileName
     * @return
     */
    public static int getLastId(String lastIdFileName) {
        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File lastIdFile = new File(dir, lastIdFileName);
        if (!lastIdFile.getAbsoluteFile().exists()) {
            try {
                lastIdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
        FileReader reader = null;
        String id = null;
        try {
            reader = new FileReader(lastIdFile);
            char[] buffer = new char[12];
            int read = reader.read(buffer);
            if (read == -1) {
                id = "-1";
            } else {
                char[] realBuffer = new char[read];
                System.arraycopy(buffer, 0, realBuffer, 0, read);
                id = new String(realBuffer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(reader);
        }
        if (id != null && !"".equals(id)) {
            id = id.replace("\r\n", "");
            return Integer.parseInt(id);
        }
        return -1;
    }

    /**
     * 更新最新处理的id
     *
     * @param filaname
     * @param autoId
     */
    public static void updateCursor(String filaname, String autoId) {

        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filaname);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(autoId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static String getFileContent(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(reader);
        }
        return sb.toString();
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static Set<String> getFileContentByLine(File file) {
        Set<String> set = new HashSet<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if ("".equals(line.trim())) {
                    continue;
                }
                set.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(reader);
        }
        return set;
    }

    /**
     * 读取文件首行
     *
     * @param filePath
     * @return
     */
    public static String getFirstLine(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line;
            if ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(reader);
        }
        return null;
    }


    /**
     * 按行读取文件
     *
     * @param fileName 文件名
     * @return List<String> 行列表
     * @throws IOException
     */
    public static List<String> readFileByLine(String fileName) throws IOException {
        List<String> lineList = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lineList.add(line.trim());
        }
        reader.close();
        return lineList;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Read one line)
     */
    public static String readOneLine(String filePath) {
        File file = new File(filePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getLastUpdateTime(String lastTimeFile) {
        String defaultTime = "1970-01-01 00:00:00";
        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File lastIdFile = new File(dir, lastTimeFile);
        if (!lastIdFile.getAbsoluteFile().exists()) {
            try {
                lastIdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // DEFAULT UPDATE TIME
            return defaultTime;
        }
        String line = readOneLine("cursor" + File.separator + lastTimeFile);
        if (line != null && !"".equals(line)) {
            return line;
        }
        return defaultTime;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Read all line)
     */
    public static String readAllLine(String filePath, String encoding) {
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(fileContent);

            return new String(fileContent, encoding);

        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将ids写入文件
     *
     * @param ids
     */
    public static void writeIdsToFile(String ids, String filename) {

        //将IDs写入文件
        try {
            File dir = new File("cursor");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            FileWriter writer = new FileWriter(file, true);
            writer.write(ids + "\r\n");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将ids写入文件
     *
     * @param ids
     */
    public static void writeIdsToFileUTF8(String ids, String filename) {

        //将IDs写入文件
        try {
            File dir = new File("cursor");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
            writer.write(ids + "\r\n");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将ids写入文件
     *
     * @param ids
     */
    public static void writeIdsToFile(String ids, String filename, boolean append) {

        //将IDs写入文件
        try {
            File dir = new File("cursor");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);

            FileWriter writer = new FileWriter(file, append);
            writer.write(ids + "\r\n");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeable = null;
            }
        }
    }


    /**
     * @param
     * @return
     * @Description: TODO(提取英文中文)
     */
    public static String matchCnEn(String source) {
        if (source != null && !"".equals(source)) {
            StringBuilder result = new StringBuilder();
            Matcher m = PATTERN_MATCH_CnEn.matcher(source);
            while (m.find()) {
                String r = m.group(0);
                result.append(r);
            }
            return result.toString();
        }
        return source;
    }

    /**
     * @param
     * @return
     * @Description: TODO(提取英文中文 - 大写转为小写 、 繁体转为简体)
     */
    public static String matchCnEnRinse(String source) {
        if (source != null && !"".equals(source)) {
            String exEnCnString = matchCnEn(source);
            /**
             * 繁体转为简体 -> 大写转为小写
             * **/
            return ZhConverterUtil.toSimple(exEnCnString).toLowerCase();
        }
        return source;
    }

    /**
     * @param
     * @return
     * @Description: TODO(对字符进行编码)
     * 1、提取英文中文
     * 2、大写转为小写 、 繁体转为简体
     * 3、编码字母
     */
    public static String encodeEncCnc(String string) {
        if (string != null && !"".equals(string)) {
            String stringStd = matchCnEnRinse(string);
            char[] chars = stringStd.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char ch : chars) {
                if (ENCODE_ENC_CNC.containsKey(String.valueOf(ch))) {
                    builder.append(ENCODE_ENC_CNC.get(String.valueOf(ch)));
                } else {
                    builder.append(ch);
                }
            }
            return builder.toString();
        }
        return string;
    }

    /**
     * @param
     * @return
     * @Description: TODO(对字符进行编码)
     * 1、提取英文中文
     * 2、大写转为小写 、 繁体转为简体
     * 3、编码字母
     */
    public static String encode(String string) {
        if (string != null && !"".equals(string)) {
            char[] chars = string.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char ch : chars) {
                if (ENCODE_ENC_CNC.containsKey(String.valueOf(ch))) {
                    builder.append(ENCODE_ENC_CNC.get(String.valueOf(ch)));
                } else {
                    builder.append(ch);
                }
            }
            return builder.toString();
        }
        return string;
    }
}

