package com.zqh.FPGrowth.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.TreeNode;

import java.util.Set;

public class FPGrowth {

    private List<FP_Item> FP_items;

    private int minSupport;


    public FPGrowth(){
        FP_items = new ArrayList<FP_Item>();
    }

    public int getMinSupport() {
        return minSupport;
    }

    public void setMinSupport(int minSupport) {
        this.minSupport = minSupport;
    }

    public List<FP_Item> getFP_items() {
        return FP_items;
    }

    public void setFP_items(List<FP_Item> FP_items) {
        this.FP_items = FP_items;
    }

    //FPGrowth算法
    public void FPGrowth(List<List<String>> transRecords,List<String> postPattern){
        ArrayList<FP_TreeNode> headerTable=buildHeaderTable(transRecords);

        FP_TreeNode treeRoot=buildFPTree(transRecords, headerTable);

        if(treeRoot.getChildren()==null||treeRoot.getChildren().size()==0){
            return;
        }
        if(postPattern!=null && postPattern.size()>=1){
            for(FP_TreeNode header:headerTable){
//                System.out.print(header.getCount()+"\t"+header.getNodeName());
                List<String> items = new ArrayList<String>();
                items.add(header.getNodeName());
                items.addAll(postPattern);
                FP_items.add(new FP_Item(items,header.getCount()));
//                System.out.println(FP_items.size());
                for(String ele:postPattern){
//                    System.out.print("\t"+ele);
//                    items.add(ele);
                }
//                System.out.println();
            }
        }

        for(FP_TreeNode header:headerTable){
            List<String> newPostPattern =new LinkedList<String>();

            newPostPattern.add(header.getNodeName());
            if(postPattern!=null){
                newPostPattern.addAll(postPattern);
            }
            List<List<String>> newTransRecords =new LinkedList<List<String>>();
            FP_TreeNode backNode=header.getNextNode();
            while(backNode!=null){
                int counter =backNode.getCount();
                List<String> preNodes=new ArrayList<String>();
                FP_TreeNode parent=backNode;
                while((parent=parent.getParent()).getNodeName()!=null){
                    preNodes.add(parent.getNodeName());
                }

                while(counter-->0){
                    newTransRecords.add(preNodes);
                }
                backNode=backNode.getNextNode();
            }
            FPGrowth(newTransRecords, newPostPattern);

        }
    }

    /*
     * @transRecords:交易记录
     * @ return: 频繁1项集
     */
    public ArrayList<FP_TreeNode> buildHeaderTable(List<List<String>> transRecords){
        ArrayList<FP_TreeNode> F1=null;
        if(transRecords.size()>0){
            F1=new ArrayList<FP_TreeNode>();
            Map<String, FP_TreeNode> map=new HashMap<String, FP_TreeNode>();

            for(List<String> record:transRecords){
                for(String item:record){
                    if(map.keySet().contains(item)){
                        map.get(item).countCreament(1);
                    }
                    else{
                        FP_TreeNode node =new FP_TreeNode(item);
                        node.setCount(1);
                        map.put(item, node);
                    }
                }
            }
            //支持度大于minSupport的放入F1中
            Set<String> names=map.keySet();
            for(String name:names){
                FP_TreeNode tmpNode=map.get(name);
                if(tmpNode.getCount()>=minSupport){
                    F1.add(tmpNode);
                }
            }

            Collections.sort(F1);

            return F1;
        }

        return null;

    }
    //构建FP-Tree

    public FP_TreeNode buildFPTree(List<List<String>> transRecords,ArrayList<FP_TreeNode> F1){
        FP_TreeNode root=new FP_TreeNode();//创建树的根节点

        for(List<String> transRecord:transRecords){

            LinkedList<String>  record=sortByF1(transRecord, F1);//根据F1频繁项集对每条记录排序
            FP_TreeNode subTreeRoot=root;
            FP_TreeNode tmpRoot=null;

            if(root.getChildren()!=null){
                while(!record.isEmpty()&&(tmpRoot=subTreeRoot.findChild(record.peek()))!=null){
                    tmpRoot.countCreament(1);
                    subTreeRoot=tmpRoot;
                    record.poll();

                }
            }
            addNodes(subTreeRoot,record,F1);


        }
        return root;

    }


    private void addNodes(FP_TreeNode subTreeRoot, LinkedList<String> record, ArrayList<FP_TreeNode> F1) {
        // TODO Auto-generated method stub

        if(record.size()>0){
            while(record.size()>0){
                String item=record.poll();
                FP_TreeNode leafNode=new FP_TreeNode(item);
                leafNode.setCount(1);
                leafNode.setParent(subTreeRoot);
                subTreeRoot.addChild(leafNode);

                for(FP_TreeNode f1:F1){
                    if(f1.getNodeName().equals(item)){
                        while(f1.getNextNode()!=null){
                            f1=f1.getNextNode();
                        }
                        f1.setNextNode(leafNode);
                        break;
                    }
                }
                addNodes(leafNode, record, F1);

            }
        }
    }

    //把交易记录按照项的频繁程度降序排列
    public LinkedList<String> sortByF1(List<String> transRecord,ArrayList<FP_TreeNode> F1){
        Map<String,Integer> map=new HashMap<String, Integer>();
        for(String item:transRecord){
            for(int i=0;i<F1.size();i++){
                FP_TreeNode tmpNode=F1.get(i);
                if (tmpNode.getNodeName().equals(item)) {
                    map.put(item, i);
                }
            }
        }
        ArrayList<Entry<String, Integer>> al=new ArrayList<Entry<String, Integer>>(map.entrySet());
        Collections.sort(al,new Comparator<Map.Entry<String, Integer>>(){
//            @Override
            public int compare(Entry<String, Integer> et,Entry<String, Integer> et1){
                //降序排列
                return et.getValue()- et1.getValue();
            }
        });
        LinkedList<String> res =new LinkedList<String>();
        for(Entry<String, Integer> entry:al){
            res.add(entry.getKey());
        }

        return res;
    }
}