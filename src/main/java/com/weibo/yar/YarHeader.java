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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @Description YarHeader
 * @author zhanglei
 * @date 2016年5月20日
 *
 */
public class YarHeader {

    public static final long MAGIC_NUM = 0x80DFEC60;

    public static final int HEADER_SIZE = 82;

    private long requestId = 0;

    private int version = 0;

    private long reserved = 0;

    private String provider = null;

    private String token = null;

    private long bodyLenght = 0;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getReserved() {
        return reserved;
    }

    public void setReserved(long reserved) {
        this.reserved = reserved;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getBodyLenght() {
        return bodyLenght;
    }

    public void setBodyLenght(long bodyLenght) {
        this.bodyLenght = bodyLenght;
    }


    public static YarHeader fromBytes(byte[] bytes) throws IOException {
        YarHeader header = new YarHeader();
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            header.setRequestId(in.readInt() & 0xffffffffL);
            header.setVersion(in.readUnsignedShort());
            if (in.readInt() != MAGIC_NUM) {
                throw new IOException("Invalid Yar header.");
            }
            header.setReserved(in.readInt() & 0xffffffffL);
            byte[] tempbyte = new byte[32];
            in.read(tempbyte, 0, 32);
            header.setProvider(new String(tempbyte));
            tempbyte = new byte[32];
            in.read(tempbyte, 0, 32);
            header.setToken(new String(tempbyte));
            header.setBodyLenght(in.readInt() & 0xffffffffL);
        } finally {
            in.close();
        }
        return header;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(HEADER_SIZE);
        DataOutputStream out = new DataOutputStream(byteStream);
        try {
            out.writeInt((int) getRequestId());
            out.writeShort((short) getVersion());
            out.writeInt((int) MAGIC_NUM);
            out.writeInt((int) reserved);
            out.write(new byte[64]);
            out.writeInt((int) getBodyLenght());
            return byteStream.toByteArray();
        } finally {
            byteStream.close();
            out.close();
        }
    }


}
