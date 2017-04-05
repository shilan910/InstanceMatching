package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/4.
 */
public class IFPFinder {

    private List<Entity> entities;
    private List<String> IFPs;

    public IFPFinder(List<Entity> entities, List<String> IFPs) {
        this.entities = entities;
        this.IFPs = IFPs;
    }

    public List<Entity> getIFPEntity(){
        List<Entity> IFP_entities = new ArrayList<Entity>();
        boolean ifp[][] = new boolean[1000][1000];
        int size = entities.size();

        //TODO
        for(int i=0 ; i<size ; i++) {
            for (Map.Entry entry1 : entities.get(i).getPredicate_objects().entrySet()) {
//                if (entry1.getKey().toString().equals(property))
            }
            for (int j = i + 1; j < size; j++) {
                for (String property : IFPs) {


                }
            }
        }





        return IFP_entities;
    }

}
