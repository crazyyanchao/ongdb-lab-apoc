package data.lab.ongdb.http.extra;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.HttpResponse;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.http.HttpInter
 * @Description: TODO(HTTP INTERFACE)
 * @date 2020/4/28 18:59
 */
public interface HttpInter {

    /**
     * @param url:支持绝对接口地址、相对接口地址、或者同时支持
     * @return
     * @Description: TODO(GET)
     */
    String httpGet(String url);

    /**
     * @param url:支持绝对接口地址、相对接口地址、或者同时支持
     * @param query:DSL查询
     * @return
     * @Description: TODO(POST)
     */
    String httpPost(String url, String query);

    /**
     * @param url:支持绝对接口地址、相对接口地址、或者同时支持
     * @param query:DSL查询
     * @return
     * @Description: TODO(PUT)
     */
    String httpPut(String url, String query);

    /**
     * @param url:支持绝对接口地址、相对接口地址、或者同时支持
     * @param query:DSL查询
     * @return
     * @Description: TODO(DELETE)
     */
    String postDeleteRequest(String url, String query);

    /**
     * @param response:HTTP RESPONSE
     * @return
     * @Description: TODO(HTTP RESPONSE压缩格式处理)
     */
    byte[] getResponseBody(HttpResponse response);
}
