package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.ChineseVerify
 * @Description: TODO(中文文本校验)
 * @date 2020/5/22 10:32
 */
public class StringVerify {

    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字 也可以判断标点符号
     */
    public boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判定输入的是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * @param string:输入字符串
     * @return
     * @Description: TODO（是否英文）
     */
    public static boolean isEnglish(String string) {
        return string.matches("^[a-zA-Z]*");
    }

    /**
     * @param string:输入字符串
     * @return
     * @Description: TODO（是否中文）
     */
    public static boolean isChinese(String string) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.find();
    }
}
