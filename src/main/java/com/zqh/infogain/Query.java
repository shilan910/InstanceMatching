package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/4.
 */
public class Query {

    private String filename;
    private String URI_prefix;
    private static Model model;
    private List<String> subjectList;
    private List<Entity> entities;

    /**
     *构造方法
     * @param filename ： 文件路径
     */
    public Query(String filename) {

        subjectList = new ArrayList<String>();
        entities = new ArrayList<Entity>();

        this.filename = filename;
        model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(filename);
        if(filename.endsWith(".nt")){
            model.read(in,"","N3");
        }else{
            model.read(in,null);
        }
    }

    /**
     * 提取属性
     * @return 属性列表
     */
    public List<String> extractProperty(){
        List<String> properties = new ArrayList<String>();
        String queryString = "SELECT ?predicate WHERE { ?subject ?predicate ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String predicate = querySolution.get("predicate").toString();
            //去重
            if(!properties.contains(predicate) && predicate.contains("http")){
                properties.add(predicate);
                System.out.println(predicate);
            }
        }
        queryExecution.close();

        List<String> properties_new = new ArrayList<String>();
        for(String predicate : properties){
            predicate = extractString(predicate);
            properties_new.add(predicate);
        }
        return properties_new;
    }


    /**
     * 提取实体
     * @return 实体列表
     */
    public List<Entity> extractEntity(){
        //提取subject
        for(StmtIterator i = model.listStatements(); i.hasNext() ; ){
            Statement j = i.nextStatement();
            String subject = j.getSubject().toString();
            if(!subjectList.contains(subject)){
                subjectList.add(subject);
            }
        }
        for(String subject : subjectList) {
            List<String> predicate_object = new ArrayList<String>();
            Map<String,List<String>> predicate_objects = new HashMap<String, List<String>>();
            queryPredicateAndObject(predicate_objects, subject);
            if (predicate_objects.size() != 0) {
                entities.add(new Entity(extractString(subject), predicate_objects));
            }
        }
        return entities;
    }


    private void queryPredicateAndObject(Map<String,List<String>> predicate_objects , String subject){

        Map<String,List<String>> predicate_objects_temp = new HashMap<String, List<String>>();
        String queryString = "SELECT ?predicate?object WHERE { <"+subject+"> ?predicate ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String predicate = querySolution.get("predicate").toString();
            String object = querySolution.get("object").toString();

            if(subjectList.contains(object)){   //object是另外一个实体，获取其localname
                object = getLocalName(object);
            }
            if(object!=""){
                if(predicate_objects_temp.containsKey(predicate)){
                    List<String> objects = predicate_objects_temp.get(predicate);
                    objects.add(object);
                }else{
                    List<String> objects = new ArrayList<String>();
                    objects.add(object);
                    predicate_objects_temp.put(predicate,objects);
                }
            }
        }

        for(Map.Entry entry : predicate_objects_temp.entrySet()){
            List<String> objects = (List<String>) entry.getValue();
            for(String o : objects){
                o = extractString(o);
            }
            predicate_objects.put(extractString(entry.getKey().toString()),objects);
        }

        queryExecution.close();
    }

    private static String getLocalName(String subject){
        String localname = "";
        String queryString = "SELECT ?p WHERE { <"+subject+"> ?p ?o}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        Resource resource = queryExecution.execSelect().getResourceModel().getResource(subject);
        localname = resource.getLocalName();
        queryExecution.close();
        return localname;
    }

    private static String extractString(String s){
        int index;
        if(s.contains("^")){    //数字
            index = s.indexOf('^');
            s = s.substring(0,index);
        }else{
            if(s.contains("#")){
                index = s.indexOf('#');
            }else{
                index = s.lastIndexOf('/');
            }
            s = s.substring(index+1).replaceAll("[_\\s+]","-").replaceAll("[(*)]","");
        }
        return s;
    }


    /**
     * 找出反函数属性
     * @return
     */
    public List<String> getIFPs(){
        List<String> IFPs = new ArrayList<String>();
        String queryString ="SELECT ?subject WHERE { ?subject <http://www.w3.org/2002/07/owl#inverseOf> ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String subject = querySolution.get("subject").toString();
            if(!IFPs.contains(subject)){
                IFPs.add(subject);
            }
        }
        queryExecution.close();

        List<String> IFPs_new = new ArrayList<String>();
        for(String IFP : IFPs){
            IFPs_new.add(extractString(IFP));
        }

        return IFPs_new;
    }


    public static void main(String args[]){
        Query query = new Query("D:\\data\\Doremus\\fp-trap\\PP-3.owl");

        List<Entity> entities = query.extractEntity();
        System.out.println("\n");
        for(Entity entity : entities){
            System.out.println(entity.getSubject()+" : "+entity.getPredicate_objects().size());
        }
        System.out.println(entities.size());
//
//        List<String> ps = query.extractProperty();
//        for(String s : ps){
//            System.out.println(s);
//        }
//        System.out.println(ps.size());
//
//        List<String> IFPs = query.getIFPs();
//        for(String ifp : IFPs){
//            System.out.println(ifp);
//        }
//        System.out.println(IFPs.size());

    }




}
