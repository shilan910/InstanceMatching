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
 * Created by Administrator on 2017/4/25.
 */
public class EqualRelation {

    private static List<String> subjects;
    private static List<String> s1_s2s;

    public static void main(String args[]){
//        SameAsFinder.selectSameAs("nyt_people","dbpedia_people","nyt_dbpedia_equalrelations");
        getSubjects();
        getTransitiveClosure();
    }

    private static void getTransitiveClosure(){
        int a[][] = new int[3000][3000];
        System.out.println(1111);
        for(String s1_s2 : s1_s2s){
            String ss[] = s1_s2.split("#");
            int i = indexOf(ss[0]);
            int j = indexOf(ss[1]);
            a[i][j] = 1;
            a[j][i] = 1;
        }
        System.out.println(2222);
        for(int k=0 ; k<3000 ; k++)
            for(int i=0 ; i<3000 ; i++)
                for(int j=0 ; j<3000 ; j++)
                    if(a[i][k]!=0 && a[k][j]!=0){
                        a[i][j] = 2;
                    }
        System.out.println(3333);
        for(int i=0 ; i<3000 ; i++)
            for(int j=i+1 ; j<3000 ; j++){
                if(a[i][j]==2){
                    String s1 = subjects.get(i);
                    String s2 = subjects.get(j);
                    if(!s1_s2s.contains(s1+"#"+s2) && !s1_s2s.contains(s2+"#"+s1)) {
                        insertEqualRelation(s1,s2);
                        System.out.println(s1+"  "+s2);
                    }
                }
            }

    }

    private static void insertEqualRelation(String s1 , String s2){
        try {
            Connection conn = DBHelper.getConn();
            String sql = "insert into nyt_dbpedia_equalrelations (s1,s2) values(?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,s1);
            statement.setString(2,s2);
            statement.execute();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int indexOf(String subject){
        int size = subjects.size();
        for(int i=0 ; i<size ; i++){
            if(subjects.get(i).equals(subject))
                return i;
        }
        return -1;
    }

    private static void getSubjects(){
        Set<String> subjectsTemp = new HashSet<String>();
        subjects = new ArrayList<String>();
        s1_s2s = new ArrayList<String>();
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
                s1_s2s.add(s1+"#"+s2);
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        subjects.addAll(subjectsTemp);
    }
}
