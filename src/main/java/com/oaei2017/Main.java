package com.oaei2017;

import com.oaei2017.dtw.DTW;
import com.oaei2017.entity.Result;
import com.oaei2017.entity.Trace;
import com.oaei2017.reader.FileReader;
import com.oaei2017.reader.GoldReader;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.toHexString;

/**
 * Created by Administrator on 2017/7/11.
 */
public class Main {

    public static void main(String args[]){
        long startTime=System.currentTimeMillis();   //获取开始时间

        FileReader fileReader1 = new FileReader("C:/Users/Administrator/Desktop/OAEI2017/data/FORTH_sandbox/Linking/Abox1.nt");
        List<Trace> traces1 = fileReader1.getTraces();
        FileReader fileReader2 = new FileReader("C:/Users/Administrator/Desktop/OAEI2017/data/FORTH_sandbox/Linking/Abox2.nt");
        List<Trace> traces2 = fileReader2.getTraces();

        System.out.println("\ntraces.size() : "+traces1.size());
        System.out.println("traces.size() : "+traces2.size()+"\n");

        DTW dtw = new DTW();
        List<Result> resultsExp = new ArrayList<Result>();
        double measure,measure_min;
        int cnt=0;
        for (Trace trace1 : traces1){
//            if(!trace1.getTraceName().equals("http://www.tomtom.com/trace-data/0000001865.ttl#trace"))
//                continue;
            System.out.println("cnt: "+cnt++);
//            System.out.println("trace1.getPoints().size() : "+trace1.getPoints().size());
            String traceName2="";
            measure_min = POSITIVE_INFINITY;
            int cnt2=1;
            for(Trace trace2 : traces2){
//                System.out.println(cnt2++);
                measure = dtw.getSimilarity(trace1,trace2,measure_min);
                if(measure<measure_min) {
                    measure_min = measure;
                    traceName2 = trace2.getTraceName();
                }
//                System.out.println("trace2.getPoints().size() : "+trace2.getPoints().size());

//                System.out.println(trace2.getTraceName());
//                System.out.println(measure+"\n");
            }
            resultsExp.add(new Result(trace1.getTraceName(),traceName2));
            System.out.println(trace1.getTraceName());
            System.out.println(traceName2);
            System.out.println("measure = "+measure_min+"\n");
        }

        GoldReader goldReader = new GoldReader("C:/Users/Administrator/Desktop/OAEI2017/data/FORTH_sandbox/Linking/refalign.rdf");
        List<Result> resultsGold = goldReader.getResults();

        //结果比较
        int a=0,flag;
        for(Result resultExp : resultsExp){
            flag=0;
            for(Result resultGold : resultsGold){
                if(resultExp.equals(resultGold)) {
                    a++;
                    flag=1;
                }
            }
            if(flag==0) {
                System.out.println("未匹配：");
                System.out.println(resultExp.getTraceName1());
                System.out.println(resultExp.getTraceName2()+"\n");
            }
        }
        int b = resultsExp.size()-a;
        int c = resultsGold.size()-a;
        double p = (double)a/(a+b);
        double r = (double)a/(a+c);
        double f1 = 2*p*r/(p+r);
        System.out.println("--------------------------------");
        System.out.println("gold匹配： "+resultsGold.size());
        System.out.println("实验匹配："+resultsExp.size());
        System.out.println("正确匹配： "+a);
        System.out.println("准确率P： "+p*100+"%");
        System.out.println("召回率R： "+r*100+"%");
        System.out.println("F1： "+f1*100+"%");
        System.out.println("--------------------------------");

        long endTime = System.currentTimeMillis();   //获取结束时间
        double runTime = (endTime-startTime)/1000;
        System.out.println("运行时间： "+(int)(runTime/3600)+"h "+(int)((runTime%3600)/60)+"m "+runTime%60+"s");

    }
}
