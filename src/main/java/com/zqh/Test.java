package com.zqh;

import com.sun.org.apache.regexp.internal.RE;

import java.util.List;

/**
 * Created by sl on 2017/3/1.
 */
public class Test {


    public static void main(String args[]) throws Exception{

        String prefix = "http://www.okkam.org/oaie/";
        FileToEntity fileToEntity1 = new FileToEntity(prefix,
                "/Users/sl/Desktop/zqh/PR/person1/person11.rdf");
        List<Entity> entities11 = fileToEntity1.extractEntity();

        FileToEntity fileToEntity2 = new FileToEntity(prefix,
                "/Users/sl/Desktop/zqh/PR/person1/person12.rdf");
        List<Entity> entities12 = fileToEntity2.extractEntity();


        Compare compare = new Compare();

        List<Result> exps = compare.compareEntity(entities11,entities12);

        XMLHelper xmlHelper = new XMLHelper("/Users/sl/Desktop/zqh/PR/person1/" +
                "dataset11_dataset12_goldstandard_person.xml");
        List<Result> reals = xmlHelper.run();

        compare.compareResult(reals,exps);

    }

}
