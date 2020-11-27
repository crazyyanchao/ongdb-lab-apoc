package data.lab.ongdb.http;

import data.lab.ongdb.common.RangeOccurs;
import data.lab.ongdb.http.extra.HttpRequest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, String[]> CONDITION_MAP = new HashMap<>();

    static {
        RangeOccurs[] rangeOccurs = RangeOccurs.values();
        for (RangeOccurs occur : rangeOccurs) {
            String name = occur.name();
            String symbol = occur.getSymbol();
            String condition = occur.getCondition();
            String[] strings = new String[]{name, symbol, condition};
            CONDITION_MAP.put(name, strings);
            CONDITION_MAP.put(symbol, strings);
            CONDITION_MAP.put(condition, strings);
        }
    }

    @Test
    public void test01() {
        for (String key : CONDITION_MAP.keySet()) {
            System.out.println(key);
            System.out.println(CONDITION_MAP.get(key));
        }
    }
}

