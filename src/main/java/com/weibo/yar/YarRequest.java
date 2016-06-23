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



import java.util.Arrays;


/**
 * 
 * @Description YarRequest
 * @author zhanglei
 * @date 2016年6月23日
 *
 */
public class YarRequest {
    private long id;
    private String packagerName;
    private String methodName;
    private Object[] parameters;
    private String requestPath;

    public YarRequest(String packagerName, String methodName, Object[] parameters) {
        super();
        this.packagerName = packagerName;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public YarRequest(long id, String packagerName, String methodName, Object[] parameters) {
        this(packagerName, methodName, parameters);
        this.id = id;
    }

    public YarRequest() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPackagerName() {
        return packagerName;
    }

    public void setPackagerName(String packagerName) {
        this.packagerName = packagerName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((packagerName == null) ? 0 : packagerName.hashCode());
        result = prime * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        YarRequest other = (YarRequest) obj;
        if (id != other.id) return false;
        if (methodName == null) {
            if (other.methodName != null) return false;
        } else if (!methodName.equals(other.methodName)) return false;
        if (packagerName == null) {
            if (other.packagerName != null) return false;
        } else if (!packagerName.equals(other.packagerName)) return false;
        if (!Arrays.equals(parameters, other.parameters)) return false;
        return true;
    }

}
