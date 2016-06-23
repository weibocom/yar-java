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
import com.weibo.yar.codec.PHPUnserializer;
import de.ailis.pherialize.Mixed;
import de.ailis.pherialize.Pherialize;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @Description php serialize packager
 * @author chengya
 * @date 2016年6月7日
 *
 */
public class PherializePackager implements Packager {
    public <E> byte[] encode(E value) throws IOException {
        return Pherialize.serialize(value).getBytes();
    }

    public <E> E decode(byte[] data, Class<E> messageType) throws IOException {
        PHPUnserializer unserializer = new PHPUnserializer(new String(data, 0, data.length, "ISO-8859-1"));
        Mixed mixed = unserializer.unserializeObject();
        Object o = convert(mixed);
        return new ObjectMapper().convertValue(o, messageType);
    }

    private Object convert(Mixed mixed) throws UnsupportedEncodingException {
        if(mixed == null){
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        if (mixed.getType() != Mixed.TYPE_ARRAY) {
            return convertNonArray(mixed);
        } else {
            boolean flag = true;
            int counter = 0;
            for (Map.Entry<Object, Object> entry : mixed.toArray().entrySet()) {
                Mixed mixedKey = (Mixed) entry.getKey();
                map.put(mixedKey.toString(), convert((Mixed) entry.getValue()));
                if (flag) {
                    if ((!mixedKey.isInt() && !mixedKey.isLong()) || counter++ != mixedKey.toLong()) {
                        flag = false;
                    }
                }
            }
            // convert to ArrayList if possible
            if (flag && map.size() > 0) {
                return new ArrayList<Object>(map.values());
            }
        }

        return map;
    }

    private Object convertNonArray(Mixed mixed) throws UnsupportedEncodingException {
        switch (mixed.getType()) {
            case Mixed.TYPE_BOOLEAN:
                return mixed.toBoolean();
            case Mixed.TYPE_CHAR:
            case Mixed.TYPE_STRING:
                return new String(mixed.toString().getBytes("ISO-8859-1"), "UTF-8");
            case Mixed.TYPE_ARRAY:
            case Mixed.TYPE_UNKNOWN:
                // null type is TYPE_UNKNOWN
                if(mixed.getValue() == null){
                    return null;
                }
                throw new UnsupportedOperationException("unknown type: " + mixed.getType());
            case Mixed.TYPE_FLOAT:
            case Mixed.TYPE_DOUBLE:
                return mixed.toDouble();
            default:
                return mixed.toLong();
        }
    }
}
