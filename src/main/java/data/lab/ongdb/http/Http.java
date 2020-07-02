package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.http.extra.HttpRequest;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http
 * @Description: TODO(执行HTTP请求的过程)
 * @date 2020/7/2 16:05
 */
public class Http {

    private static final HttpRequest REQUEST = new HttpRequest();

    /**
     * @param
     * @return
     * @Description: TODO(执行post请求)
     */
    @UserFunction(name = "olab.http.post")
    @Description("RETURN olab.http.post('api-address','input')")
    public String post(@Name("api") String api, @Name("para") String para) {
        return REQUEST.httpPost(api, para);
    }

    /**
     * @param
     * @return
     * @Description: TODO(执行get请求)
     */
    @UserFunction(name = "olab.http.get")
    @Description("RETURN olab.http.get('api-address')")
    public String get(@Name("api") String api) {
        return REQUEST.httpGet(api);
    }

    /**
     * @param
     * @return
     * @Description: TODO(执行put请求)
     */
    @UserFunction(name = "olab.http.put")
    @Description("RETURN olab.http.put('api-address','input')")
    public String put(@Name("api") String api, @Name("para") String para) {
        return REQUEST.httpPut(api, para);
    }

    /**
     * @param
     * @return
     * @Description: TODO(执行delete请求)
     */
    @UserFunction(name = "olab.http.delete")
    @Description("RETURN olab.http.delete('api-address','input')")
    public String delete(@Name("api") String api, @Name("para") String para) {
        return REQUEST.postDeleteRequest(api, para);
    }
}

