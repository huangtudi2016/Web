package com.huangzan.web.utils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by huangzan on 16/7/14.
 */
public class CollectionUtil {

    public static List<String > duplicate(List<String> list){
//        System.out.print("111111111"+list.toString());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            if (null==iterator.next()/*||"".equals(iterator.next().toString().trim())*/){
               iterator.remove();
           }
        }
//        System.out.print("222222222"+list.toString());
        return list;
    }
}
