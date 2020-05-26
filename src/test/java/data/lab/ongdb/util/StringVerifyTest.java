package data.lab.ongdb.util;

import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util
 * @Description: TODO
 * @date 2020/5/26 18:28
 */
public class StringVerifyTest {

    @Test
    public void isEnglish() {
        System.out.println(StringVerify.isEnglish("公司股票于1996年7月15日上市")); // false
        System.out.println(StringVerify.isChinese("公司股票于1996年7月15日上市》*&……876")); // true

        System.out.println(StringVerify.isEnglish("GoogleMInc")); // true
        System.out.println(StringVerify.isChinese("GoogleMInc.")); // false
    }

}


