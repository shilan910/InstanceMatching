package com.zqh.infogain.finder;

import com.zqh.infogain.DBHelper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 */
public class SameAsFinder {

    public static void selectSameAs(String tableName ,String tableName1, String equalRelationTableName) {
        List<String> subjects = getSubjectsFromAnother(tableName1);
        System.out.println("subjects.size() = "+subjects.size());
        try {
            Connection conn = DBHelper.getConn();
            String sql = "select s,o from nyt_people where p=?";
            PreparedStatement statement = conn.prepareStatement(sql);
//            statement.setString(1,tableName);
            statement.setString(1,"http://www.w3.org/2002/07/owl#sameAs");

            String sql1 = "insert into nyt_dbpedia_equalrelations (s1,s2) values(?,?)";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
//            statement1.setString(1,equalRelationTableName);

            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String s1 = rs.getString(1);
                String s2 = rs.getString(2);
                if(subjects.contains(s2)){
                    statement1.setString(1,s1);
                    statement1.setString(2,s2);
                    statement1.execute();
                }
            }
            rs.close();
            statement.close();
            statement1.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getSubjectsFromAnother(String tableName1){
        List<String> subjects = new ArrayList<String>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT s from dbpedia_people";
            PreparedStatement statement = conn.prepareStatement(sql);
//            statement.setString(1,tableName1);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                subjects.add(rs.getString(1));
            }
            rs.close();
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }
}
