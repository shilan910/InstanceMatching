package com.zqh.infogain.finder;

import com.zqh.infogain.DBHelper;
import com.zqh.util.Levenshtein;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/26.
 */
public class SimilarityPropertyObjectPair {

    private static List<String> properties1;
    private static List<String> properties2;
    private static double sim = 0.8;

    public static void main(String args[]){
        getPropertyObjectPair();
    }

    private static void getPropertyObjectPair(){
        getProperties();
        Map<String,List<String>> p_os = new HashMap<String, List<String>>();
        int size1 = properties1.size();
        int size2 = properties2.size();
        for(String p : properties1) {
            p_os.put(p,getObjectByProperty(p));
        }
        for(String p : properties2) {
            p_os.put(p,getObjectByProperty(p));
        }

        for(int i=0 ; i<size1 ; i++){
            List<String> o1s = p_os.get(properties1.get(i));
            for(int j=i+1 ; j<size2 ; j++){
                List<String> o2s = p_os.get(properties2.get(j));
                for(String o1 : o1s)
                    for(String o2 : o2s)
                        if(Levenshtein.levenshtein(o1,o2)>=sim){
                            insertPPOO(properties1.get(i),properties2.get(j),o1,o2);
                        }
            }
        }
    }

    private static void insertPPOO(String p1 , String p2 , String o1 , String o2){
        try {
            Connection conn = DBHelper.getConn();
            String sql = "INSERT nyt_dbpedia_people_ppoo (p1,p2,o1,o2) values(?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,p1);
            statement.setString(2,p2);
            statement.setString(3,o1);
            statement.setString(4,o2);

            statement.execute();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getObjectByProperty(String p){
        List<String> obejcts = new ArrayList<String>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT o from dbpedia_people WHERE p=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,p);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                obejcts.add(rs.getString(1));
            }
            rs.close();
            statement.close();

            String sql1 = "select DISTINCT o from nyt_people WHERE p=?";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            statement1.setString(1,p);
            ResultSet rs1 = statement1.executeQuery();
            while(rs1.next()){
                obejcts.add(rs1.getString(1));
            }
            rs1.close();
            statement1.close();

            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return obejcts;
    }

    private static void getProperties(){
        properties1 = new ArrayList<String>();
        properties2 = new ArrayList<String>();

        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT p from dbpedia_people";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                properties1.add(rs.getString(1));
            }
            rs.close();
            statement.close();

            String sql1 = "select DISTINCT p from nyt_people";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            ResultSet rs1 = statement1.executeQuery();
            while(rs1.next()){
                properties2.add(rs1.getString(1));
            }
            rs1.close();
            statement1.close();

            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
