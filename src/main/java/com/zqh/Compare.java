package com.zqh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sl on 2017/3/1.
 */
public class Compare {

    public void compareResult(List<Result> reals , List<Result> exps){

        int matchNum = 0;
        int notmatchNum = 0;

        for(Result exp : exps){
            if(reals.contains(exp)){
                matchNum++;
            }else{
                notmatchNum++;
            }
        }

        System.out.println("真实结果集实体对数："+reals.size());
        System.out.println("实验结果集实体对数："+exps.size());
        System.out.println("正确匹配对数："+matchNum);
        System.out.println("错误匹配对数："+notmatchNum);
        System.out.println("未匹配实体对数："+(reals.size()-matchNum));
        System.out.println("正确匹配率："+matchNum/(double)reals.size()*100+"%");
    }



    public List<Result> compareEntity(List<Entity> entities1 , List<Entity> entities2){
        List<Result> results = new ArrayList<Result>();

        for(Entity entity1 : entities1){
            Map<String,String> e1_predicate_object = entity1.getPredicate_object();

            for(Entity entity2 : entities2){
                Map<String,String> e2_predicate_object = entity2.getPredicate_object();

                boolean flag = true;
                int cnt=0;
                if(e1_predicate_object.size() == e2_predicate_object.size()){
                    for(Map.Entry<String,String> entry1 : e1_predicate_object.entrySet()){

                        if(!entry1.getValue().equals(e2_predicate_object.get(entry1.getKey()))){
                            flag = false;
                            cnt++;
                        }
                    }
                }else{
                    flag = false;
                    cnt=10;
                }

                // 参数控制： cnt表示不相同属性的个数
                if(cnt<=1){
                    results.add(new Result(entity1.getSubject(),entity2.getSubject(),"=",
                            "http://www.w3.org/2001/XMLSchema#float","1.0"));
                    break;
                }
            }
        }
        return results;
    }

}
