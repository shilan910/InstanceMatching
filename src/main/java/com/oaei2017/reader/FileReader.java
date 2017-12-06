package com.oaei2017.reader;

import com.oaei2017.entity.Point;
import com.oaei2017.entity.Trace;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/9.
 */
public class FileReader {

    private static Model model;
    private Set<String> traceNames;
    private List<Trace> traces;
    private String filename;

    public FileReader(String filename){
        this.filename = filename;
        init();
    }

    public void init() {
        traceNames = new HashSet<String>();
        traces = new ArrayList<Trace>();
        model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(filename);
        if(filename.endsWith(".nt")){
            model.read(in,"","N3");
        }else if(filename.endsWith(".ttl")) {
            model.read(in,"","TTL");
        }else{
            model.read(in,null);
        }
        exactTrace();
    }

    private void exactTrace(){
        String queryString = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String subject = querySolution.get("subject").toString();
            String predicate = querySolution.get("predicate").toString();
            String object = querySolution.get("object").toString();

            if(subject.endsWith("trace") || (predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")&&
                    object.equals("http://www.tomtom.com/ontologies/traces#Trace")))
                traceNames.add(subject);
        }
        queryExecution.close();

        for(String traceName : traceNames){
            String queryString1 = "SELECT ?predicate ?object WHERE { <"+traceName+"> ?predicate ?object}";
            org.apache.jena.query.Query query1 = QueryFactory.create(queryString1);
            QueryExecution queryExecution1 = QueryExecutionFactory.create(query1,model);
            ResultSet resultSet1 = queryExecution1.execSelect();
            List<Point> points = new ArrayList<Point>();
            while(resultSet1.hasNext()){
                QuerySolution querySolution = resultSet1.next();
                String predicate = querySolution.get("predicate").toString();
                String object = querySolution.get("object").toString();
                if(predicate.equals("http://www.tomtom.com/ontologies/traces#hasPoint")){
                    points.add(exactPointByName(object));
                }
            }
            queryExecution1.close();
            traces.add(new Trace(traceName,points));
        }
        deleteZore();
        deleteOthers();
    }

    private Point exactPointByName(String pointName){
        String queryString = "SELECT ?predicate ?object WHERE { <"+pointName+"> ?predicate ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        Point point = new Point(pointName);
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String predicate = querySolution.get("predicate").toString();
            String object = querySolution.get("object").toString();
            if(predicate.equals("http://www.tomtom.com/ontologies/traces#long") || predicate.equals("http://www.tomtom.com/ontologies/traces#DMSlong")){
                point.setLongitude(turnLongOrLat(object));
            }else if(predicate.equals("http://www.tomtom.com/ontologies/traces#lat") || predicate.equals("http://www.tomtom.com/ontologies/traces#DMSlat")){
                point.setLatitude(turnLongOrLat(object));
            }else if(predicate.equals("http://www.tomtom.com/ontologies/traces#hasTimestamp")){
                point.setDateTime(turnTime(object));
            }
        }
        queryExecution.close();
        return point;
    }

    /**
     * 删除干扰点
     */
    private void deleteOthers(){
        for (Trace trace : traces){
            List<Point> points = trace.getPoints();
            double average_long=0,average_lat=0;
            for(Point point : points){
                average_lat += point.getLatitude();
                average_long += point.getLongitude();
            }
            average_long /= points.size();
            average_lat /= points.size();
            Iterator<Point> it = trace.getPoints().iterator();
            while(it.hasNext()){
                Point point = it.next();
                if(Math.sqrt(Math.pow(point.getLongitude()-average_long,2) + Math.pow(point.getLatitude()-average_lat,2))>5)
                    it.remove();
            }
        }

    }

    /**
     * 删除经纬度均为0的点
     */
    private void deleteZore(){
        for(Trace trace : traces){
            Iterator<Point> it = trace.getPoints().iterator();
            while(it.hasNext()){
                Point point = it.next();
                if(point.getLongitude()==0 && point.getLatitude()==0)
                    it.remove();
            }
        }
    }


    /**
     * 经度（正：东经　负：西经）纬度（正：北纬　负：南纬）
     * @param s
     * @return
     */
    private double turnLongOrLat(String s){
        double num = 0;
        if(s.contains("^")) {
            s = s.substring(0, s.indexOf("^"));
            num = Double.valueOf(s);
        }else{
            int du = s.indexOf("°");
            int fen = s.indexOf("'");
            int miao = s.indexOf("\\");
            num = Double.valueOf(s.substring(0,du)) + Double.valueOf(s.substring(du+1,fen))/60
                    + Double.valueOf(s.substring(fen+1,miao))/3600;
            if(s.endsWith("W") || s.endsWith("S"))
                num = -num;
        }
        return num;
    }

    public static long turnTime(String s){
        String dateRaw="";
        String[] ss = s.split(",");
        if(s.contains("^")){
            dateRaw = s.substring(0,s.indexOf('^'));
            dateRaw = dateRaw.replace("T"," ");
        }else if(s.contains("/")){
            int front = s.indexOf("/");
            int last = s.lastIndexOf("/");
            dateRaw = "20"+s.substring(last+1)+"-";
            dateRaw += s.substring(0,front)+"-"+s.substring(front+1,last);
            dateRaw += " 0:0:0";
        }else if(ss.length==2){
            dateRaw = ss[1].replace(" ","");
            return 0;
        }else if(ss.length==3){
            return 0;
        }else{
            return 0;
        }
        //Friday, November 19, 2010
        //Nov 19, 2010

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        try {
            date = formatter.parse(dateRaw);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public List<Trace> getTraces() {
        return traces;
    }

    private void reader(){
        String queryString = "SELECT ?predicate?subject?object WHERE { ?subject ?predicate ?object}";
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        ResultSet resultSet = queryExecution.execSelect();

        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            String predicate = querySolution.get("predicate").toString();
            String subject = querySolution.get("subject").toString();
            String object = querySolution.get("object").toString();
            System.out.println(subject+"\n"+predicate+"\n"+object+"\n");
        }
        queryExecution.close();
    }

    public static void main(String args[]) throws ParseException {

        FileReader fileReader = new FileReader("C:/Users/Administrator/Desktop/OAEI2017/data/FORTH_sandbox/Linking/Abox2.nt");
        List<Trace> traces = fileReader.getTraces();
        System.out.println("\ntraces.size() : "+traces.size());

        for(Trace trace : traces){
            if(trace.getTraceName().equals("http://www.hobbit.e1478d63b-41cd-4f6c-9f71-6533b06ce5ef")){
                System.out.println("trace.getPoints().size() : "+trace.getPoints().size());
                for(Point point : trace.getPoints()){
                    System.out.println(point.getLongitude());
                }
                System.out.println("\n\n\n\n");
                for(Point point : trace.getPoints()){
                    System.out.println(point.getLatitude());
                }
            }
        }
    }
}
