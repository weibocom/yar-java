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

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 
 * @Description YarResponse
 * @author zhanglei
 * @date 2016年6月7日
 *
 */
public class YarResponse {
    private long id;
    private String status = "0";
    private Object ret;
    private Object output;
    private String error;

    private String packagerName;

    public <T> T getValue(Class<T> contentClass) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        return om.convertValue(ret, contentClass);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getRet() {
        return ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPackagerName() {
        return packagerName;
    }

    public void setPackagerName(String packagerName) {
        this.packagerName = packagerName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((output == null) ? 0 : output.hashCode());
        result = prime * result + ((packagerName == null) ? 0 : packagerName.hashCode());
        result = prime * result + ((ret == null) ? 0 : ret.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        YarResponse other = (YarResponse) obj;
        if (error == null) {
            if (other.error != null) return false;
        } else if (!error.equals(other.error)) return false;
        if (id != other.id) return false;
        if (output == null) {
            if (other.output != null) return false;
        } else if (!output.equals(other.output)) return false;
        if (packagerName == null) {
            if (other.packagerName != null) return false;
        } else if (!packagerName.equals(other.packagerName)) return false;
        if (ret == null) {
            if (other.ret != null) return false;
        } else if (!ret.equals(other.ret)) return false;
        if (status == null) {
            if (other.status != null) return false;
        } else if (!status.equals(other.status)) return false;
        return true;
    }


}
