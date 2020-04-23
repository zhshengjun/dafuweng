package com.stupidzhang.dafuweng.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stupidzhang.dafuweng.base.Valuation;

import java.util.Iterator;
import java.util.List;

public class HttpClientProxy<T> {

   List<T> getResult(String url, T object, List<String> params){

       // 获取数据
       JSONObject data = JSON.parseObject(HttpUtil.doGet(url));
       Iterator<String> iterator = params.iterator();
       if(iterator.hasNext()){
           String next = iterator.next();
           data = JSON.parseObject(data.getString(next));
       }

       String items = data.getString("items");
       return (List<T>) JSON.parseArray(items, object.getClass());
   }
}
