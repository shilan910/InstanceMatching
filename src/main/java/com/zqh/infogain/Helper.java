package com.zqh.infogain;

import com.zqh.infogain.entity.Entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017/4/24.
 */
public class Helper {
    public static void main(String args[]){
        Helper helper = new Helper();
//        helper.writeToFile("test");
    }
    public void writeToFile(String filename , Map<Entity,List<Entity>> data){

        Set<String> temp = new HashSet<String>();
        List<String> output = new ArrayList<String>();

        String s1="",s2="";
        for(Map.Entry entry : data.entrySet()){
            s1 = ((Entity)entry.getKey()).getSubject();
            List<Entity> entities = (List<Entity>) entry.getValue();
            if(!entities.isEmpty())
                for(Entity entity : entities){
                    if(entity!=null && entity.getSubject()!=null){
                        s2 = entity.getSubject();
                        if(!temp.contains(s1+" "+s2) && !temp.contains(s2+" "+s1)){
                            temp.add(s1+" "+s2);
                            temp.add(s2+" "+s1);
                            output.add(s1+" "+s2);
                        }
                    }
                }
        }

        filename = "./tempdata/"+filename+".txt";
        try{
            File file =new File(filename);
            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }
            //true = append file
            FileWriter fileWritter = new FileWriter(filename);
            for(String out : output){
                fileWritter.write(out+"\n");
            }
            fileWritter.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
