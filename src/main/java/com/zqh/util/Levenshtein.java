package com.zqh.util;

import org.apache.commons.lang3.builder.Diff;

/**
 * Created by sl on 2017/3/6.
 */

public class Levenshtein {

    public static void main(String[] args) {
        //要比较的两个字符串
        String str1 = "Ainsley";
        String str2 = "Charles_Lindbergh";
//        Levenshtein.levenshtein(str1,str2);

        String str3 = "2127531870";
        String str4 = "2127531871";
//        Levenshtein.levenshtein(str3,str4);

        String str5 = "abcdefhijk";
        String str6 = "abcdefhijl";
//        Levenshtein.levenshtein(str5,str6);

        System.out.println(Levenshtein.levenshtein(str1,str2));
//        levenshtein(str1,str2);
    }

    public static double levenshtein(String str1,String str2) {
        if(str1==null || str2==null || str1.equals("") || str2.equals("") || str1.equals(" ") || str2.equals(" ")){
            return 0;
        }else{
            str1 = extractString(str1);
            str2 = extractString(str2);
            double res = Math.abs(Double.valueOf(str1)-Double.valueOf(str2));
            if(res<=1)
                return 1-res;
            else return 0;
        }
        /*str1 = extractString(str1);
        str2 = extractString(str2);
        if(personName(str1,str2)==1)
            return 1;
        int len1 = str1.length();
        int len2 = str2.length();
        int[][] dif = new int[len1 + 1][len2 + 1];
        for (int a = 0; a <= len1; a++)
            dif[a][0] = a;
        for (int a = 0; a <= len2; a++)
            dif[0][a] = a;
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1))
                    temp = 0;
                else temp = 1;
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }
        double similarity =1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        return similarity;*/
    }

    private static String extractString(String s){
        int index;
        if(s.contains("^")){    //数字
            index = s.indexOf('^');
            s = s.substring(0,index);
        }else{
            if(s.contains("#")){
                index = s.indexOf('#');
            }else{
                index = s.lastIndexOf('/');
            }
            if(s.endsWith("@en"))
                s = s.replace("@en","");
            s = s.substring(index+1).replaceAll("[\\s+, ]","_").replaceAll("[(*)]","");
        }
        return s;
    }

    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    private static int personName(String s1 , String s2){
        String temp="";
        if(s1.length()<s2.length()){
            temp = s1;
            s1 = s2;
            s2 = temp;
        }

        String ss2 [] = s2.split("_");
        for(String sss2 : ss2){
            //System.out.println(sss2);
            if(!s1.contains(sss2) && !sss2.equals(" "))
                return 0;
        }
        return 1;
    }


}