package data.lab.ongdb.util;

import org.junit.Test;

import java.util.Arrays;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.DateHandleTest
 * @Description: TODO
 * @date 2020/5/22 10:52
 */
public class DateUtilTest {
    @Test
    public void test01() {
        String input = "origina||english";
        Arrays.stream(input.split("\\|\\|")).parallel().forEach(c -> {
            System.out.println(c);
        });
    }

    @Test
    public void test02() {
        String input = "origina&&english";
        Arrays.stream(input.split("&&")).parallel().forEach(c -> {
            System.out.println(c);
        });
    }

    @Test
    public void test03() {
        System.out.println(DateUtil.standardizeDate("asdsa",false));
        System.out.println(DateUtil.standardizeDate("2020",false));
        System.out.println(DateUtil.standardizeDate("202012",false));
        System.out.println(DateUtil.standardizeDate("20201206",false));
        System.out.println(DateUtil.standardizeDate("2020120612",false));
        System.out.println(DateUtil.standardizeDate("202012061201",false));
        System.out.println(DateUtil.standardizeDate("asdsa",true));
        System.out.println(DateUtil.standardizeDate("2020",true));
        System.out.println(DateUtil.standardizeDate("202012",true));
        System.out.println(DateUtil.standardizeDate("20201206",true));
        System.out.println(DateUtil.standardizeDate("2020120612",true));
        System.out.println(DateUtil.standardizeDate("202012061201",true));
        System.out.println(DateUtil.standardizeDate("202012061201",true));
        System.out.println(DateUtil.standardizeDate("2020-11-26 08:47:38.0",true));
        System.out.println(DateUtil.standardizeDate( "2020-11-26T08:47:38",true));
    }
}


