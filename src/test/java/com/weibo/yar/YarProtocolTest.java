/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.yar;

import java.io.IOException;

import org.json.simple.JSONObject;

import junit.framework.TestCase;

public class YarProtocolTest extends TestCase {
    
    String methodName = "testMethod";
    Object[] params = new Object[]{"123", 456, 789};
    String packagerName = "JSON";

    public void testProtocolYarRequest() throws IOException {
        YarRequest request = new YarRequest();
        request.setId(45454516);
        request.setMethodName(methodName);
        request.setPackagerName(packagerName);
        request.setParameters(params);
        byte[] requestBytes = YarProtocol.toProtocolBytes(request);
        
        YarRequest newRequest = YarProtocol.buildRequest(requestBytes);
        assertNotNull(newRequest);
        assertEquals(request, newRequest);
    }

    @SuppressWarnings("unchecked")
    public void testProtocolYarResponse() throws IOException {
        YarResponse response = new YarResponse();
        response.setError("eee");
        response.setId(623746);
        response.setOutput("tt");
        response.setPackagerName(packagerName);
        JSONObject jo = new JSONObject();
        jo.put("k1", "v1");
        jo.put("k2", "v2");
        response.setRet(jo);
        response.setStatus("0");
        
        byte[] responseBytes = YarProtocol.toProtocolBytes(response);
        
        YarResponse newResponse = YarProtocol.buildResponse(responseBytes);
        assertNotNull(newResponse);
        assertEquals(response, newResponse);
    }

}
