package com.zqh;


import com.zqh.FPGrowth.example.FP_Item;
import com.zqh.FPGrowth.example.RunFPGrowth;
import com.zqh.infogain.entity.Entity;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by sl on 2017/3/1.
 */
public class Test {


    public static void main(String args[]) throws Exception {

        String filePrefix = "D:/data/PR/person1/";

//        FileToEntity fileToEntity1 = new FileToEntity(filePrefix + "person11.rdf");
//        FileToEntity fileToEntity2 = new FileToEntity(filePrefix + "person12.rdf");

        System.out.println("fileToEntity start ...");

//        List<Entity> entities1 = fileToEntity1.extractEntity();
//        List<Entity> entities2 = fileToEntity2.extractEntity();

        System.out.println("fileToEntity done!");


        System.out.println("FPGrowth start ...");
//        RunFPGrowth runFPGrowth = new RunFPGrowth(entities1, entities2);
//        List<FP_Item> FP_items = runFPGrowth.run();
        System.out.println("FPGrowth done!");

        System.out.println("XMLHelper start ...");
        XMLHelper xmlHelper = new XMLHelper(filePrefix + "dataset11_dataset12_goldstandard_person.xml");
        List<Result> reals = xmlHelper.run();
        System.out.println("XMLHelper done!");

        System.out.println("Compare start ...");
//        Compare compare = new Compare(entities1, entities2, reals, FP_items);
//        compare.compareResult();
        System.out.println("Compare done!");

    }
}