/*
 * Copyright 2009-2016 Weibo, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.yar.yarclient;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;



public class HttpYarClient extends AbstractYarClient {
    private static Log log = LogFactory.getLog(HttpYarClient.class);
    private HttpClient httpClient = HttpClients.createDefault();
    private int soTimeout;
    private int connectTimeout;

    public HttpYarClient() {
        this(3000, 5000);

    }

    public HttpYarClient(int soTimeout, int connectTimeout) {
        httpClient = HttpClients.createDefault();
        this.soTimeout = soTimeout;
        this.connectTimeout = connectTimeout;
    }


    @Override
    protected byte[] httpPost(String url, Map<String, String> headers, byte[] content) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(soTimeout).setSocketTimeout(soTimeout).setConnectTimeout(connectTimeout).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new ByteArrayEntity(content, ContentType.APPLICATION_FORM_URLENCODED));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toByteArray(entity) : null;
            } else {
                log.error("Unexpected response status: " + status);
                return null;
            }
        } catch (Exception e) {
            log.error("httpclient execute fail.", e);
            return null;
        } finally{
            httpPost.releaseConnection();
        }
    }


}
