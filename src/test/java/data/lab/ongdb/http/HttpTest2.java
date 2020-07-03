package data.lab.ongdb.http;

import data.lab.ongdb.http.extra.HttpRequest;
import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http
 * @Description: TODO
 * @date 2020/7/3 9:26
 */
public class HttpTest2 {

    private HttpRequest httpRequest = new HttpRequest();

    @Test
    public void post() {
    }

    @Test
    public void get() {
        String result = httpRequest.httpGet("https://localhost:7424/pre_org_cn_node//_search?q=name:吉林白山航空发展股份有限公司+hcode:HORGdcb7f837be6b845725f67a598ebea1b6");
        System.out.println(result);
    }

    @Test
    public void put() {
    }

    @Test
    public void delete() {
    }
}

