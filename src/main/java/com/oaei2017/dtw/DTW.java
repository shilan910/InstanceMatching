package com.oaei2017.dtw;

import com.oaei2017.entity.Point;
import com.oaei2017.entity.Trace;

import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;

/**
 * Created by Administrator on 2017/7/11.
 */
public class DTW {
    private List<Point> ps1,ps2;
    private Map<Integer,Double> temp;
    private Set<Integer> borders;
    private int len1;
    private int len2;

    /**
     * 基于边界优化
     * @param n
     * @param m
     */
    private void optimize(int n , int m){
        if(n>m){
            int i = n-m;
            for(int j=1 ; j<=m ; j++)
                borders.add(i*1000000+j);
        }else if(n<m){
            int j = m-n;
            for(int i=1 ; i<=n ; i++)
                borders.add(i*1000000+j);
        }
    }

    private boolean isInBorders(int i , int j){
        if(len1>len2){
            if(i==len1-len2 && j>=1 && j<=len2)
                return true;
        }else if(len1<len2){
            if(j==len2-len1 && i>=1 && i<=len1)
                return true;
        }else {
            return true;
        }
        return false;
    }

    public double getSimilarity(Trace trace1 , Trace trace2 , double measure_min){
        temp = new HashMap<Integer, Double>();
        borders = new HashSet<Integer>();
        ps1 = trace1.getPoints();
        ps2 = trace2.getPoints();
//        if(trace2.getTraceName().equals("http://www.hobbit.e1db98cc8-ed48-467e-884b-e7d9c3fc1d3e")){
//            System.out.println(ps1.size());
//            System.out.println("ps2.size() : "+ps2.size());
//        }

        len1 = ps1.size()-1;
        len2 = ps2.size()-1;

        optimize(len1,len2);

        int len_min = (len1<len2?len1:len2)-1;
        temp.put(0,0.0);
        for(int i=1 ; i<=len1 ; i++)
            temp.put(i*1000000,POSITIVE_INFINITY);
        for(int j=1 ; j<=len2 ; j++)
            temp.put(j,POSITIVE_INFINITY);

        double optimize_min = POSITIVE_INFINITY;
        for(int i=1 ; i<=len1 ; i++){
            for(int j=1 ; j<=len2 ; j++){
                Point p1 = ps1.get(i);
                Point p2 = ps2.get(j);
                double euclideanDistance = Math.sqrt(Math.pow(p1.getLongitude()-p2.getLongitude(),2)
                        +Math.pow(p1.getLatitude()-p2.getLatitude(),2));
                int key = i*1000000+j;
                double value = euclideanDistance+Math.min(temp.get((i-1)*1000000+j-1),
                        Math.min(temp.get(i*1000000+j-1),temp.get((i-1)*1000000+j)));

                if(borders.isEmpty()){
                    if(optimize_min>value)
                        optimize_min = value;
                    if(optimize_min>measure_min)
                        return POSITIVE_INFINITY;
                }else if(isInBorders(i,j)) {
                    borders.remove(new Integer(key));
                    if(optimize_min>value)
                        optimize_min = value;
                }
                temp.put(key,value);
            }
        }
        return temp.get(len1*1000000+len2);
    }




    private double DP(int i , int j){
        if(i==0 && j==0)
            return 0;
        if(i==0 || j==0)
            return POSITIVE_INFINITY;
        Point p1 = ps1.get(i);
        Point p2 = ps2.get(j);
        double euclideanDistance = Math.sqrt(Math.pow(p1.getLongitude()-p2.getLongitude(),2)
                +Math.pow(p1.getLatitude()-p2.getLatitude(),2));
        return euclideanDistance+Math.min(DP(i-1,j-1),Math.min(DP(i,j-1),DP(i-1,j)));
    }

}
