package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import com.zqh.util.Levenshtein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/4.
 */
public class Similarity {

    private List<Entity> entities;
    private double sim;

    public Similarity(List<Entity> entities , double sim) {
        this.entities = entities;
        this.sim = sim;
    }

    /**
     * 找出满足 property1 和 property2 相似度不低于sim的实体
     * @param property1
     * @param property2
     * @return
     */
    public List<Entity> getEntityPairNum(String property1 , String property2){
        List<Entity> entities_sim = new ArrayList<Entity>();
        int size = entities.size();
        boolean flag;
        for(int i=0 ; i<size ; i++){
            flag = true;
            Entity entity1 = entities.get(i);
            List<String> p1_objects = getProperty_Objects(entity1,property1);
            if(p1_objects == null)
                continue;
            for(int j=i+1 ; j<size ; j++){
                Entity entity2 = entities.get(j);
                List<String> p2_objects = getProperty_Objects(entity2,property2);
                if(p2_objects == null)
                    continue;
                double maxSimilarity=0 , similarity;
                for(String p1_o : p1_objects){
                    for(String p2_o : p2_objects){
                        similarity = Levenshtein.levenshtein(p1_o,p2_o);
                        if(similarity>maxSimilarity){
                            maxSimilarity = similarity;
                        }
                    }
                }
                if(maxSimilarity>=sim){
                    if(!entities_sim.contains(entity2))
                        entities_sim.add(entity2);
                    if(!flag) {
                        entities_sim.add(entity1);
                        flag = true;
                    }
                }
            }
        }
        return entities_sim;
    }

    private static List<String> getProperty_Objects(Entity entity , String property){
        for(Map.Entry entry : entity.getPredicate_objects().entrySet()){
            if(entry.getKey().toString().equals(property)){
                return (List<String>) entry.getValue();
            }
        }
        return null;
    }

}
