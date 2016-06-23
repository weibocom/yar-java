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
package com.weibo.yar.yarserver;

import org.json.simple.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import com.weibo.yar.YarProtocol;
import com.weibo.yar.YarRequest;
import com.weibo.yar.YarResponse;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        ByteBuf buf = msg.content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);

        YarRequest yarRequest = YarProtocol.buildRequest(bytes);
        YarResponse yarResponse = process(yarRequest);

        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(YarProtocol
                        .toProtocolBytes(yarResponse)));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (HttpHeaders.isKeepAlive(msg)) {
            response.headers().set(HttpHeaders.Names.CONNECTION, Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ctx.flush();
        ctx.close();
    }


    private YarResponse process(YarRequest request) {
        YarResponse response = new YarResponse();
        response.setId(request.getId());
        response.setPackagerName(request.getPackagerName());
        JSONObject jo = new JSONObject();
        jo.put("key", "test");
        response.setRet(jo);

        return response;

    }

}
