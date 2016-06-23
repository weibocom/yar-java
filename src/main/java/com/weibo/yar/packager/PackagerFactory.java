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

public class PackagerFactory {

    /**
     * Create a new packager by name
     * 
     * @param packagerName
     * @return
     */
    public static Packager createPackager(String packagerName) {
        if (packagerName == null) {
            throw new IllegalArgumentException("Packager name should not be null.");
        }
        if (packagerName.equalsIgnoreCase("PHP")) {
            return new PherializePackager();
        } else if (packagerName.equalsIgnoreCase("JSON")) {
            return new JsonPackager();
        } else if (packagerName.equalsIgnoreCase("MSGPACK")) {
            return new MsgpackPackager();
        } else {
            throw new IllegalArgumentException("Unknown packager type " + packagerName + ".");
        }
    }
}
