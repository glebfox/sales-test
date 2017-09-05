package com.company.sales.web;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

@Ignore
public class RestApiTest {
    private static final String URI_BASE = "http://localhost:8080/app/rest/v2";

    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";

    private String oauthToken;

    @Before
    public void setUp() throws Exception {
        oauthToken = getAuthToken("admin", "admin");
    }

    @Test
    public void createCustomer() throws Exception {
        String entityName = "sales$Customer";
        String url = "/entities/" + entityName;
        String json = "{\"name\":\"Alex\",\"email\":\"alex@home.com\"}";
        UUID customerId;

        // Save the customer to the database
        try (CloseableHttpResponse response = sendPost(url, oauthToken, json)) {
            Header[] locationHeaders = response.getHeaders("Location");
            assertEquals(1, locationHeaders.length);
            String location = locationHeaders[0].getValue();
            assertTrue(location.startsWith(URI_BASE + url));
            String idString = location.substring(location.lastIndexOf("/") + 1);
            customerId = UUID.fromString(idString);

            ReadContext ctx = parseResponse(response);
            assertEquals(entityName, ctx.read("$._entityName"));
            assertEquals(customerId.toString(), ctx.read("$.id"));
            assertNotNull(ctx.read("$.createTs"));
            assertNotNull(ctx.read("$.version"));
        }

        // Remove the customer
        url += "/" + customerId.toString();
        try (CloseableHttpResponse response = sendDelete(url, oauthToken)) {
            assertEquals(HttpStatus.SC_OK, statusCode(response));
        }
    }

    private String getAuthToken(String login, String password) throws IOException {
        String uri = URI_BASE + "/oauth/token";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String encoding = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Authorization", "Basic " + encoding);
        httpPost.setHeader("Accept-Language", "en");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "password"));
        urlParameters.add(new BasicNameValuePair("username", login));
        urlParameters.add(new BasicNameValuePair("password", password));

        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = statusCode(response);
            if (statusCode != SC_OK) {
                throw new RuntimeException("Status Code: " + statusCode);
            }
            ReadContext ctx = parseResponse(response);
            return ctx.read("$.access_token");
        }
    }

    private CloseableHttpResponse sendGet(String url, String token) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(URI_BASE + url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeader("Authorization", "Bearer " + token);
        httpGet.setHeader("Accept-Language", "en");
        return httpClient.execute(httpGet);
    }

    private CloseableHttpResponse sendPost(String url, String token, String body) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(URI_BASE + url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        StringEntity stringEntity = new StringEntity(body);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Authorization", "Bearer " + token);
        return httpClient.execute(httpPost);
    }

    private CloseableHttpResponse sendDelete(String url, String token) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(URI_BASE + url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
        httpDelete.setHeader("Authorization", "Bearer " + token);
        return httpClient.execute(httpDelete);
    }

    private ReadContext parseResponse(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity);
        return JsonPath.parse(s);
    }

    private int statusCode(CloseableHttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }
}