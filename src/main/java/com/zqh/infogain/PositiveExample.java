package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import com.zqh.util.Levenshtein;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

import java.util.*;

/**
 * Created by Administrator on 2017/4/4.
 */
public class PositiveExample {

    private List<Entity> entities;
    private Set<String> FPs_IFPs;
    private Map<String,List<String>> sameAs;
    private Map<Entity,List<Entity>> FP_IFP_entities;
    private Map<Entity,List<Entity>> sameAs_entities;
    private Map<Entity,List<Entity>> relations;
    Map<String,Entity> table;
    private int cnt=0;

    public PositiveExample(List<Entity> entities , List<String> FPs ,  List<String> IFPs , Map<String,List<String>> sameAs) {
        this.entities = entities;
        this.sameAs = sameAs;
        FP_IFP_entities = new HashMap<Entity, List<Entity>>();
        sameAs_entities = new HashMap<Entity, List<Entity>>();
        relations = new HashMap<Entity, List<Entity>>();
        FPs_IFPs = new HashSet<String>();
        table = new HashMap<String, Entity>();
        FPs_IFPs.addAll(FPs);
        FPs_IFPs.addAll(IFPs);
        System.out.println("FPs_IFPs.size() = "+FPs_IFPs.size());
//        for(String s : FPs)
//            System.out.println(s);
        getTableForSameAs();
    }

    public int getPositiveExamplePairNum(List<Entity> entities) {
        Helper helper = new Helper();

        this.entities = entities;
//        getFP_IFPEntityPair();
//        helper.writeToFile("nyt-dbpedia-people-FP_IFP-relations",FP_IFP_entities);
        System.out.println("FP_IFP_entities.size() = "+FP_IFP_entities.size());
        System.out.println("    getFP_IFPEntityPair() ... done");
        getSameAsRelations();
//        helper.writeToFile("nyt-dbpedia-people-sameAs-relations",sameAs_entities);
        System.out.println("sameAs_entities.size() = "+sameAs_entities.size());
        System.out.println("    getSameAsRelations() ... done");

//        relations.putAll(FP_IFP_entities);
        relations.putAll(sameAs_entities);
        countTransitiveClosure();
        System.out.println("relations.size() = "+relations.size());
        System.out.println("    countTransitiveClosure() ... done");
//        helper.writeToFile("nyt-dbpedia-people-relations",relations);

        countPositiveExamplePairNum();
        System.out.println("    countPositiveExamplePairNum() ... done");
        return cnt;
    }


    private void countPositiveExamplePairNum(){

        Set<String> temp = new HashSet<String>();
        for(Map.Entry entry1 : relations.entrySet()) {
            String localName1 = ((Entity)entry1.getKey()).getSubject();
            List<Entity> entities_equal_1 = (List<Entity>) entry1.getValue();
            for(Entity entity2 : entities_equal_1){
                if(entity2 != null){
                    temp.add(localName1+"#"+entity2.getSubject());
                    temp.add(entity2.getSubject()+"#"+localName1);
                }

            }
        }
        cnt = temp.size()/2;
    }

    /**
     * 计算传递闭包
     */
    private void  countTransitiveClosure(){
        for(Map.Entry entry1 : relations.entrySet()){
            List<Entity> entities_equal_1 = (List<Entity>) entry1.getValue();
            List<Entity> entities_equal_1_new = new ArrayList<Entity>();
            entities_equal_1_new.addAll(entities_equal_1);
            if(!entities_equal_1.isEmpty())
                for(Entity entity2 : entities_equal_1){
                    List<Entity> entities_equal_2 = new ArrayList<Entity>();
                    entities_equal_2 = relations.get(entity2);
                    if(entities_equal_2!=null && !entities_equal_2.isEmpty())
                        for(Entity entity3 : entities_equal_2){
                        if(entry1.getKey().equals(entity3) && !entities_equal_1.contains(entity3))
                            entities_equal_1_new.add(entity3);
                        }
                }
            entry1.setValue(entities_equal_1_new);
        }
    }

