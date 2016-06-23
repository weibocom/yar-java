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
package com.weibo.yar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weibo.yar.packager.Packager;
import com.weibo.yar.packager.PackagerFactory;

/**
 * 
 * @Description YarProtocol
 * @author zhanglei
 * @date 2016年6月23日
 *
 */
public class YarProtocol {

    public static byte[] toProtocolBytes(YarRequest request) throws IOException {
        if (request == null) {
            throw new YarException("YarRequest is null");
        }
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("i", request.getId());
        requestMap.put("m", request.getMethodName());
        requestMap.put("p", Arrays.asList(request.getParameters()));
        String packagerName = request.getPackagerName();
        return toProtocolBytes(request.getId(), packagerName, requestMap);
    }

    public static byte[] toProtocolBytes(YarResponse response) throws IOException {
        if (response == null) {
            throw new YarException("YarResponse is null");
        }
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("i", response.getId());
        responseMap.put("s", response.getStatus());
        if (response.getRet() != null) {
            responseMap.put("r", response.getRet());
        }
        if (response.getOutput() != null) {
            responseMap.put("o", response.getOutput());
        }
        if (response.getError() != null) {
            responseMap.put("e", response.getError());
        }

        String packagerName = response.getPackagerName();
        return toProtocolBytes(response.getId(), packagerName, responseMap);
    }

    @SuppressWarnings("rawtypes")
    public static YarRequest buildRequest(byte[] requestBytes) throws IOException {
        if (requestBytes == null) {
            throw new YarException("request bytes is null");
        }
        Map contentMap = fetchContent(requestBytes);
        YarRequest request = new YarRequest();
        if (contentMap.containsKey("i")) {
            request.setId(((Number) contentMap.get("i")).longValue());
        }

        request.setPackagerName((String) contentMap.get("ext-packagerName"));
        if (contentMap.containsKey("m")) {
            request.setMethodName((contentMap.get("m").toString()));
        }

        if (contentMap.containsKey("p")) {
            Object value = contentMap.get("p");
            if ((value instanceof List)) {
                request.setParameters(((List) value).toArray());
            } else if (value instanceof Map) {
                request.setParameters(((Map) value).values().toArray());
            }
        }
        return request;
    }

    @SuppressWarnings("rawtypes")
    public static YarResponse buildResponse(byte[] responseBytes) throws IOException {
        if (responseBytes == null) {
            throw new YarException("response bytes is null");
        }

        Map contentMap = fetchContent(responseBytes);

        YarResponse response = new YarResponse();
        response.setPackagerName((String) contentMap.get("ext-packagerName"));
        if (contentMap.containsKey("i")) {
            response.setId(((Number) contentMap.get("i")).longValue());
        }

        if (contentMap.containsKey("s")) {
            response.setStatus(contentMap.get("s").toString());
        }

        if (contentMap.containsKey("o")) {
            response.setOutput(contentMap.get("o"));
        }

        if (contentMap.containsKey("e")) {
            response.setError(contentMap.get("e").toString());
        }

        if (contentMap.containsKey("r")) {
            response.setRet(contentMap.get("r"));
        }
        return response;
    }

    private static byte[] toProtocolBytes(long id, String packagerName, Map<String, Object> params) throws IOException {
        Packager packager = PackagerFactory.createPackager(packagerName);

        byte[] bodyBytes;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(Arrays.copyOf(packagerName.toUpperCase().getBytes(), 8));
            out.write(packager.encode(params));
            bodyBytes = out.toByteArray();
        } finally {
            out.close();
        }

        YarHeader header = new YarHeader();
        header.setBodyLenght(bodyBytes.length);
        header.setRequestId(id);
        ByteArrayOutputStream totalByteArrayOutputStream = new ByteArrayOutputStream(YarHeader.HEADER_SIZE + bodyBytes.length);
        try {
            totalByteArrayOutputStream.write(header.toBytes());
            totalByteArrayOutputStream.write(bodyBytes);
            return totalByteArrayOutputStream.toByteArray();
        } finally {
            totalByteArrayOutputStream.close();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map fetchContent(byte[] bytes) throws IOException {
        ByteArrayInputStream responseInputStream = new ByteArrayInputStream(bytes);
        // Read protocol header.
        byte[] headerBytes = new byte[YarHeader.HEADER_SIZE];
        responseInputStream.read(headerBytes);
        YarHeader header = YarHeader.fromBytes(headerBytes);

        // Read 8 bytes as packager name.
        byte[] packagerNameBytes = new byte[8];
        responseInputStream.read(packagerNameBytes);

        // packager name end with '\0'.
        int length;
        for (length = 0; length < packagerNameBytes.length && packagerNameBytes[length] != 0; length++);
        String packagerName = new String(packagerNameBytes, 0, length);

        // remain bytes is response content.
        byte[] bodyBytes = new byte[(int) (header.getBodyLenght() - 8)];
        responseInputStream.read(bodyBytes);

        Packager packager = PackagerFactory.createPackager(packagerName);
        Map content = packager.decode(bodyBytes, Map.class);

        // ext信息
        content.put("ext-packagerName", packagerName);
        content.put("ext-protocol-header", header);
        return content;
    }

}
