package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/4.
 */
public class Main {

    public static void main(String args[]){
        Query query = new Query("D:\\data\\Doremus\\fp-trap\\PP-3.owl");

        List<Entity> entities = query.extractEntity();
        System.out.println("entities.size() : "+entities.size());

        List<String> IFPs = query.getIFPs();
        System.out.println("IFPs.size() : "+IFPs.size());
        for(String ifp : IFPs){
//            System.out.println(ifp);
        }

        PositiveExample pe = new PositiveExample(entities,IFPs);
        System.out.println(pe.Run());

        List<String> ss = query.extractProperty();
    }

}
