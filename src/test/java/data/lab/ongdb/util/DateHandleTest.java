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
 * @PACKAGE_NAME: data.lab.ongdb.neo4j.util
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/3/11 10:32
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