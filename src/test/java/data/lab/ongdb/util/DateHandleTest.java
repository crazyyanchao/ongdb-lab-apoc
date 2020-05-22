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
public class DateHandleTest {
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
}