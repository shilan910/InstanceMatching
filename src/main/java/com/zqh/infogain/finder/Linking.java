package com.zqh.infogain.finder;

import com.zqh.infogain.DBHelper;
import com.zqh.infogain.entity.Triple;
import com.zqh.util.Levenshtein;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2017/4/26.
 */
public class Linking {
    private static List<String> p1p2s;
    private static Set<String> subjects1;
    private static Set<String> subjects2;
    private static List<String> p1s;
    private static List<String> p2s;
    private static Set<Triple> triples1;
    private static Set<Triple> triples2;
    private static List<EntityForLinking> entities1;
    private static List<EntityForLinking> entities2;

    public static void main(String args[]){
        link();
    }

    private static void link(){
        p1s = new ArrayList<String>();
        p2s = new ArrayList<String>();
        subjects1 = new HashSet<String>();
        subjects2 = new HashSet<String>();
        getDiscriminativePropertyPair();
        System.out.println("getDiscriminativePropertyPair()");
        System.out.println("DiscriminativePropertyPairNum = "+p1p2s.size());
//        getTriples();

        ReadModel rm1 = new ReadModel("D:\\data\\new\\people.rdf",p1s);
        ReadModel rm2 = new ReadModel("D:\\data\\new\\persondata_en.nt",p2s);

        entities1 = rm1.read();
//        getEntities(subjects1,triples1,p1s,entities1);
        System.out.println("getEntities1");
        System.out.println(entities1.size());
        entities2 = rm2.read();
//        getEntities(subjects2,triples2,p2s,entities2);
        System.out.println("getEntities2");
        System.out.println(entities2.size());

        for(EntityForLinking entity1 : entities1){
            double sim=0,cnt=0,temp;
            String aim = "";
            for(EntityForLinking entity2 : entities2){
                temp = 0;
                cnt = 0;
                for(String p1p2 : p1p2s){
                    String pp[] = p1p2.split("#~#");
                    if(entity1.getP_os().containsKey(pp[0]) && entity2.getP_os().containsKey(pp[1])){
                        temp += countLinkValue(entity1.getP_os(),entity2.getP_os(),pp);
//                        if(temp>0){
                        cnt++;
                        //}
                    }
                }
                if(temp/cnt>sim){
                    aim = entity2.getSubject();
                    sim = temp/cnt;
                }
               // System.out.println(entity1.getSubject()+"   "+entity2.getSubject()+"   "+sim/cnt);
//                if(temp/cnt>=0.99){
//                    insertLinkdata(entity1.getSubject(),entity2.getSubject(),sim/cnt);
//                    System.out.println("insertLinkdata ... "+entity1.getSubject()+"   "+entity2.getSubject()+"   "+sim/cnt);
//                }
            }
            if(sim>=0.90){
                insertLinkdata(entity1.getSubject(),aim,sim);
                System.out.println("insertLinkdata ... "+entity1.getSubject()+"   "+aim+"   "+sim);
           }
        }
    }

    private static void insertLinkdata(String s1 , String s2 , double measure){
        try {
            Connection conn = DBHelper.getConn();
            String sql = "insert into nyt_dbpedia_people_linkdata (s1,s2,measure) values(?,?,?)";
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

    private static double countLinkValue(Map<String,List<String>> p_os1 , Map<String,List<String>> p_os2 , String []pp){
        double sim=0;
        List<String> o1s = new ArrayList<String>();
        List<String> o2s = new ArrayList<String>();

        for(Map.Entry p_o1 : p_os1.entrySet())
            if(p_o1.getKey().toString().equals(pp[0]))
                o1s = (List<String>) p_o1.getValue();

        for(Map.Entry p_o2 : p_os2.entrySet())
            if(p_o2.getKey().toString().equals(pp[1]))
                o2s = (List<String>) p_o2.getValue();

//        System.out.println("o1s.size() = "+o1s.size());
//        System.out.println("o2s.size() = "+o2s.size());

        for(String o1 : o1s)
            for(String o2 : o2s)
                if(Levenshtein.levenshtein(o1,o2)>sim) {
                    sim = Levenshtein.levenshtein(o1, o2);
//                    System.out.prinln(o1+"    "+o2);
                }
        return sim;
    }

    private static void getEntities(Set<String> subjects , Set<Triple> triples , List<String> ps , List<EntityForLinking> entities){
        System.out.println("subjects.size() = "+subjects.size());
        for(String s : subjects){
            Map<String,List<String>> p_os = new HashMap<String, List<String>>();
            List<Triple> tempTriples = new ArrayList<Triple>();
            for(Triple triple : triples) {
                if (triple.getSubject().equals(s))
                    tempTriples.add(triple);
            }
            for(String p : ps){
                List<String> os = new ArrayList<String>();
                for(Triple triple1 : tempTriples)
                    if(triple1.getProperty().equals(p))
                        os.add(triple1.getObject());
                p_os.put(p,os);
            }
            entities.add(new EntityForLinking(s,p_os));
        }
    }

    private static void getDiscriminativePropertyPair(){
        p1p2s = new ArrayList<String>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select p1,p2,ig from discriminative_pair WHERE source=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,"nyt_dbpedia_locations");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                String p1 = rs.getString(1);
                String p2 = rs.getString(2);
                p1p2s.add(p1+"#~#"+p2);
//                getSubjects(p1,p2);
                p1s.add(p1);
                p2s.add(p2);
            }
            rs.close();
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getSubjects(String p1 , String p2){
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT s from nyt_people WHERE p=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,p1);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                subjects1.add(rs.getString(1));
            }
            rs.close();
            statement.close();

            String sql1 = "select DISTINCT s from dbpedia_people WHERE p=?";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            statement1.setString(1,p2);
            ResultSet rs1 = statement1.executeQuery();
            while(rs1.next()){
                subjects2.add(rs1.getString(1));
            }
            rs1.close();
            statement1.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getTriples(){
        triples1 = new HashSet<Triple>();
        triples2 = new HashSet<Triple>();
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select DISTINCT s,o from nyt_people WHERE p=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            for(String p : p1p2s){
                String pp[] = p.split("#~#");
                statement.setString(1,pp[0]);
                System.out.println(p);
                System.out.println(pp[0]);
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    triples1.add(new Triple(rs.getString(1),pp[0],
                            rs.getString(2)));
                }
                rs.close();
            }
            statement.close();

            String sql1 = "select DISTINCT s,o from dbpedia_people WHERE p=?";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            for(String p : p1p2s){
                String pp[] = p.split("#~#");
                statement1.setString(1,pp[1]);
                ResultSet rs = statement1.executeQuery();
                while(rs.next()){
                    triples2.add(new Triple(rs.getString(1),pp[1],
                            rs.getString(2)));
                }
                rs.close();
            }
            statement.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void testSameAs(){

    }

}
