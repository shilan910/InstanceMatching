package com.zqh;

import com.zqh.FPGrowth.example.FP_Item;
import com.zqh.FPGrowth.example.RunFPGrowth;
import com.zqh.infogain.DBHelper;
import com.zqh.infogain.entity.Entity;
import org.dom4j.DocumentException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by sl on 2017/3/1.
 */
public class Compare {

    private static List<Result> reals;
    private static List<Result> exps;

    public Compare(List<Result> reals) {
        this.reals = reals;
        exps = new ArrayList<Result>();
    }

    public static void main(String args[]) throws DocumentException {
        XMLHelper xmlHelper = new XMLHelper("D:\\data\\new\\nytimes-mappings-split\\nyt-dbpedia-people-mappings.rdf");
        reals = xmlHelper.run();
        Compare compare = new Compare(reals);
        compare.compareResult();
    }

    /**
     * 与标准答案比较
     */
    public void compareResult(){
        getExps();
        int matchNum = 0;
        int notmatchNum = 0;

        for(Result exp : exps){
            if(reals.contains(exp)){
                matchNum++;
            }else{
                System.out.println(exp.getEntity1()+"   "+exp.getEntity2());
                notmatchNum++;
            }
        }

        double p = matchNum/(double)exps.size();
        double r = matchNum/(double)reals.size();
        System.out.println("真实结果集实体对数："+reals.size());
        System.out.println("实验结果集实体对数："+exps.size());
        System.out.println("正确匹配对数："+matchNum);
        System.out.println("错误匹配对数："+notmatchNum);
        System.out.println("未匹配对数："+(reals.size()-matchNum));
        System.out.println("准确率："+p*100+"%");
        System.out.println("召回率："+r*100+"%");
        System.out.println("F-1-measure: "+2*p*r/(p+r)*100+"%");
    }

    private static void getExps(){
        try{
            Connection conn = DBHelper.getConn();
            String sql = "select s1,s2 from nyt_dbpedia_linkdata";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
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

}
