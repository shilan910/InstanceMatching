package com.zqh;

import com.zqh.FPGrowth.example.FP_Item;
import com.zqh.FPGrowth.example.RunFPGrowth;
import com.zqh.infogain.entity.Entity;

import java.util.*;

/**
 * Created by sl on 2017/3/1.
 */
public class Compare {

    private List<Entity> entities1;
    private List<Entity> entities2;
    private List<FP_Item> FP_items;
    private List<Result> reals;
    private List<Result> exps;

    public Compare(List<Entity> entities1, List<Entity> entities2, List<Result> reals , List<FP_Item> FP_items) {
        this.entities1 = entities1;
        this.entities2 = entities2;
        this.reals = reals;
        this.FP_items = FP_items;
        exps = new ArrayList<Result>();
    }

    /**
     * 与标准答案比较
     */
    public void compareResult(){

        compareEntity();

        int matchNum = 0;
        int notmatchNum = 0;

        for(Result exp : exps){
            if(reals.contains(exp)){
                matchNum++;
            }else{
                notmatchNum++;
//                System.out.println("错误匹配：");
//                System.out.println(exp.getEntity1());
//                System.out.println(exp.getEntity2()+"\n");
            }
        }
//        for(Result re : exps){
//            if(re.getEntity1().equals("http://www.okkam.org/oaie/person1-Person00"))
//                System.out.println(re.getEntity2()+"\n");
////            break;
//        }
//
//        for(Result re : reals){
//            System.out.println(re.getEntity1());
//            System.out.println(re.getEntity2()+"\n");
//            break;
//        }

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

    /**
     * 匹配两个实体集合
     */
    private void compareEntity(){


        for(Entity entity : entities1){
            findItemsForEntity(entity);
        }

        for(Entity entity : entities2){
            findItemsForEntity(entity);
        }

        System.out.println("findItemsForEntity done!");
        System.out.println("entities1.size() = "+entities1.size());
        System.out.println("entities2.size() = "+entities2.size());

        int cnt=1;
        int temp=1;
        for(Entity entity1 : entities1){
            double measure=0;
//            List<FP_Item> fp_items1 = entity1.getItems();
//            System.out.println("\nfp_items1 = "+fp_items1.size());
//            System.out.println(cnt++);
            for(Entity entity2 : entities2){
//                List<FP_Item> fp_items2 = entity2.getItems();
//                System.out.println("fp_items2 = "+fp_items2.size());

//                for(FP_Item fp_item2 : fp_items2){
//                    if(fp_items1.contains(fp_item2)){
//                        measure += fp_item2.getWeight();
//                    }
                }
                if(measure >= 95){
//                    if(temp%1007==0){
//                        System.out.println("entity1.getSubject() : "+entity1.getSubject());
//                        System.out.println("entity2.getSubject() : "+entity2.getSubject());
//                        System.out.println("measure = "+measure);
//                    }
                    temp++;
//                    exps.add(new Result("http://www.okkam.org/oaie/"+entity1.getSubject(),
//                            "http://www.okkam.org/oaie/"+entity2.getSubject(),"1.0"));
                }

            }

        }


//    }

    private void findItemsForEntity(Entity entity){
        List<String> predicate_object = entity.getPredicate_object();
//        List<FP_Item> items = entity.getItems();

        for(FP_Item fp : FP_items){
            boolean flag = true;
            List<String>  fp_items = fp.getItems();
            for(String item : fp_items){
                if(!predicate_object.contains(item)){
                    flag = false;
                    break;
                }
            }
//            if(flag)
//                items.add(fp);
        }
    }

}
