package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/7/29.
 */
public class CollectionUtils {

    public static List castCollection2List(Collection c){
        List list = new ArrayList();
//        while (c.iterator().hasNext()){
//            list.add(c.iterator().next());
//        }
        c.toArray();
        return list;
    }

    public static void main(String[] args) {
    }
}
