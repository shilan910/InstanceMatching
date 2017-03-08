package com.zqh;

import com.zqh.util.Levenshtein;

import java.util.*;

/**
 * Created by sl on 2017/3/1.
 */
public class Compare {

    private List<Entity> entities1;
    private List<Entity> entities2;
    private List<Result> reals;

    private List<Result> exps;
    private Map<String,Double> predicate_weight;

    public Compare(List<Entity> entities1, List<Entity> entities2, List<Result> reals) {
        this.entities1 = entities1;
        this.entities2 = entities2;
        this.reals = reals;
        exps = new ArrayList<Result>();
        predicate_weight = new HashMap<String, Double>();
    }


    /**
     * 与标准答案比较
     */
    public void compareResult(){

        countWeight();

        System.out.println("zqh : "+predicate_weight.size());
        for(Map.Entry entry : predicate_weight.entrySet()){
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }

        compareEntity();


        int matchNum = 0;
        int notmatchNum = 0;

        for(Result exp : exps){
            if(reals.contains(exp)){
                matchNum++;
            }else{
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


    /**
     * 匹配两个实体集合
     */
    private void compareEntity(){

        double sum=0,cnt=0;

        for(Entity entity1 : entities1){
            Map<String,String> e1_predicate_object = entity1.getPredicate_object();

            List<Result> resultsForEntity1 = new ArrayList<Result>();

            for(Entity entity2 : entities2){
                Map<String,String> e2_predicate_object = entity2.getPredicate_object();

                double measure = 0;
                int cntForMatch = 0;
                int cntForAll = 0;
                for(Map.Entry<String,String> entry1 : e1_predicate_object.entrySet()) {
                    if (Levenshtein.levenshtein(entry1.getValue(), e2_predicate_object.get(entry1.getKey()))
                            >= 0.8) {
                        measure += predicate_weight.get(entry1.getKey());
                        cntForMatch++;
                    }
                    cntForAll++;
                }
                measure = measure*0.8 + cntForMatch/(double)cntForAll*0.2;
                resultsForEntity1.add(new Result(entity1.getSubject(),entity2.getSubject(),"=",
                        "http://www.w3.org/2001/XMLSchema#float",String.valueOf(measure)));

            }

            Collections.sort(resultsForEntity1);


            for (Result resultForMatch : resultsForEntity1){
                System.out.println(resultForMatch.getMeasure());
                double v1 = Double.valueOf(resultForMatch.getMeasure());
//                if(v1>0.15){
                    sum += v1;
                    cnt++;
//                }

                // TODO:参数控制：person1:0.8636435021360473  person2:0.0083
                if(Double.valueOf(resultForMatch.getMeasure())>=0.41){
//                    System.out.println(resultForMatch.getMeasure());
                    resultForMatch.setMeasure("1.0");
                    exps.add(resultForMatch);
                }
            }


        }
        System.out.println("平均 = "+sum/cnt);

    }


    /**
     * 计算每种属性的权重
     */
    public void countWeight(){
        Map<String,Double> predicate_weight_1 = countWeightForOneEntities(entities1);
        Map<String,Double> predicate_weight_2 = countWeightForOneEntities(entities2);

        double sum=0,cnt=0;

        for(Map.Entry entry1 : predicate_weight_1.entrySet()){
            String predicate = entry1.getKey().toString();
            double weight1 , weight2;
            if(entry1.getValue()==null || entry1.getValue().equals(""))
                weight1 = 0;
            else weight1= (Double)entry1.getValue();

            if(predicate_weight_2.get(predicate)==null || predicate_weight_2.get(predicate).equals(""))
                weight2 = 0;
            else weight2 = (Double)predicate_weight_2.get(predicate);
            sum += (weight1+weight2)/2.0;
            cnt++;

            predicate_weight.put(predicate , (weight1+weight2)/2.0);
        }

        System.out.println("权重  sum="+sum+"\n平均值="+sum/cnt);

    }

    /**
     * 服务于 countWeight 方法，针对每个entity List计算
     */
    private Map<String,Double> countWeightForOneEntities(List<Entity> entities){

        Map<String,Double> predicate_weight = new HashMap<String, Double>();

//        System.out.println("entities.size() : "+entities.size());

        //获取所有的predicate，并去重
        for(Entity entity : entities){
            Map<String,String> predicate_object = entity.getPredicate_object();
            for(Map.Entry entry : predicate_object.entrySet()){
                if(!predicate_weight.containsKey(entry.getKey())){
                    predicate_weight.put(entry.getKey().toString(),0.0);
                }
            }
        }
//        System.out.println("predicate_weight.size() : "+predicate_weight.size());


        int valueTypeNum , sum=0;

        //统计每种predicate拥有多少种不同的属性值
        for(Map.Entry entry : predicate_weight.entrySet()){

            String predicate = entry.getKey().toString();
            String[] values = new String[5000];
            boolean hasNull = false;
            int cnt=0;

            for(Entity entity : entities){
                Map<String,String> predicate_object = entity.getPredicate_object();
                for(Map.Entry entry1 : predicate_object.entrySet()){
                    if(predicate.equals(entry1.getKey())){
                        String temp = entry1.getValue().toString();
                        if(temp==null || temp.length()<=0){
                            hasNull = true;
                        }else{
                            values[cnt++] = entry1.getValue().toString();
                        }
                        break;
                    }
                }
            }

            Arrays.sort(values,0,cnt);

            String value="";
            valueTypeNum = 0;
            if(hasNull)
                valueTypeNum = 1;

            for(int i=0 ; i<cnt ; i++){
//                if(Levenshtein.levenshtein(values[i],value)<0.8){
                if(!values[i].equals(value)){
                    valueTypeNum++;
                    value = values[i];
                }
            }
//
//            if(predicate.equals("soc_sec_id")){
//                System.out.println(cnt);
//                System.out.println("\n<zqh>");
//                for(int i=0 ; i<cnt ; i++)
//                    System.out.println(values[i]);
//                System.out.println("</zqh>\n");
//                System.out.println(valueTypeNum);
//            }

            sum += valueTypeNum;
            entry.setValue(valueTypeNum);
        }


        for(Map.Entry entry : predicate_weight.entrySet()){
//            System.out.println(entry.getValue());
            entry.setValue(Double.valueOf(entry.getValue().toString())/(double)sum);
//            System.out.println(entry.getKey()+" : "+entry.getValue()+"\n");
        }

        return predicate_weight;

    }


}
