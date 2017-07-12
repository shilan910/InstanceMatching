package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;
import com.zqh.infogain.entity.PropertyPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/17.
 */
public class InfoGain {

    private List<String> properties;
    private List<String> propertiesRaw;
    private List<Entity> entitiesAll;
    private List<String> FPs;
    private List<String> IFPs;
    private Map<String,List<String>> sameAs;
    private int peNum_total;
    private PositiveExample pe;
    private Similarity similarity;
    private double ig_threshold;

    public InfoGain(String filename1 , String filename2 , double ig_threshold){
        this.ig_threshold = ig_threshold;
        Query query = new Query(filename1,filename2);
        properties = query.getProperties();
        propertiesRaw = query.getPropertiesRaw();
        System.out.println("propertiesRaw.size() = "+propertiesRaw.size());
        for(String p : propertiesRaw)
            System.out.println(p);
        entitiesAll = query.extractEntity();
        System.out.println("entitiesAll.size() = "+entitiesAll.size());
        FPs = query.getFPs();
        System.out.println("FPs.size() = "+FPs.size());
        for(String fp : FPs)
            System.out.println("FP  :  "+fp);
        IFPs = query.getIFPs();
        System.out.println("IFPs.size() = "+IFPs.size());
        for(String ifp : IFPs)
            System.out.println("IFP  :  "+ifp);

        sameAs = query.getSameAs();
        System.out.println("sameAs.size() = "+sameAs.size());

        pe = new PositiveExample(entitiesAll,FPs,IFPs,sameAs);
        System.out.println("new PositiveExample ... done");
        peNum_total = pe.getPositiveExamplePairNum(entitiesAll);
        System.out.println("getPositiveExamplePairNum ... done");
        similarity = new Similarity(entitiesAll,0.8);

    }

    public static void main(String args[]){
        String filename1 = "D:\\data\\new\\persondata_en.nt";
        String filename2 = "D:\\data\\new\\people.rdf";
        InfoGain infoGain = new InfoGain(filename1,filename2,0.01);
        List<PropertyPair> propertyPairs = infoGain.generatePropertyPairs();
        for(PropertyPair propertyPair : propertyPairs){
            System.out.println(propertyPair.getProperty1());
            System.out.println(propertyPair.getProperty1());
            System.out.println(propertyPair.getIg()+"\n");
        }
        System.out.println("propertyPairs.size() = "+propertyPairs.size());
    }


    public List<PropertyPair> generatePropertyPairs(){
        List<PropertyPair> propertyPairs = new ArrayList<PropertyPair>();
        int size = propertiesRaw.size();
        System.out.println("properties.size() = "+size);
        String property1,property2;
        double H_D = getEntropy(peNum_total,entitiesAll.size());
        System.out.println("peNum_total = "+peNum_total);
        for(int i=0 ; i<size ; i++){
            System.out.println("i = "+i);
            property1 = propertiesRaw.get(i);
            for(int j=i+1 ; j<size ; j++){
                System.out.println("j = "+j);
                property2 = propertiesRaw.get(j);
                List<Entity> entitiesSim = similarity.getEntityBy2Property(property1,property2);

                int peNum_sim = pe.getPositiveExamplePairNum(entitiesSim);

                List<Entity> entitiesRest = entitiesAll;
                entitiesRest.removeAll(entitiesSim);
                int peNum_rest = pe.getPositiveExamplePairNum(entitiesRest);

                double H_Q = getEntropy(peNum_sim,entitiesSim.size());
                double H_R = getEntropy(peNum_rest,entitiesRest.size());
                double H_D_p1_p2 = (entitiesSim.size()*H_Q + entitiesRest.size()*H_R) / (double)entitiesAll.size();
                double ig = H_D - H_D_p1_p2;

                if(entitiesSim.size()>0) {
                    System.out.println("entitiesSim.size() = " + entitiesSim.size());
                    System.out.println("peNum_sim = "+peNum_sim);
                    System.out.println("H_D = " + H_D);
                    System.out.println("H_Q = " + H_Q);
                    System.out.println("H_R = " + H_R);
                    System.out.println("H_D_p1_p2 = " + H_D_p1_p2);
                }
                if(ig >= ig_threshold) {
                    propertyPairs.add(new PropertyPair(property1, property2, ig));
                }
            }
        }
        return propertyPairs;
    }

    private static double getEntropy(int peNum , int size){
        double sum = size*(size-1);
        double pe_Proportion = (double)peNum / sum;
        double ne_Proportion = (sum - peNum) / sum;
        double h = -1*pe_Proportion*Math.log(pe_Proportion)/Math.log(2)-ne_Proportion*Math.log(ne_Proportion)/Math.log(2);
        return h;
    }

}
