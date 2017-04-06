package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import com.zqh.util.Levenshtein;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/4.
 */
public class PositiveExample {

    static private List<Entity> entities;
    static private List<String> IFPs;
    static private Map<Entity,List<Entity>> IFP_entities;
    static private int cnt=0;
    public PositiveExample(List<Entity> entities, List<String> IFPs) {
        this.entities = entities;
        IFP_entities = new HashMap<Entity, List<Entity>>();
        this.IFPs = IFPs;
    }

    public int Run(){
        getIFPEntityPair();
        countTransitiveClosure();
        countTransitiveClosure();
        return cnt;
    }


    private static void countPositiveExamplePairNum(){

        for(Map.Entry entry1 : IFP_entities.entrySet()) {
            List<Entity> entities_equal_1 = (List<Entity>) entry1.getValue();
            for(Entity entity2 : entities_equal_1){
                List<Entity> entities_equal_2 = IFP_entities.get(entity2);
                entities_equal_2.remove(entry1.getKey());
                cnt++;
            }
        }
    }


    /**
     * 计算传递闭包
     */
    private static void  countTransitiveClosure(){
        for(Map.Entry entry1 : IFP_entities.entrySet()){
            List<Entity> entities_equal_1 = (List<Entity>) entry1.getValue();
            List<Entity> entities_equal_1_new = new ArrayList<Entity>();
            entities_equal_1_new.addAll(entities_equal_1);
            for(Entity entity2 : entities_equal_1){
                List<Entity> entities_equal_2 = IFP_entities.get(entity2);
                for(Entity entity3 : entities_equal_2){
                    if(entry1.getKey().equals(entity3) && !entities_equal_1.contains(entity3)){
                        entities_equal_1_new.add(entity3);
                    }
                }
            }
            entry1.setValue(entities_equal_1_new);
        }
    }

    /**
     * 两个实体之间建立IFP关系
     */
    private static void getIFPEntityPair(){
        for(Entity entity1 : entities){
            List<Entity> entities_equal = new ArrayList<Entity>();
            Map<String,List<String>> p1_os =entity1.getPredicate_objects();
            for(Entity entity2 : entities){
                if(!entity1.getSubject().equals(entity2.getSubject())){
                    Map<String,List<String>> p2_os =entity2.getPredicate_objects();
                    if(isProperty_ObjedtsCantainEqualIFP(p1_os,p2_os)){
                        entities_equal.add(entity2);
                    }
                }
            }
            IFP_entities.put(entity1,entities_equal);
        }
    }

    private static boolean isProperty_ObjedtsCantainEqualIFP(Map<String,List<String>> p1_os , Map<String,List<String>> p2_os){
        for(Map.Entry entry1 : p1_os.entrySet()) {
            String s1 = entry1.getKey().toString();
//            System.out.println(s1);
//            for(String s : IFPs){
//                if(s.equals(s1))
//                    System.out.println("zqh");
//            }
            if(!IFPs.contains(entry1.getKey().toString()))
                continue;
            List<String> objects1 = (List<String>) entry1.getValue();
//            System.out.println("objects1.size() : "+objects1.size());
            for (Map.Entry entry2 : p2_os.entrySet()) {
                if (entry1.getKey().toString().equals(entry2.getKey().toString()) && IFPs.contains(entry1.getKey().toString())) {
                    List<String> objects2 = (List<String>) entry2.getValue();
                    System.out.println("objects2.size() : "+objects2.size());
                    for(String o1 : objects1)
                        for(String o2 : objects2)
                            if(o1.equals(o2)) {
                                System.out.println();
                                System.out.println(entry1.getKey().toString());
                                System.out.println(entry2.getKey().toString());
                                System.out.println(o1+"   "+o2);
                                return true;
                            }
                }
            }
        }
        return false;
    }

}
