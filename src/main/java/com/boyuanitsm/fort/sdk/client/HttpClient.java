package com.boyuanitsm.fort.sdk.client;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Apache http client.
 *
 * @author zhanghua on 5/16/16.
 */
public class HttpClient {

    private final Logger log = LoggerFactory.getLogger(HttpClient.class);

    HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        context.setCookieStore(cookieStore);
    }

    private String baseUrl;

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private HttpClientContext context = HttpClientContext.create();
    private CookieStore cookieStore = new BasicCookieStore();

    CookieStore loginFortSecurityServer(String url, BasicNameValuePair... pairs) throws IOException, HttpException {
        postForm(url, pairs);
        return cookieStore;
    }

    /**
     * send post request Content-Type: application/x-www-form-urlencoded
     *
     * @param url post url
     * @param pairs form params
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    String postForm(String url, BasicNameValuePair... pairs) throws IOException, HttpException {
        StringBuffer fullPostUrl = new StringBuffer().append(baseUrl).append(url);
        HttpPost post = new HttpPost(fullPostUrl.toString());
        // set form entity
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.addAll(Arrays.asList(pairs));
        formParams.add(new BasicNameValuePair("_csrf", getCookieValue("CSRF-TOKEN")));
        post.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
        return send(post);
    }

    /**
     * send post request Content-Type: application/json
     *
     * @param url post url
     * @param json json
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    String postJson(String url, JSON json) throws IOException, HttpException {
        StringBuffer fullPostUrl = new StringBuffer().append(baseUrl).append(url);
        HttpPost post = new HttpPost(fullPostUrl.toString());
        // set json string entity
        post.setEntity(new StringEntity(json.toJSONString()));
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json;charset=UTF-8");
        post.setHeader("X-CSRF-TOKEN", getCookieValue("CSRF-TOKEN"));

        return send(post);
    }

    /**
     * send post request Content-Type: application/json
     *
     * @param url post url
     * @param obj obj
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    String postJson(String url, Object obj) throws IOException, HttpException {
        JSON json = (JSON) JSON.toJSON(obj);
        return postJson(url, json);
    }

    /**
     * send put request Content-Type: application/json
     *
     * @param url put url
     * @param json json
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    String putJson(String url, JSON json) throws IOException, HttpException {
        StringBuffer fullPostUrl = new StringBuffer().append(baseUrl).append(url);
        HttpPut put = new HttpPut(fullPostUrl.toString());
        // set json string entity
        put.setEntity(new StringEntity(json.toJSONString()));
        put.setHeader("Accept", "application/json");
        put.setHeader("Content-type", "application/json;charset=UTF-8");
        return send(put);
    }

    /**
     * send put request Content-Type: application/json
     *
     * @param url put url
     * @param obj obj
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    String putJson(String url, Object obj) throws IOException, HttpException {
        JSON json = (JSON) JSON.toJSON(obj);
        return putJson(url, json);
    }

    /**
     * send get request
     *
     * @param url request url
     * @param pairs url params
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    String get(String url, BasicNameValuePair... pairs) throws IOException, HttpException {
        String urlParams = URLEncodedUtils.format(Arrays.asList(pairs), "UTF-8");
        if (!url.endsWith("?")) {
            url += "?";
        }
        StringBuffer fullGetUrl = new StringBuffer().append(baseUrl).append(url).append(urlParams);

        HttpGet get = new HttpGet(fullGetUrl.toString());
        return send(get);
    }

    String delete(String url) throws IOException, HttpException {
        StringBuffer fullDeleteUrl = new StringBuffer().append(baseUrl).append(url);
        HttpDelete delete = new HttpDelete(fullDeleteUrl.toString());
        return send(delete);
    }

    /**
     * send http request
     *
     * @param request http request base
     * @return response content
     * @throws IOException
     * @throws HttpException
     */
    private String send(HttpRequestBase request) throws IOException, HttpException {
        // Make sure cookie headers are written
        RequestAddCookies addCookies = new RequestAddCookies();
        addCookies.process(request, context);
        // send request
        CloseableHttpResponse response = httpClient.execute(request, context);
        // validate http status code
        isSuccess(response.getStatusLine().getStatusCode(), null);
        // get response content
        String content = EntityUtils.toString(response.getEntity());
        // close response
        response.close();
        return content;
    }

    /**
     * get cookie value from cookie store
     *
     * @param cookieName the cookie name
     * @return if not found return null else cookie value
     */
    private String getCookieValue(String cookieName) {
        String value = null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                value = cookie.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * is request success
     *
     * @param statusCode http status code
     * @param errMsg error message
     * @throws RuntimeException if status code != 200 || != 201, throw exception
     */
    private void isSuccess(int statusCode, String errMsg) throws RuntimeException {
        if (errMsg == null)
            errMsg = "";

        if (statusCode != 200 && statusCode != 201) {
            throw new RuntimeException(String.format("http code is not ok! is %s. %s", statusCode, errMsg));
        }
    }
}
