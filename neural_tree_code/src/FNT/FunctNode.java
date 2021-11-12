package FNT;

import Randoms.*;
import java.io.*;
import java.util.*;

class FunctNode extends Node {

    int m_Arity;
    Vector m_Children;
    double m_A;
    double m_B;
    int m_actFun;
    ///FunctNode m_ParentNode;
    //JavaRand m_RNG;
    MersenneTwisterFast m_RNG;

    public FunctNode() {
    }

    public FunctNode(double weight, double a, double b, int actFun, int arity, FunctNode parentNode, MersenneTwisterFast random) {
        m_A = a;
        m_B = b;
        m_actFun = actFun;
        m_Arity = arity;
        m_Children = new Vector();
        m_ParentNode = parentNode;
        m_RNG = random;
        m_Weight = weight;
    }//end constructor

    @Override
    public double getOutput(double[] inputs) {
        double netn = 0;
        for (Object m_Children1 : m_Children) {
            Node n = (Node) m_Children1;
            netn += n.getWeight() * n.getOutput(inputs);
        }
        ActivationFunction actvaton = new ActivationFunction();
        return actvaton.getOutput(m_A, m_B, m_actFun, netn);
    }

    //public double getOutput(double[] inputs, int depth){	}
    public void generateChildren(int nInputs, int maxArity, int depth, int maxDepth, int m_funType, double mhMIN_Node, double mhMAX_Node, double mhMIN_Edge, double mhMAX_Edge) {
        double weight, a, b;
        int actFun = m_funType;
        if (depth < maxDepth) {
            for (int i = 0; i < m_Arity; i++) {
                weight = m_RNG.random(mhMIN_Edge, mhMAX_Edge);
                a = m_RNG.random(mhMIN_Node, mhMAX_Node);
                b = m_RNG.random(mhMIN_Node, mhMAX_Node);
                if (0 == actFun) {//0 indicates random selection
                    actFun = 1 + m_RNG.random(7);//random selection currently only 7 function are implemented
                }

                int minArity = 2;
                int range = nInputs + maxArity - minArity;
                int num = m_RNG.random(range);
                if (num < nInputs) //leaf node
                {
                    LeafNode n = new LeafNode(weight, num, this);
                    m_Children.add(n);
                    //System.out.print(" D"+depth+"LN:"+num);
                } else // func node
                {
                    int arity = num - nInputs + minArity;
                    //System.out.print(" D"+depth+"FN:"+arity);
                    FunctNode n = new FunctNode(weight, a, b, actFun, arity, this, m_RNG);
                    n.generateChildren(nInputs, maxArity, depth + 1, maxDepth, m_funType, mhMIN_Node, mhMAX_Node, mhMIN_Edge, mhMAX_Edge);
                    m_Children.add(n);
                }
            }
        }//
        else // if max depth only leafs
        {
            for (int i = 0; i < m_Arity; i++) {
                weight = m_RNG.random(mhMIN_Edge, mhMAX_Edge);
                a = m_RNG.random(mhMIN_Node, mhMAX_Node);
                b = m_RNG.random(mhMIN_Node, mhMAX_Node);
                int range = nInputs - 1;
                int num = m_RNG.random(range);
                LeafNode n = new LeafNode(weight, num, this);
                m_Children.add(n);
                //System.out.print(" D"+depth+"LN:"+i+"A"+num);				
            }
        }
    }//end generate Child

    public void readChildren(BufferedReader brProb) {
        try {
            double weight;
            double a;
            double b;
            int actFun;
            int num = 0;
            int arity = 0;
            String dataline;
            for (int i = 0; i < m_Arity; i++) {
                if ((dataline = brProb.readLine()) != null) {
                    if (!dataline.contains("f")) {
                        //read a child as leaf node
                        double[] d = new double[2];
                        String[] tokens = dataline.split(",");
                        for (int t = 0; t < tokens.length; t++) {
                            d[t] = Double.parseDouble(tokens[t]);
                            //System.out.println(tokens[t] + "==" + d[t]);
                        }

                        num = (int) d[0];
                        weight = d[1];
                        LeafNode n = new LeafNode(weight, num, this);
                        m_Children.add(n);
                    } else {
                        //read the function Node
                        double[] d = new double[4];
                        int j = 0;

                        /*Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(dataline);
                         while (m.find())
                         {
                         d[j] = Double.parseDouble(m.group(1));
                         //System.out.println(d[j]);
                         j++;
                         }*/
                        String[] tokens = dataline.split(",");
                        actFun = Integer.parseInt(tokens[1]);
                        for (int t = 2; t < tokens.length; t++) {
                            d[j] = Double.parseDouble(tokens[t]);
                            //System.out.println(tokens[t] + "==" + d[j]);
                            j++;
                        }
                        arity = (int) d[0];
                        weight = d[1];
                        a = d[2];
                        b = d[3];
                        FunctNode n = new FunctNode(weight, a, b, actFun, arity, this, m_RNG);
                        n.readChildren(brProb);
                        m_Children.add(n);
                    }
                }//if data is null do nothing
            }//end reading arity	
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error Read Child" + e);
        }
    }//end generate Child

