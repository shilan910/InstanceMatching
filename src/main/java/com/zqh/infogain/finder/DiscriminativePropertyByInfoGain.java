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
public class DiscriminativePropertyByInfoGain {

    private static double H_D;
    private static List<String> properties1;
    private static List<String> properties2;
    private static int peNum;
    private static int neNum;
    private static int size;

    public static void main(String args[]){
//        List<String> ss = getSubjectsBy2P("http://www.w3.org/2004/02/skos/core#prefLabel","http://xmlns.com/foaf/0.1/name");
//        System.out.println(ss.size());
        peNum = getAllExamplesNum("p")/2;
        neNum = getAllExamplesNum("n")/2;
        size = getSubjectsNum();
        H_D = getEntropy(peNum,neNum,size);
        System.out.println(peNum);
        System.out.println(neNum);
        System.out.println(size);
        System.out.println(H_D);
        getDiscriminativePropertyPair();
    }

    private static void getDiscriminativePropertyPair(){
        getProperties();
//        for(String p1 : properties1){
//            for(String p2 : properties2){
        String p2="http://xmlns.com/foaf/0.1/name";
        String p1="http://www.w3.org/2002/07/owl#sameAs";
                int cnt_p=0 , cnt_n=0;
                List<String> subjects = getSubjectsBy2P(p1,p2);
                for(String s : subjects) {
                    cnt_p += getExampleNumBySubject(s, "p");
                    cnt_n += getExampleNumBySubject(s, "n");
                }
                System.out.println(p1+"    "+p2);
                double Q = subjects.size();
                double R = size - Q;
                double H_Q = getEntropy(cnt_p,cnt_n,Q);
                double H_R = getEntropy(peNum-cnt_p,neNum-cnt_n,R);
                double H_Dp1p2 = Q/(double)size*H_Q + R/(double)size*H_R;
                double ig = 0;
                ig = H_D - H_Dp1p2;
                System.out.println("Q = "+Q+"   R = "+R);
                System.out.println("cnt_p = "+cnt_p+"   cnt_n = "+cnt_n);
                System.out.println("H_Q = "+H_Q);
                System.out.println("H_R = "+H_R);
                System.out.println("H_Dp1p2 = "+H_Dp1p2);
                System.out.println("ig = "+ig+"\n");

                if(ig>=0.01)
                    insertDiscriminativePropertyPair(p1,p2,ig);

//            }
//        }
    }

    private static void insertDiscriminativePropertyPair(String p1 , String p2 , double ig){
        try {
            Connection conn = DBHelper.getConn();
            String sql = "INSERT nyt_dbpedia_people_discriminative_pair (p1,p2,ig) values(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,p1);
            statement.setString(2,p2);
            statement.setDouble(3,ig);
            statement.execute();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static int getExampleNumBySubject(String s , String p_n){
        Set<String> ss = new HashSet<String>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select s from nyt_dbpedia_people_examples WHERE belongto=? AND p_n=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,s);
            statement.setString(2,p_n);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ss.add(rs.getString(1));
            }
            rs.close();
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return ss.size();
    }

    private static double getEntropy(int peNum , int neNum , double sum){
        sum = sum*sum;
        double pe_Proportion = (double)peNum*peNum / sum;
        double ne_Proportion = (double)neNum*neNum / sum;
        double h = -1*pe_Proportion*Math.log(pe_Proportion)/Math.log(2)-ne_Proportion*Math.log(ne_Proportion)/Math.log(2);
        return h;
    }

    private static int getSubjectsNum(){
        List<String> subjects = new ArrayList<String>();
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
        return subjects.size();
    }

    private static int getAllExamplesNum(String p_n){
        int num=0;
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select COUNT(s) from nyt_dbpedia_people_examples WHERE p_n=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,p_n);
            ResultSet rs = statement.executeQuery();
            while(rs.next())
                num = rs.getInt(1);
            rs.close();
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    private static void getProperties(){
        properties1 = new ArrayList<String>();
        properties2 = new ArrayList<String>();

        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT p from nyt_people";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                properties1.add(rs.getString(1));
            }
            rs.close();
            statement.close();

            String sql1 = "select DISTINCT p from dbpedia_people";
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

    private static List<String> getSubjectsBy2P(String p1 , String p2){
        List<String> subjects = new ArrayList<String>();
//        Set<String> temp = new HashSet<String>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select o1,o2 from nyt_dbpedia_people_ppoo WHERE p1=? AND p2=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,p2);
            statement.setString(2,p1);
            ResultSet rs = statement.executeQuery();

            String sql1 = "SELECT s FROM nyt_people WHERE p=? AND o=?";
            PreparedStatement statement1 = conn.prepareStatement(sql1);

            String sql2 = "SELECT s FROM dbpedia_people WHERE p=? AND o=?";
            PreparedStatement statement2 = conn.prepareStatement(sql2);

            while(rs.next()){
                statement1.setString(1,p1);
//                System.out.println(p1+"    "+rs.getString(2));
                statement1.setString(2,rs.getString(2));
                ResultSet rs1 = statement1.executeQuery();
                while(rs1.next())
                    subjects.add(rs1.getString(1));
                rs1.close();

                statement2.setString(1,p2);
                statement2.setString(2,rs.getString(1));
                ResultSet rs2 = statement2.executeQuery();
                while(rs2.next())
                    subjects.add(rs2.getString(1));
                rs2.close();
            }
            rs.close();
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
//        subjects.addAll(temp);
        return subjects;
    }

}
