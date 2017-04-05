package com.zqh.FPGrowth.example;

import com.zqh.infogain.entity.Entity;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RunFPGrowth {

    private List<Entity> entities1;
    private List<Entity> entities2;

    public RunFPGrowth(List<Entity> entities1, List<Entity> entities2) {
        this.entities1 = entities1;
        this.entities2 = entities2;
    }

    private static List<List<String>> readTransRecords(String file){
        List<List<String>> transRecord=new LinkedList<List<String>>();
        try {
            BufferedReader bf=new BufferedReader(new FileReader(file));
            String line;
            List<String> record;
            while((line=bf.readLine())!=null){
                if(line.trim().length()>0){
                    String[] str=line.split(" ");
                    record=new LinkedList<String>();
                    for(String string:str){
                        record.add(string);
                    }
                    transRecord.add(record);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return transRecord;
    }

//    @Test
//    public void  run(){
    public List<FP_Item> run() {
        FPGrowth fpGrowth=new FPGrowth();
        fpGrowth.setMinSupport(3);
        String file="D:/data/record.txt";
//        dealTowEntities(file);
        List<List<String>> transRecords=RunFPGrowth.readTransRecords(file);

        ArrayList<FP_TreeNode> list=fpGrowth.buildHeaderTable(transRecords);

        fpGrowth.FPGrowth(transRecords, null);
        List<FP_Item>  FP_items  = fpGrowth.getFP_items();

        countWeightForItems(FP_items);

        List<FP_Item> FP_Items_slected = new ArrayList<FP_Item>();
        int cnt1=0,cnt2=0,cnt3=0,cnt4=0,cnt5=0;
        for(FP_Item item : FP_items) {
            if(item.getWeight() > 5){
                cnt3++;
                FP_Items_slected.add(item);
            }else{
//                System.out.println("zqh "+item.getWeight());
                cnt5++;
            }
        }
//        System.out.println("cnt1 = "+cnt1);
//        System.out.println("cnt2 = "+cnt2);
        System.out.println("cnt3 = "+cnt3);
//        System.out.println("cnt4 = "+cnt4);
        System.out.println("cnt5 = "+cnt5);

        System.out.println("FP_items.size() = " + FP_items.size());
        return FP_Items_slected;
    }


    private static void countWeightForItems(List<FP_Item>  FP_items){
        double maxCnt_item=0;
        for(FP_Item entry : FP_items){//统计频繁项集的最大项数
            double cnt_item = entry.getItems().size();
            if(cnt_item > maxCnt_item){
                maxCnt_item = cnt_item;
            }
        }

        double cnt_items = FP_items.size();

        for(FP_Item entry : FP_items){
            double cnt_item = entry.getItems().size();
            double weight = Math.log( cnt_items / (double) entry.getCount() ) / Math.log(2)
                            * cnt_item/maxCnt_item;
            entry.setWeight(weight);
        }
    }


//    private  void dealTowEntities(String fileName) {
//
//        String out = "";
//        for (Entity entity : entities1) {
//            List<String> predicate_object = entity.getPredicate_object();
//            for (String s : predicate_object) {
//                out = out + " " + s;
//            }
//            out += "\n";
//        }
//
//        for (Entity entity : entities2) {
//            List<String> predicate_object = entity.getPredicate_object();
//            for (String s1 : predicate_object)
//                out = out + " " + s1;
//            out += "\n";
//        }
//
//        FileOutputStream o = null;
//        try {
//            o = new FileOutputStream(fileName);
//            o.write(out.getBytes("GBK"));
//            o.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}