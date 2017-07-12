package com.zqh.infogain.finder;

import com.zqh.infogain.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/26.
 */
public class Examples {

    private static List<String> subjectsPositive;
    private static List<String> subjects;

    public static void main(String args[]){
        generatePositiveExamples();
        generateNegativeExamples();
    }

    private static void generateNegativeExamples(){
        getSubjects();
        for(String sp : subjectsPositive)
            for(String s : subjects)
                if(compareString(sp,s)){
                    insertExamples(sp,"n",s);
                    insertExamples(s,"n",sp);
                }
    }

    private static void generatePositiveExamples(){
        subjectsPositive = new ArrayList<String>();
        Set<String> subjectsTemp = new HashSet<String>();
        try {
            Connection conn = DBHelper.getConn();
            String sql = "select s1,s2 from nyt_dbpedia_equalrelations";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            String s1,s2;
            while(rs.next()){
                s1 = rs.getString(1);
                s2 = rs.getString(2);
                subjectsTemp.add(s1);
                subjectsTemp.add(s2);
                insertExamples(s1,"p",s2);
                insertExamples(s2,"p",s1);
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        subjectsPositive.addAll(subjectsTemp);
    }

    private static void insertExamples(String s , String p_n , String belongto){
        try {
            Connection conn = DBHelper.getConn();
            String sql = "INSERT nyt_dbpedia_people_examples (s,p_n,belongto) values(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,s);
            statement.setString(2,p_n);
            statement.setString(3,belongto);
            statement.execute();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean compareString(String s1 , String s2){

        if(s1.endsWith(".rdf") || s2.endsWith(".rdf") || s1.equals(s2))
            return false;
        String reg="^\\d+$";
        int index;
        if(s1.contains("#"))
            index = s1.indexOf('#');
        else
            index = s1.lastIndexOf('/');
        s1 = s1.substring(index+1);

        if(s2.contains("#"))
            index = s2.indexOf('#');
        else
            index = s2.lastIndexOf('/');
        s2 = s2.substring(index+1);

        int f = 12;
        if(s1.matches(reg) && s2.matches(reg)) {
            f = 4;
           // System.out.println(s1+"   "+s2);
        }

        if(s1.length()>=f)
            s1 = s1.substring(0,f);
        if(s2.length()>=f)
            s2 = s2.substring(0,f);
//        if(s1.equals(s2))
//        System.out.println(s1+"   "+s2);
        return s1.equals(s2);
    }

    private static void getSubjects(){
        subjects = new ArrayList<String>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT s from dbpedia_people";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                subjects.add(rs.getString(1));
            }
            rs.close();
            statement.close();

            String sql1 = "select DISTINCT s from nyt_people";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            ResultSet rs1 = statement1.executeQuery();
            while(rs1.next()){
                subjects.add(rs1.getString(1));
            }
            rs1.close();
            statement1.close();

            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
