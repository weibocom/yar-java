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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.ListTemplate;
import org.msgpack.template.MapTemplate;
import org.msgpack.template.Templates;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Converter;
import org.msgpack.unpacker.Unpacker;

/**
 * 
 * @Description MsgpackPackager
 * @author zhanglei
 * @date 2016年6月7日
 *
 */
public class MsgpackPackager implements Packager {
    public <E> byte[] encode(E value) throws IOException {
        MessagePack msgpack = new MessagePack();
        return msgpack.write(value);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "resource"})
    public <E> E decode(byte[] data, Class<E> messageType) throws IOException {
        MessagePack msgpack = new MessagePack();
        msgpack.register(Map.class, new MapTemplate(Templates.TString, new ObjectTemplate()));
        msgpack.register(List.class, new ListTemplate(new ObjectTemplate()));
        Value dynamic = msgpack.read(data);
        Converter converter = new Converter(msgpack, dynamic);
        return converter.read(messageType);
    }

    public static class ObjectTemplate extends AbstractTemplate<Object> {

        public void write(Packer pk, Object v, boolean required) throws IOException {
            if (v == null) {
                if (required) {
                    throw new MessageTypeException("Attempted to write null");
                }
                pk.writeNil();
                return;
            }
            pk.write(v);
        }

        public Object read(Unpacker u, Object to, boolean required) throws IOException {
            if (!required && u.trySkipNil()) {
                return null;
            }

            return toObject(u.readValue());
        }

        @SuppressWarnings({"rawtypes", "unchecked", "resource", "unused"})
        private static Object toObject(Value value) throws IOException {
            Converter conv = new Converter(value);
            if (value.isNilValue()) {
                return null;
            } else if (value.isRawValue()) { // byte[] or String
                return conv.read(Templates.TString);
            } else if (value.isBooleanValue()) {
                return conv.read(Templates.TBoolean);
            } else if (value.isIntegerValue()) {
                IntegerValue intValue = value.asIntegerValue();
                try {
                    int tempInt = intValue.getInt();
                    return conv.read(Templates.TInteger);
                } catch (Exception e) {// when value is long, you got exception
                    return conv.read(Templates.TLong);
                }
            } else if (value.isFloatValue()) {
                return conv.read(Templates.TDouble);
            } else if (value.isArrayValue()) {
                ArrayValue v = value.asArrayValue();
                List<Object> ret = new ArrayList<Object>(v.size());
                for (Value elementValue : v) {
                    ret.add(toObject(elementValue));
                }
                return ret;
            } else if (value.isMapValue()) {
                MapValue v = value.asMapValue();
                Map map = new HashMap(v.size());
                for (Map.Entry<Value, Value> entry : v.entrySet()) {
                    Value key = entry.getKey();
                    Value val = entry.getValue();
                    map.put(toObject(key), toObject(val));
                }
                return map;
            } else {
                throw new RuntimeException("unknown msgpack type");
            }
        }
    }


}
