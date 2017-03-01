package com.zqh;


import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.util.*;

/**
 * Created by sl on 2017/1/16.
 */
public class FileToEntity {

    private String prefix;
    private String filenameOrURI;
    private Model model;

    public FileToEntity(String prefix , String filenameOrURI) {
        this.prefix = prefix;
        this.filenameOrURI = filenameOrURI;
    }



    /**
     * 提取当前model中的所有实体
     */
    public List<Entity> extractEntity(){
        List<String> subjectList = new ArrayList<String>();
        List<Entity> entities = new ArrayList<Entity>();


        model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(filenameOrURI);
        model.read(in,null);


        //提取subject
        for(StmtIterator i = model.listStatements() ; i.hasNext() ; ){
            Statement j = i.nextStatement();
            String subject = j.getSubject().toString();
            if(!subjectList.contains(subject) && subject.contains("Person")){
                subjectList.add(subject);
            }
        }

        for(String personName : subjectList){
            Map<String,String> predicate_object = new HashMap();

            queryAndLink(predicate_object,personName);

            entities.add(new Entity(personName,predicate_object));
        }

        return entities;
    }


    private void queryAndLink(Map<String,String> predicate_object , String subject){

        String queryString = "SELECT ?predicate?object WHERE { <"+subject+"> ?predicate ?object}";
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();


        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();

            String predicate = querySolution.get("predicate").toString();
            predicate = predicate.substring(predicate.indexOf('#')+1);

            String object = querySolution.get("object").toString();

            if(object.contains("http") && !object.contains("#")){//需要link
                queryAndLink(predicate_object , object);
            }else {
                if(object.contains("#")){
                    object = object.substring(object.indexOf('#')+1);
                }

                if(predicate.equals("name")){
                    if(subject.contains("State"))
                        predicate = "state";
                    else
                        predicate = "suburb";
                }else if(predicate.equals("type")){
                    object = "Person";
                }
                predicate_object.put(predicate,object);
            }
        }

        queryExecution.close();

    }



}
