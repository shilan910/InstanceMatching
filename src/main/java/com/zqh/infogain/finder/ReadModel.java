package com.zqh.infogain.finder;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2017/5/3.
 */
public class ReadModel {

    private Model model;
    private List<String> ps;
    private Set<String> subjects;
    public ReadModel(String filename , List<String> ps){
        this.ps = ps;
        subjects = new HashSet<String>();
        model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(filename);
        if(filename.endsWith(".nt")){
            model.read(in,"","N3");
        }else if(filename.endsWith(".ttl")) {
            model.read(in,"","TTL");
        }else{
            model.read(in,null);
        }
        getSubjects();
    }


    public List<EntityForLinking> read(){
        List<EntityForLinking> entities = new ArrayList<EntityForLinking>();
        for(String s : subjects){
            Map<String,List<String>> p_os = new HashMap<String, List<String>>();
            for (String p : ps){
                List<String> os = new ArrayList<String>();
                String queryString = "SELECT ?o WHERE { <"+s+"> <"+p+"> ?o}";
                org.apache.jena.query.Query query = QueryFactory.create(queryString);
                QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
                ResultSet resultSet = queryExecution.execSelect();
                while (resultSet.hasNext()){
                    QuerySolution querySolution = resultSet.next();
                    os.add(querySolution.get("o").toString());
                }
                queryExecution.close();
                p_os.put(p,os);
            }
            entities.add(new EntityForLinking(s,p_os));
        }
        return entities;
    }

    private void getSubjects(){
        for(String p : ps){
            String queryString = "SELECT ?s?o WHERE { ?s <"+p+"> ?o}";
            org.apache.jena.query.Query query = QueryFactory.create(queryString);
            QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
            ResultSet resultSet = queryExecution.execSelect();
            while(resultSet.hasNext()){
                QuerySolution querySolution = resultSet.next();
                subjects.add(querySolution.get("s").toString());
            }
            queryExecution.close();
        }
    }

}
