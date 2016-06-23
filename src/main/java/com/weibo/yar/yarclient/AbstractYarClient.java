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

import java.io.IOException;
import java.util.Map;

import com.weibo.yar.YarException;
import com.weibo.yar.YarProtocol;
import com.weibo.yar.YarRequest;
import com.weibo.yar.YarResponse;

/**
 * 
 * @Description Abstract YarClient.
 * @author zhanglei28
 * @date 2016年5月20日
 *
 */
public abstract class AbstractYarClient implements YarClient {

    protected String packageName = "JSON";// default packageName

    public <E> E call(String path, String method, Class<E> responseClass, Object... parameterObject) throws IOException {
        return call(path, method, packageName, responseClass, parameterObject);
    }

    public <E> E call(String path, String method, String packageName, Class<E> responseClass, Object... parameterObject) throws IOException {
        byte[] requestBytes = buildRequestBtyes(generateId(), path, method, packageName, parameterObject);
        byte[] responseBytes = httpPost(path, null, requestBytes);
        E value = buildResponse(responseBytes, responseClass);
        return value;
    }

    protected byte[] buildRequestBtyes(String path, String method, String packageName, Object... parameterObject) throws IOException {
        return buildRequestBtyes(generateId(), path, method, packageName, parameterObject);
    }

    protected byte[] buildRequestBtyes(long id, String path, String method, String packageName, Object... parameterObject)
            throws IOException {
        YarRequest yarRequest = new YarRequest(id, packageName, method, parameterObject);
        return YarProtocol.toProtocolBytes(yarRequest);
    }

    protected <E> E buildResponse(byte[] responseBytes, Class<E> responseClass) throws IOException {
        YarResponse yarResponse = YarProtocol.buildResponse(responseBytes);
        if (yarResponse == null || yarResponse.getError() != null) {
            throw new YarException(yarResponse == null ? "yar response is null" : yarResponse.getError());
        }
        E value = yarResponse.getValue(responseClass);
        return value;
    }

    protected abstract byte[] httpPost(String url, Map<String, String> headers, byte[] content);

    protected long generateId() {
        // TODO common id. can override by sub class
        return System.currentTimeMillis();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }



}
