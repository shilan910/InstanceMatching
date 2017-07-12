package com.zqh;


import com.zqh.FPGrowth.example.FP_Item;
import com.zqh.FPGrowth.example.RunFPGrowth;
import com.zqh.infogain.entity.Entity;

import java.io.*;
import java.util.List;

/**
 * Created by sl on 2017/3/1.
 */
public class Test {


    public static void main(String args[]) throws Exception {
        String fileName = "D:/data/new/freebase-datadump-quadruples.tsv";
        File file = new File(fileName);

        InputStream in = null;
        try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[1000];
            int byteread = 0;
            in = new FileInputStream(fileName);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }

    }
}