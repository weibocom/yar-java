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
package com.weibo.yar.packager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;
/**
 * 
 * @Description BasePackagerTest
 * @author zhanglei
 * @date 2016年6月7日
 *
 */
public class BasePackagerTest extends TestCase{
    protected Packager packager;
    protected void setUp() throws Exception {
        packager = new JsonPackager();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testString() throws Exception{
        String org = "testString";
        validate(org, String.class);
    }
    
    public void testInt() throws Exception{
        int i = 20;
        validate(i, int.class);
        
        Integer integer = new Integer(20);
        validate(integer, Integer.class);
    }
    
    public void testLong() throws Exception{
        long l = 1234l;
        validate(l, long.class);
        
        Long longNum = new Long(7987);
        validate(longNum, Long.class);
                
    }
    
    public void testFloat() throws Exception{
        float f = 0.15f;
        validate(f, float.class);
        
        Float floatNum = new Float(0.22);
        validate(floatNum, Float.class);
    }
    
    public void testDouble() throws Exception{
        double d = 2.33d;
        validate(d, double.class);
        
        Double doubleNum = new Double(3.55);
        validate(doubleNum, Double.class);
    }
    
    public void testBoolean() throws Exception{
        boolean b = true;
        validate(b, boolean.class);
        
        Boolean bool = Boolean.TRUE;
        validate(bool, Boolean.class);
    }
    
    public void testNull() throws Exception{
        Object o = null;
        byte[] encodeBytes = packager.encode(o);
        assertNotNull(encodeBytes);
        Object temp = packager.decode(encodeBytes, Object.class);
        assertNull(temp);
    }
    
    public void testList() throws Exception{
        List<Object> list = new ArrayList<Object>();
        list.add(123);
        list.add(465l);
        list.add(0.23f);
        list.add(3.45d);
        list.add(true);
        list.add(null);
        validate(list, ArrayList.class);
    }
    
    //只保证支持字符做key
    public void testMap() throws Exception{
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("test", 123);
        map.put("test1", 0.23f);
        
        map.put("test2", "tt");
        map.put("test3", true);
        map.put("test4", 777l);
        map.put("test5", null);
        validate(map, Map.class);
        
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testComplex() throws Exception{
        List<Object> list = new ArrayList<Object>();
        list.add("testlist");
        list.add(234234);
        list.add(false);
        
        Map map = new HashMap();
        map.put("tt", 123);
        
        List<Object> templist = new ArrayList<Object>();
        templist.add(798798);
        templist.add("jsdlkjf");
        templist.add(true);
        map.put("list", templist);
        
        list.add(map);
        validate(list, ArrayList.class);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void validate(Object orgValue, Class valueClazz) throws Exception{
        byte[] encodeBytes = packager.encode(orgValue);
        assertNotNull(encodeBytes);
        Object temp = packager.decode(encodeBytes, valueClazz);
        assertNotNull(temp);
        assertTrue(objectEquals(orgValue, temp));
    }
    
    
    
    public static boolean objectEquals(Object target, Object actual){
        if(target == null && actual == null){
            return true;
        }
        if(target == null || actual == null){
            return false;
        }
        if(target instanceof List){
            return listEquals(target, actual);
        }else if(target instanceof Map){
            return mapEquals(target, actual);
        } else {
            return primitiveEquals(target, actual);
        } 
    }
    

  
    private static boolean primitiveEquals(Object target, Object actual) {
        if(target.equals(actual)){
            return true;
        }else{
            if(target instanceof Long){
                if(((Long) target).longValue() == ((Number)actual).longValue()){
                    return true;
                }
            } else if((target instanceof Integer)){
                if(((Integer) target).intValue() == ((Number)actual).intValue()){
                    return true;
                }
            } else if(target instanceof Double){
                if(((Double) target).doubleValue() == ((Number)actual).doubleValue()){
                    return true;
                }
            }else if(target instanceof Float){
                if(((Float) target).floatValue() == ((Number)actual).floatValue()){
                    return true;
                }
            }else{
                return false;
            }

        }

        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean mapEquals(Object target, Object actual) {
        assertTrue(target instanceof Map);
        assertTrue(actual instanceof Map);
        
        Map map1 = (Map) target;
        Map map2 = (Map) actual;
        if(map1.size() != map2.size()){
            return false;
        }
        
        Iterator<Entry> i = map1.entrySet().iterator();
        while (i.hasNext()) {
            Entry e = i.next();
            Object key = e.getKey();
            Object value = e.getValue();
            if (value == null) {
                if (!(map2.containsKey(key) && map2.get(key)==null))
                    return false;
            } else {
                if (!objectEquals(value, map2.get(key)))
                    return false;
            }
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    private static boolean listEquals(Object target, Object actual) {
        assertTrue(target instanceof List);
        assertTrue(actual instanceof List);
        
        List list1 = (List) target;
        List list2 = (List) actual;
        if(list1.size() != list2.size()){
            return false;
        }
        
        for(int i = 0; i< list1.size(); i ++){
            if(!objectEquals(list1.get(i), list2.get(i))){
                return false;
            }
        }
        return true;
    }


}