    @Override
    FunctNode getParentNode() {
        //System.out.println("returnParrent:"+m_ParentNode);
        return m_ParentNode;
    }

    @Override
    public void inspect(FNT tree, int depth) {
        try {
            for (Object m_Children1 : m_Children) {
                Node n = (Node) m_Children1;
                if (n.isLeaf()) {
                    n.inspect(tree, depth + 1);
                    tree.addLeafChild((LeafNode) n);
                } else {
                    n.inspect(tree, depth + 1);
                    tree.addFunctChild((FunctNode) n);
                }
            }
        } catch (Exception e) {
            System.out.print("Error Inspection FuncNode" + e);
        }
    }//end Function Inspect

    @Override
    public boolean isLeaf() {
        return false;
    }

    public int getArity() {
        return m_Arity;
    }

    public void setA(double a) {
        m_A = a;
    }

    public void setB(double b) {
        m_B = b;
    }

    public double getA() {
        return m_A;
    }

    public double getB() {
        return m_B;
    }

    public void replace(FunctNode oldNode, FunctNode newNode) {// delete oldNode?
        try {
            //System.out.print(m_Children.size()+": "+m_Children.contains(oldNode)+": "+m_Children.indexOf(oldNode));
            if (m_Children.contains(oldNode)) {
                m_Children.removeElement(oldNode); //remove old
                m_Children.add(newNode);
                //System.out.println(" "+m_Children.size()+": "+m_Children.contains(oldNode));
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.print("Error Replaccing:" + e);
        }
    }

    public void removeAndReplace(FunctNode node, LeafNode newLeafNode) {
        try {
            //System.out.print(m_Children.size()+": "+m_Children.contains(node)+": "+m_Children.indexOf(node));
            if (m_Children.contains(node)) {
                m_Children.removeElement(node); //remove old
                m_Children.add(newLeafNode);
                //System.out.println(" "+m_Children.size()+": "+m_Children.contains(node));
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.print("Error RemoveAndReplace-" + e);
        }
    }

    public void removeAndGrow(LeafNode toRemove, int maxDepth, int nInputs, int maxArity, ArrayList treeParameters) {
        try {
        int m_funType = (int) treeParameters.get(3); // int  activation function type        
        double mhMIN_Node = (double) treeParameters.get(4);//double mh min node 
        double mhMAX_Node = (double) treeParameters.get(5);//double mh max naode
        double mhMIN_Edge = (double) treeParameters.get(6);//double mh min edge
        double mhMAX_Edge = (double) treeParameters.get(7);//double mh max edge
            //System.out.print(m_Children.size()+": "+m_Children.contains(toRemove)+": "+m_Children.indexOf(toRemove));
            if (m_Children.contains(toRemove)) {
                m_Children.removeElement(toRemove);
                double weight = m_RNG.random(mhMIN_Edge, mhMAX_Edge);
                double a = m_RNG.random(mhMIN_Node, mhMAX_Node);
                double b = m_RNG.random(mhMIN_Node, mhMAX_Node);
                int actFun = m_funType;
                if (0 == actFun) {
                    actFun = 1 + m_RNG.random(7);//random selection currently only four function are implemented
                }
                int minArity = 2;
                int range = maxArity - minArity;
                int arity = minArity + m_RNG.random(range);
                FunctNode newNode = new FunctNode(weight, a, b, actFun, arity, this, m_RNG);
                newNode.generateChildren(nInputs, maxArity, 0, maxDepth, m_funType, mhMIN_Node, mhMAX_Node, mhMIN_Edge, mhMAX_Edge);
                m_Children.add(newNode);
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.print("Error RemoveAndGraow-" + e);
        }
    }

    @Override
    public void print(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("-");
        }
        System.out.println(" +" + m_Arity);

        for (Object m_Children1 : m_Children) {
            Node node = (Node) m_Children1;
            //System.out.print(" "+node.m_Weight);
            node.print(depth + 1);
        }
    }//end print

    @Override
    public void printFile(int depth, PrintWriter file) {
        //for(int i = 0; i < depth; i++)
        {
            //file.print("");
        }
        file.println("f" + "," + m_actFun + "," + m_Arity + "," + m_Weight + "," + m_A + "," + m_B);
        for (Object m_Children1 : m_Children) {
            Node node = (Node) m_Children1;
            //System.out.print(" "+node.m_Weight);
            node.printFile(depth + 1, file);
        }
    }//end printFile

    // public void printFile(int depth, std::fstream* file);
    @Override
    Node copyNode(FunctNode parentNode) {
        FunctNode node = new FunctNode(m_Weight, m_A, m_B, m_actFun, m_Arity, parentNode, m_RNG);
        for (Object m_Children1 : m_Children) {
            node.m_Children.add(((Node) m_Children1).copyNode(node));
        }
        return node;
    }//end CopyNode

}//end function Node
