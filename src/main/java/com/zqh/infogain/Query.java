package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2017/4/4.
 */
public class Query {

    private String filename;
    private String URI_prefix;
    private static Model model;
    private List<String> subjectList;
    private List<Entity> entities;
    private List<String> properties;
    private List<String> propertiesRaw;
    private List<String> FPs;
    private List<String> IFPs;
    private Map<String,List<String>> sameAs;

    /**
     *构造方法
     * @param filename1 ： 文件路径
     */
    public Query(String filename1 , String filename2) {
        subjectList = new ArrayList<String>();
        entities = new ArrayList<Entity>();
        properties = new ArrayList<String>();
        propertiesRaw = new ArrayList<String>();
        FPs = new ArrayList<String>();
        IFPs = new ArrayList<String>();
        sameAs = new HashMap<String, List<String>>();

        model = ModelFactory.createDefaultModel();
        InputStream in1 = FileManager.get().open(filename1);
        if(filename1.endsWith(".nt")){
            model.read(in1,"","N3");
        }else if(filename1.endsWith(".ttl")) {
            model.read(in1,"","TTL");
        }else{
            model.read(in1,null);
        }

        InputStream in = FileManager.get().open(filename2);
        if(filename2.endsWith(".nt")){
            model.read(in,"","N3");
        }else if(filename2.endsWith(".ttl")) {
            model.read(in,"","TTL");
        }else{
            model.read(in,null);
        }

        extractSubjectList();
        extractProperty();
//        extractFPs();
//        extractIFPs();
        extractSameAs();
    }

    /**
     * 提取属性
     * @return 属性列表
     */
    private void extractProperty(){
        String queryString = "SELECT ?predicate WHERE { ?subject ?predicate ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String predicate = querySolution.get("predicate").toString();
            //去重
            if(!propertiesRaw.contains(predicate) && !predicate.endsWith("type") && !predicate.endsWith("age")){
                propertiesRaw.add(predicate);
                String p = extractString(predicate);
                if(!properties.contains(p))
                    properties.add(extractString(p));
            }
        }
        queryExecution.close();


    }

    private void extractSubjectList(){
        //提取subject
        boolean flag = true;
        for(StmtIterator i = model.listStatements(); i.hasNext() ; ){
            Statement j = i.nextStatement();
            String subject = j.getSubject().toString();
            if(flag){
                URI_prefix = subject.substring(0,subject.lastIndexOf("/")+1);
                flag = false;
            }
            if(!subjectList.contains(subject)){
                subjectList.add(subject);
            }
        }
    }

    /**
     * 提取实体
     * @return 实体列表
     */
    public List<Entity> extractEntity(){
        for(String subject : subjectList) {
            Map<String,List<String>> predicate_objects = new HashMap<String, List<String>>();
            queryPredicateAndObject(predicate_objects, subject);
            if (predicate_objects.size() != 0) {
                entities.add(new Entity(subject, predicate_objects));
            }
        }
        return entities;
    }

    private void queryPredicateAndObject(Map<String,List<String>> predicate_objects , String subject){

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
            object = extractString(object);
//            predicate = extractString(predicate);
            if(object!=""){
                if(predicate_objects.containsKey(predicate)){
                    List<String> objects = predicate_objects.get(predicate);
                    objects.add(object);
                }else{
                    List<String> objects = new ArrayList<String>();
                    objects.add(object);
                    predicate_objects.put(predicate,objects);
                }
            }
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
            s = s.substring(index+1).replaceAll("[-\\s+]","_").replaceAll("[(*)]","");
        }
        return s;
    }


    /**
     * 找出函数属性
     * @return
     */
    private void extractFPs(){
        for(String p : propertiesRaw){
            boolean isFP = true;
            Set<String> subjects = new HashSet<String>();
            int cnt = 0;
            String queryString ="SELECT ?s?o WHERE { ?s <"+p+"> ?o}";
            org.apache.jena.query.Query query = QueryFactory.create(queryString);
            QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
            ResultSet resultSet = queryExecution.execSelect();
            while(resultSet.hasNext()){
                cnt++;
                QuerySolution querySolution = resultSet.next();
                String subject = querySolution.get("s").toString();
                subjects.add(subject);
            }
            queryExecution.close();
            if(subjects.size()==cnt && !FPs.contains(p))
                FPs.add(p);
        }
    }

    /**
     * 找出反函数属性
     * @return
     */
    private void extractIFPs(){
        for(String p : propertiesRaw){
            boolean isIFP = true;
            Map<String,String> map = new HashMap<String, String>();
            String queryString ="SELECT ?s?o WHERE { ?s <"+p+"> ?o}";
            org.apache.jena.query.Query query = QueryFactory.create(queryString);
            QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
            ResultSet resultSet = queryExecution.execSelect();
            while(resultSet.hasNext()){
                QuerySolution querySolution = resultSet.next();
                String subject = querySolution.get("s").toString();
                String object = querySolution.get("o").toString();
                if(!object.equals(""))
                if(!map.containsKey(subject) && !map.containsValue(object)){
                    map.put(subject,object);
                }else{
                    isIFP = false;
                    break;
                }
            }
            queryExecution.close();
            if(isIFP){
                if(!IFPs.contains(p))
                    IFPs.add(p);
            }
        }
    }


    private void extractSameAs(){
        System.out.println("subjectList.size() = "+subjectList.size());
        String queryString ="SELECT ?s1?s2 WHERE { ?s1 <http://www.w3.org/2002/07/owl#sameAs> ?s2}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String s1 = querySolution.get("s1").toString();
            String s2 = querySolution.get("s2").toString();
            if(subjectList.contains(s1) && subjectList.contains(s2)){
                if(sameAs.containsKey(s1)){
                    List<String> s1_relations = sameAs.get(s1);
                    if(!s1_relations.contains(s2))
                        s1_relations.add(s2);
                }else{
                    List<String> s1_relations = new ArrayList<String>();
                    s1_relations.add(s2);
                    sameAs.put(s1,s1_relations);
                }
                if(sameAs.containsKey(s2)){
                    List<String> s2_relations = sameAs.get(s2);
                    if(!s2_relations.contains(s1))
                        s2_relations.add(s1);
                }else{
                    List<String> s2_relations = new ArrayList<String>();
                    s2_relations.add(s1);
                    sameAs.put(s2,s2_relations);
                }
            }

        }
        queryExecution.close();
    }


    public List<String> getProperties() {
        return properties;
    }

    public List<String> getFPs() {
        return FPs;
    }

    public List<String> getIFPs() {
        return IFPs;
    }

    public Map<String, List<String>> getSameAs() {
        return sameAs;
    }

    public List<String> getPropertiesRaw() {
        return propertiesRaw;
    }

    public static void main(String args[]){
//        Query query = new Query("D:\\data\\new\\people.rdf");
//        Map<String, List<String>> map = query.getSameAs();
//        System.out.println("map.size() = "+map.size());
//
//        List<Entity> entities = query.extractEntity();
//        System.out.println("\n");
//        for(Entity entity : entities){
//            System.out.println(entity.getSubject()+" : "+entity.getPredicate_objects().size());
//        }
//        System.out.println(entities.size());
//
//        List<String> ps = query.getProperties();
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
