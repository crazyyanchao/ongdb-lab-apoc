package data.lab.ongdb.http.extra;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.util.Base64Digest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: http.HttpRequest
 * @Description: TODO(使用绝对地址访问接口)
 * @date 2019/7/9 9:21
 */
public class HttpRequest implements HttpInter {
    private Logger logger = Logger.getLogger(HttpRequest.class);

    private static HttpClient httpClient;

    /**
     * 服务器授权验证
     **/
    private static String authBase64;

    @SuppressWarnings("deprecation")
    public HttpRequest() {
        httpClient = new DefaultHttpClient();
        if (httpClient != null) {
            httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HttpHeader.Encoding_UTF_8);
        }
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20 * 60 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20 * 60 * 1000);
        httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
    }

    @SuppressWarnings("deprecation")
    public HttpRequest(String authAccount, String authPassword) {

        authBase64 = "Basic " + Base64Digest.encoder(authAccount + ":" + authPassword);

        httpClient = new DefaultHttpClient();
        if (httpClient != null) {
            httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HttpHeader.Encoding_UTF_8);
        }
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20 * 60 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20 * 60 * 1000);
        httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
    }

    /**
     * @param url :支持绝对接口地址
     * @return
     * @Description: TODO(GET)
     */
    @Override
    public String httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        try {
            httpGet.setHeader("User-Agent", HttpHeader.User_Agent_Firefox);
            httpGet.setHeader("Authorization", authBase64);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int requestStatus = httpResponse.getStatusLine().getStatusCode();

            if (requestStatus == HttpStatus.SC_OK) {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);
                return html;
            } else {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);

                logger.info(requestStatus + "\t" + url);
                logger.error(html);

                return html;
            }
        } catch (IOException e) {
            logger.error("error", e);
        } finally {
            try {
                httpGet.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("error", e);
            }
        }
        return null;
    }

    /**
     * @param url   :支持绝对接口地址
     * @param query :DSL查询
     * @return
     * @Description: TODO(POST)
     */
    @Override
    public synchronized String httpPost(String url, String query) {
        HttpPost httpPost = new HttpPost(url);
        try {

            StringEntity input = new StringEntity(query, HttpHeader.Encoding_UTF_8);
            input.setContentType("application/json");
            httpPost.setEntity(input);
            httpPost.setHeader("Authorization", authBase64);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int requestStatus = httpResponse.getStatusLine().getStatusCode();

            if (requestStatus == HttpStatus.SC_OK) {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);
                return html;
            } else {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);

                logger.info(requestStatus + "\t" + url);
                logger.error(html);

                return html;
            }
        } catch (IOException e) {
            logger.error("error", e);
        } finally {
            try {
                httpPost.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("error", e);
            }
        }
        return null;
    }

    /**
     * @param url   :支持绝对接口地址
     * @param query :DSL查询
     * @return
     * @Description: TODO(PUT)
     */
    @Override
    public String httpPut(String url, String query) {
        HttpPut httpPut = new HttpPut(url);
        try {
            if (query != null) {
                StringEntity input = new StringEntity(query, HttpHeader.Encoding_UTF_8);
                input.setContentType("application/json");
                httpPut.setEntity(input);
                httpPut.setHeader("Authorization", authBase64);
            }

            HttpResponse httpResponse = httpClient.execute(httpPut);
            int requestStatus = httpResponse.getStatusLine().getStatusCode();

            if (requestStatus == HttpStatus.SC_OK) {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);
                return html;
            } else {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);

                logger.info(requestStatus + "\t" + url);
                logger.error(html);

                return html;
            }
        } catch (IOException e) {
            logger.error("error", e);
        } finally {
            try {
                httpPut.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("error", e);
            }
        }
        return null;
    }

    /**
     * @param url   :支持绝对接口地址
     * @param query :DSL查询
     * @return
     * @Description: TODO(DELETE)
     */
    @Override
    public String postDeleteRequest(String url, String query) {
        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
        try {
            if (query != null && !query.equals("")) {
                StringEntity input = new StringEntity(query, HttpHeader.Encoding_UTF_8);
                input.setContentType("application/json");
                httpDeleteWithBody.setEntity(input);
                httpDeleteWithBody.setHeader("Authorization", authBase64);
            }
            HttpResponse httpResponse = httpClient.execute(httpDeleteWithBody);
            int requestStatus = httpResponse.getStatusLine().getStatusCode();
            if (requestStatus == HttpStatus.SC_OK) {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);
                return html;
            } else {
                byte[] temp = getResponseBody(httpResponse);
                String html = new String(temp, HttpHeader.Encoding_UTF_8);
                logger.info(requestStatus + "\t" + url);
                logger.error(html);
                System.out.println(requestStatus + "\t" + url);
                System.out.println(html);

                return html;
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            try {
                httpDeleteWithBody.abort();
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return null;
    }

    /**
     * @param response :HTTP RESPONSE
     * @return
     * @Description: TODO(HTTP RESPONSE压缩格式处理)
     */
    @Override
    public byte[] getResponseBody(HttpResponse response) {
        try {
            Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");
            HttpEntity entity = response.getEntity();
            if (contentEncodingHeader != null) {
                String contentEncoding = contentEncodingHeader.getValue();
                if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {
                    GZIPInputStream gzipInput = null;
                    try {
                        gzipInput = new GZIPInputStream(entity.getContent());
                    } catch (EOFException e) {
                        logger.error("read gzip inputstream eof exception!");
                    }
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[256];
                    int n;
                    while ((n = gzipInput.read(buffer)) >= 0) {
                        out.write(buffer, 0, n);
                    }
                    return out.toByteArray();
                }
            }
            return EntityUtils.toByteArray(entity);
        } catch (Exception e) {
            logger.error("read response body exception! ", e);
        }

        return null;
    }

}