    /**
     * 两个实体之间建立 FP 或 IFP 关系
     */
    private void getFP_IFPEntityPair(){
        for(Entity entity1 : entities){
            List<Entity> entities_equal = new ArrayList<Entity>();
            Map<String,List<String>> p1_os =entity1.getPredicate_objects();
            for(Entity entity2 : entities){
                if(!entity1.getSubject().equals(entity2.getSubject())){
                    Map<String,List<String>> p2_os =entity2.getPredicate_objects();
                    if(isProperty_ObjedtsCantainEqualFP_IFP(p1_os,p2_os)){
                        if(entity1.getSubject().equals("http://data.nytimes.com/81216719683522436183.rdf")
                                && entity2.getSubject().equals("http://data.nytimes.com/4768797027183064773.rdf")){
                            System.out.println(entity1.getSubject() + "    "+ entity2.getSubject());
                            System.out.println(isProperty_ObjedtsCantainEqualFP_IFP1(p1_os,p2_os));
                        }
                        entities_equal.add(entity2);
                    }
                }
            }
            if(!entities_equal.isEmpty())
                FP_IFP_entities.put(entity1,entities_equal);
        }
    }

    private boolean isProperty_ObjedtsCantainEqualFP_IFP(Map<String,List<String>> p1_os , Map<String,List<String>> p2_os){
        for(Map.Entry entry1 : p1_os.entrySet()) {
            if(!FPs_IFPs.contains(entry1.getKey().toString()))
                continue;
            List<String> objects1 = (List<String>) entry1.getValue();
            for (Map.Entry entry2 : p2_os.entrySet()) {
                if (entry1.getKey().toString().equals(entry2.getKey().toString()) && FPs_IFPs.contains(entry1.getKey().toString())) {
                    List<String> objects2 = (List<String>) entry2.getValue();
                    for(String o1 : objects1) {
                        if(o1==null || o1.equals(""))
                            continue;
                        for (String o2 : objects2) {
                            if(o2==null || o2.equals(""))
                                continue;
                            if (o1.equals(o2)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private String isProperty_ObjedtsCantainEqualFP_IFP1(Map<String,List<String>> p1_os , Map<String,List<String>> p2_os){
        for(Map.Entry entry1 : p1_os.entrySet()) {
            if(!FPs_IFPs.contains(entry1.getKey().toString()))
                continue;
            List<String> objects1 = (List<String>) entry1.getValue();
            for (Map.Entry entry2 : p2_os.entrySet()) {
                if (entry1.getKey().toString().equals(entry2.getKey().toString()) && FPs_IFPs.contains(entry1.getKey().toString())) {
                    List<String> objects2 = (List<String>) entry2.getValue();
                    for(String o1 : objects1)
                        for(String o2 : objects2)
                            if(o1.equals(o2)) {
                                return o1+"*"+o2+"&"+entry1.getKey().toString();
                            }
                }
            }
        }
        return "";
    }

    /**
     * 两个实体之间建立 sameAs 关系
     */
    private void getSameAsRelations(){
        for(Entity entity : entities){
            List<Entity> equal_entities = new ArrayList<Entity>();
            Map<String,List<String>> predicate_objects = entity.getPredicate_objects();
            for(Map.Entry entry : predicate_objects.entrySet()){
                if(entry.getKey().toString().equals("http://www.w3.org/2002/07/owl#sameAs")){
                    List<String> equal_strings = (List<String>) entry.getValue();
                    for(String equal_string : equal_strings)
                        equal_entities.add(table.get(equal_string));
                    break;
                }
            }
            if(!equal_entities.isEmpty())
                sameAs_entities.put(entity,equal_entities);
        }
    }

    /**
     * 建立subject和entity一一对应的关系，方便后续查找
     */
    private void getTableForSameAs(){
        for(Map.Entry entry : sameAs.entrySet()){
            String subject = entry.getKey().toString();
            for(Entity entity : entities){
                if(entity.getSubject().equals(subject)){
                    table.put(subject,entity);
                    break;
                }
            }
        }
    }
}
