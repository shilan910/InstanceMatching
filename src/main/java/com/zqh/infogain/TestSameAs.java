package com.zqh.infogain;

import com.zqh.Result;
import com.zqh.infogain.finder.EntityForLinking;
import org.apache.jena.query.*;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * Created by Administrator on 2017/5/4.
 */
public class TestSameAs {

    private Model model1;
    private Model model2;
    private Set<String> subjects;
    private static List<Result> exps;
    private static List<Result> exps2;

    public TestSameAs(String filename1 , String filename2){
        exps = new ArrayList<Result>();
        exps2 = new ArrayList<Result>();
        subjects = new HashSet<String>();
        model1 = ModelFactory.createDefaultModel();
        InputStream in1 = FileManager.get().open(filename1);
        if(filename1.endsWith(".nt")){
            model1.read(in1,"","N3");
        }else if(filename1.endsWith(".ttl")) {
            model1.read(in1,"","TTL");
        }else{
            model1.read(in1,null);
        }
        model2 = ModelFactory.createDefaultModel();
        InputStream in2 = FileManager.get().open(filename2);
        if(filename2.endsWith(".nt")){
            model2.read(in2,"","N3");
        }else if(filename2.endsWith(".ttl")) {
            model2.read(in2,"","TTL");
        }else{
            model2.read(in2,null);
        }
        getSubjects();
       // read();
    }


    public void read(){
        String queryString = "SELECT ?s?o WHERE { ?s <http://www.w3.org/2002/07/owl#sameAs> ?o}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model1);
        ResultSet resultSet = queryExecution.execSelect();
        String s,o;
        while (resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            o = querySolution.get("o").toString();
            s = querySolution.get("s").toString();
            if(o.startsWith("http://dbpedia.org/resource/") && !s.endsWith("/"))
                exps2.add(new Result(s,o,"1.0"));
        }
        queryExecution.close();
    }

    private void getSubjects(){
        String queryString = "SELECT ?s?p?o WHERE { ?s ?p ?o}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model2);
        ResultSet resultSet = queryExecution.execSelect();
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            subjects.add(querySolution.get("s").toString());
        }
        queryExecution.close();
    }

    private static void getExps(){
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select s1,s2 from nyt_dbpedia_locations_linkdata";
            PreparedStatement statement = conn.prepareStatement(sql);
            java.sql.ResultSet rs = statement.executeQuery();
            while(rs.next()){
                exps.add(new Result(rs.getString(1),rs.getString(2),"1.0"));
            }
            rs.close();
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertLinkdata(String s1 , String s2 , double measure){
        try {
            Connection conn = DBHelper.getConn();
            String sql = "insert into nyt_dbpedia_locations_linkdata (s1,s2,measure) values(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,s1);
            statement.setString(2,s2);
            statement.setDouble(3,measure);
            statement.execute();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        TestSameAs test = new TestSameAs("D:\\data\\new\\locations.rdf","D:\\data\\new\\geo_en.nt");
        test.read();
        int cnt=0;
        for(Result result : exps2){
            if(!exps.contains(result)){
                exps.add(result);
            }
        }
        System.out.println(exps2.size());
        for(Result result : exps){
            insertLinkdata(result.getEntity1(),result.getEntity2(),1.0);
        }
        System.out.println(exps.size());
    }

}
