package com.zqh;


import java.util.List;

/**
 * Created by sl on 2017/3/1.
 */
public class Test {


    public static void main(String args[]) throws Exception{

        String filrPrefix = "/Users/sl/Desktop/zqh/PR/person2/";
//        String filrPrefix = "/Users/sl/Desktop/zqh/PR/restaurants_2/";
//
        String prefix = "http://www.okkam.org/oaie/";
        FileToEntity fileToEntity1 = new FileToEntity(prefix, filrPrefix+"person21.rdf");
        List<Entity> entities11 = fileToEntity1.extractEntity();

        FileToEntity fileToEntity2 = new FileToEntity(prefix, filrPrefix+"person22.rdf");
        List<Entity> entities12 = fileToEntity2.extractEntity();

//        System.out.println(entities11.size());
//        System.out.println(entities12.size()+"\n");

        //restaurant1_restaurant2_goldstandard.xml
        //dataset21_dataset22_goldstandard_person.xml
//        dataset11_dataset12_goldstandard_person


        XMLHelper xmlHelper = new XMLHelper(filrPrefix+ "dataset21_dataset22_goldstandard_person.xml");

        List<Result> reals = xmlHelper.run();

        Compare compare = new Compare(entities11,entities12,reals);

        compare.compareResult();
//        compare.countWeight();

    }

}
