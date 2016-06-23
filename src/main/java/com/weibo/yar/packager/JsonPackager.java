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
package com.weibo.yar.packager;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonPackager implements Packager {

    private ObjectMapper objectMapper = new ObjectMapper();


    public JsonPackager() {
        objectMapper = new ObjectMapper();
    }

    public <E> byte[] encode(E value) throws IOException {
        return objectMapper.writeValueAsBytes(value);
    }

    public <E> E decode(byte[] data, Class<E> messageType) throws IOException {
        return objectMapper.readValue(data, messageType);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
