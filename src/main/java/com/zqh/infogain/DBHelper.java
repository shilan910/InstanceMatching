package com.zqh.infogain;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/4/24.
 */
public class DBHelper {

    public static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/oaei";
        String username = "root";
        String password = "root";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void insert(String s , String p , String o) {
        Connection conn = getConn();
        int i = 0;
        String sql = "insert into dbpedia_people (s,p,o) values(?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, s);
            pstmt.setString(2, p);
            pstmt.setString(3, o);
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]) {
        String filename = "D:\\data\\new\\persondata_en.nt";
        Model model = ModelFactory.createDefaultModel();
        InputStream in1 = FileManager.get().open(filename);
        if(filename.endsWith(".nt")){
            model.read(in1,"","N3");
        }else if(filename.endsWith(".ttl")) {
            model.read(in1,"","TTL");
        }else{
            model.read(in1,null);
        }


        String queryString = "SELECT ?s?p?o WHERE { ?s ?p ?o}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        String s,p,o;
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            s = querySolution.get("s").toString();
            p = querySolution.get("p").toString();
            o = querySolution.get("o").toString();
            insert(s,p,o);
        }
        queryExecution.close();
    }

}
