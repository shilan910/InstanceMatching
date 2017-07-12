package com.zqh.infogain.finder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */
public class FPFinder {

    public static void main(String args[]){
        List<String> ps = selectFP();
        for(String p : ps){
            insert(p,"f","dbpedia_people");
        }
    }


    private static List<String> selectFP() {
        int cnt = 0;
        List<String> ps = new ArrayList<String>();
        try {
            Connection conn = getConn();
            Statement stmt = conn.createStatement(); //创建Statement对象
            String sql = "select DISTINCT p from dbpedia_people";    //要执行的SQL
            String sql1 = "select s from dbpedia_people where p=?";
            PreparedStatement statement = conn.prepareStatement(sql1);
            ResultSet rs = stmt.executeQuery(sql);//创建数据对象
            while (rs.next()) {
                String p = rs.getString(1);
                statement.setString(1,p);
                ResultSet rs1 = statement.executeQuery();
                List<String> ss = new ArrayList<String>();
                cnt=0;
                while(rs1.next()){
                    String s = rs1.getString(1);
                    if(!ss.contains(s)){
                        ss.add(s);
                    }else if(p.equals("http://xmlns.com/foaf/0.1/givenname"))
                        System.out.println("zqh : "+s);
                    cnt ++;
                }
                System.out.println(p);
                System.out.println(cnt + "   "+ss.size()+"\n");
                if(cnt==ss.size())
                    ps.add(p);
                rs1.close();
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }


    private static void insert(String p , String f_i , String source) {
        Connection conn = getConn();
        int i = 0;
        String sql = "insert into fp_ifp (property,f_i,source) values(?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, p);
            pstmt.setString(2, f_i);
            pstmt.setString(3, source);
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static Connection getConn() {
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


}
