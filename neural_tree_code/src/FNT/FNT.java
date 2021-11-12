package FNT;

import Randoms.*;
import java.io.*;
import java.util.*;

public class FNT {

    //FNT variables
    FunctNode m_Root;
    double m_Fitness;//obj = 1
    int m_Size;   //obj = 2
    int m_Diversity;//obj = 3
    int m_Depth;//obj = 4

    int m_rank = -1;
    double m_dist = -1.0;

    Vector m_FunctChilds;
    Vector m_LeafChilds;

    boolean m_WeightsOnly = false;
    //JavaRand m_RNG;
    MersenneTwisterFast m_RNG;
    //MersenneTwisterFast m_RNG;
    public int m_InputsCount;
    public int m_MaxArity;

    /**
     *
     * @param random
     *
     */
    public FNT(MersenneTwisterFast random) {
        m_Root = null;
        m_FunctChilds = new Vector();
        m_LeafChilds = new Vector();
        m_Depth = 0;
        m_WeightsOnly = false;
        m_RNG = random;

        //System.out.println("FNT Constructor:Done");
    }//end Constructor FNT

    /**
     * Random construction of the trees using recursion
     *
     * @param inputsCount (Integer) indicated the total number of input features
     * @param treeParameters

     */
    public void randomConstruction(int inputsCount, ArrayList treeParameters){//int maxArity, int maxDepth, int m_funType, double nodeMin, double nodeMax, double edgeMin, double edgeMax) {
        int maxDepth = (int) treeParameters.get(1);// int depth of tree
        int maxArity = (int) treeParameters.get(2);//int arity of a tree
        int m_funType = (int) treeParameters.get(3); // int  activation function type        
        double mhMIN_Node = (double) treeParameters.get(4);//double mh min node 
        double mhMAX_Node = (double) treeParameters.get(5);//double mh max naode
        double mhMIN_Edge = (double) treeParameters.get(6);//double mh min edge
        double mhMAX_Edge = (double) treeParameters.get(7);//double mh max edge
        
        m_InputsCount = inputsCount;
        m_MaxArity = maxArity;
        
        double weight = m_RNG.random(mhMIN_Edge, mhMAX_Edge);
        double a = m_RNG.random(mhMIN_Node, mhMAX_Node);
        double b = m_RNG.random(mhMIN_Node, mhMAX_Node);
        int actFun = m_funType;
        if (0 == actFun) {
            actFun = 1 + m_RNG.random(7);//random selection currently only 7 function are implemented
        }

        //System.out.print(actFun);
        int minArity = 2;
        int range = maxArity - minArity;
        if (range == 0) {
            range = 1;
        }
        int arity = minArity + m_RNG.random(range);	//System.out.println(arity);
        m_Root = new FunctNode(weight, a, b, actFun, arity, m_Root, m_RNG);
        //System.out.println("Root:"+this+" has arity "+arity);
        m_Root.generateChildren(inputsCount, maxArity, 0, maxDepth, m_funType, mhMIN_Node, mhMAX_Node, mhMIN_Edge, mhMAX_Edge);
        inspectChilds();
        //System.out.println("FNT random Constructor:Done");
    }//end randomConstruction

    /**
     * Inspects he the created tree for retrieving its function and leaf child's
     * information
     */
    public void inspectChilds() {
        try {
            m_FunctChilds.clear();
            m_LeafChilds.clear();
            m_Depth = 0;
            m_Root.inspect(this, 0);
            setDiversity();
        } catch (Exception e) {
            System.out.print("Error Inspection FNT" + e);
        }
    }//end inspect FNT

    /**
     * Set "true" if wants to optimize the tree weights only
     *
     * @param weightsOnly true/false
     */
    public void setWeightsOnly(boolean weightsOnly) {
        m_WeightsOnly = weightsOnly;
    }

    public void setFitness(double fitness) {
        m_Fitness = fitness;
    }

    public double getFitness() {
        return m_Fitness;
    }

    public void setRank(int rank) {
        m_rank = rank;
    }

    public int getRank() {
        return m_rank;
    }

    public void setDistance(double dist) {
        m_dist = dist;
    }

    public double getDistance() {
        return m_dist;
    }

    public void setDiversity() {
        Vector div = new Vector();
        for (Object m_FunctChild : m_FunctChilds) {
            FunctNode cur = (FunctNode) m_FunctChild;
            div.add(cur.m_actFun);
        }//for
        int[] arr = new int[div.size()];
        for (int i = 0; i < div.size(); i++) {
            arr[i] = Integer.parseInt(div.get(i).toString());
        }//for i
        int diversity = countDistinctElements(arr);
        m_Diversity = -1 * diversity;
    }//fun

    public int countDistinctElements(int[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            boolean isDistinct = false;
            for (int j = 0; j < i; j++) {
                if (arr[i] == arr[j]) {
                    isDistinct = true;
                    break;
                }//if
            }//for
            if (!isDistinct) {
                count++;
                //System.out.print(arr[i]+" ");
            }//if
        }//for
        return count;
    }//fun

    public int getDiversity() {
        return m_Diversity;
    }

    public Vector getFunctionChild() {
        return m_FunctChilds;
    }

    public Vector getLeafChild() {
        return m_LeafChilds;
    }

    /**
     * Returns the node activation value
     *
     * @param inputs an array of input features
     * @param inputsCount total number of input features
     * @return activation value
     */
    public double getOutput(double[] inputs, int inputsCount) {
        return m_Root.getOutput(inputs);
    }

    /**
     * Adding function node in a vector of function nodes
     *
     * @param functNode function node of the tree
     */
    public void addFunctChild(FunctNode functNode) {
        //System.out.println("fun child"); 
        m_FunctChilds.add(functNode);
    }

    /**
     * Adding leaf node in a vector of leaf nodes
     *
     * @param leafNode leaf node of the tree
     */
    public void addLeafChild(LeafNode leafNode) {
        //System.out.println("leaf child");
        m_LeafChilds.add(leafNode);
    }

    /**
     * Returns height of the tree
     *
     * @return height of the tree
     */
    public int getDepth() {
        return m_Depth;
    }

    /**
     * Set height of the tree
     *
     * @param depth height of the tree
     */
    public void setDepth(int depth) {
        m_Depth = depth;
    }

    /**
     * Size of the tree in terms of total number of nodes
     *
     * @return total number of node
     */
    public int size() {
        return (m_FunctChilds.size() + m_LeafChilds.size());
    }

    public int getSize() {
        m_Size = (m_FunctChilds.size() + m_LeafChilds.size());
        return m_Size;
    }

    /**
     * Print tree for console
     */
    public void printTree() {
        m_Root.print(0);
    }

    /**
     * Print tree to a data/text file
     *
     * @param fileName name of the file tree to be printed
     */
    public void printTreeFile(String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            try (PrintWriter file = new PrintWriter(bw)) {
                m_Root.printFile(0, file);
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.print("Error PrintFile:" + e);
        }
    }

    /**
     * Read a saved tree model
     *
     * @param fileName the filename of the saved tree model
     */
    public void readSavedFNTmodel(String fileName) {
        //System.out.println("Model Reading " + fileName);
        try {
            FileReader fin = new FileReader(fileName);
            try (BufferedReader brProb = new BufferedReader(fin)) {
                String rootData;// = brProb.readLine();
                double weight = 0.0;
                double a = 0.0;
                double b = 0.0;
                int actFun = 0;
                int arity = 0;
                if ((rootData = brProb.readLine()) != null) {
                    if (rootData.contains("f")) {
                        double[] d = new double[4];
                        int i = 0;
                        String[] tokens = rootData.split(",");
                        actFun = Integer.parseInt(tokens[1]);
                        for (int t = 2; t < tokens.length; t++) {
                            d[i] = Double.parseDouble(tokens[t]);
                            //System.out.println(tokens[t] + "==" + d[i]);
                            i++;
                        }

                        arity = (int) d[0];
                        weight = d[1];
                        a = d[2];
                        b = d[3];
                    }
                }//if

                m_Root = new FunctNode(weight, a, b, actFun, arity, m_Root, m_RNG);
                //System.out.println("Root:"+this+" has arity "+arity);
                //m_Root.readChildren(brProb,0);
                m_Root.readChildren(brProb);
                inspectChilds();
                brProb.close();
                //System.out.println("FNT random Constructor:Done");
            } // = brProb.readLine();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Model Reading Error" + e);
        }
    }//end Model reading

    /**
     * Print function nodes only to console
     */
    public void printFunctNodes() {
        System.out.print("{");
        for (Object m_FunctChild : m_FunctChilds) {
            FunctNode cur = (FunctNode) m_FunctChild;
            System.out.print("+" + cur.getArity() + "(" + cur.getWeight() + "," + cur.getA() + "," + cur.getB() + ") , ");
        }
        System.out.print("}\n");
    }//end printFunction

    /**
     * Prints leaf nodes only to console
     */
    public void printLeafNodes() {
        System.out.print("{");
        for (Object m_LeafChild : m_LeafChilds) {
            LeafNode cur = (LeafNode) m_LeafChild;
            System.out.print(cur.getInputNumber() + "(" + cur.getWeight() + ") , ");
        }
        System.out.print("} \n");
    }//end printLeaf

    /**
     * Copy the current object of the tree to another object to preserve and
     * protect from other operations
     *
     * @return a new FNT with new object
     */
    public FNT copyTree() {
        FNT tree = new FNT(m_RNG);
        tree.m_MaxArity = m_MaxArity;
        tree.m_InputsCount = m_InputsCount;
        tree.m_Fitness = m_Fitness;
        tree.m_Root = (FunctNode) m_Root.copyNode(m_Root);
        tree.inspectChilds();
        return tree;
    }//end copyTree

    /**
     * Returns the tree count real values weights and activation function
     * parameters
     *
     * @return return the tree parameters count
     */
    public int getParametersCount() {
        if (!m_WeightsOnly) {
            int nWeights = 2; //root A + B
            nWeights += m_FunctChilds.size() * 3; // weight + A + B
            nWeights += m_LeafChilds.size();
            return nWeights;
        } else {
            int nWeights = 0;
            nWeights += m_FunctChilds.size(); // weight
            nWeights += m_LeafChilds.size();
            return nWeights;
        }
    }//end ParameterCount

    /**
     * Set the tree real values weights and activation function parameters
     *
     * @param parameters total number of parameters
     * @return return the tree parameters count
     */
    public double[] getParameters(int parameters) {
        double weights[] = new double[parameters];
        try {
            if (!m_WeightsOnly) {
                int idx = 0;
                weights[idx] = m_Root.getA();
                idx++;
                weights[idx] = m_Root.getB();

                for (Object m_FunctChild : m_FunctChilds) {
                    FunctNode cur = (FunctNode) m_FunctChild;
                    idx++;
                    //cur.setWeight(parameters[idx]/* *2 - 1*/);
                    weights[idx] = cur.getWeight();
                    idx++;
                    weights[idx] = cur.getA();
                    idx++;
                    weights[idx] = cur.getB();
                }

                for (Object m_LeafChild : m_LeafChilds) {
                    LeafNode cur = (LeafNode) m_LeafChild;
                    idx++;
                    weights[idx] = cur.getWeight();
                }
            } else {
                int idx = 0;
                for (Object m_FunctChild : m_FunctChilds) {
                    FunctNode cur = (FunctNode) m_FunctChild;
                    weights[idx] = cur.getWeight();
                    idx++;
                }

                for (Object m_LeafChild : m_LeafChilds) {
                    LeafNode cur = (LeafNode) m_LeafChild;
                    weights[idx] = cur.getWeight();
                    idx++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting paramter" + e);
        }
        return weights;
    }//end getParameters	

    /**
     * Set the tree real values weights and activation function parameters
     *
     * @param parameters total number of parameters
     * @param treeParameters tree parameters
     * @return return the tree parameters count
     */
    public double[][] getParametersRange(int parameters,  ArrayList treeParameters) {
        double range[][] = new double[parameters][2];
        double mhMIN_Node = (double) treeParameters.get(4);//double mh min node 
        double mhMAX_Node = (double) treeParameters.get(5);//double mh max naode
        double mhMIN_Edge = (double) treeParameters.get(6);//double mh min edge
        double mhMAX_Edge = (double) treeParameters.get(7);//double mh max edge
        try {
            if (!m_WeightsOnly) {
                int idx = 0;
                range[idx][0] = mhMIN_Node;//getA();
                range[idx][1] = mhMAX_Node;
                idx++;
                range[idx][0] = mhMIN_Node;//getB();
                range[idx][1] = mhMAX_Node;

                for (Object m_FunctChild : m_FunctChilds) {
                    FunctNode cur = (FunctNode) m_FunctChild;
                    idx++;
                    //cur.setWeight(parameters[idx]/* *2 - 1*/);
                    range[idx][0] = mhMIN_Edge;//getWeight();
                    range[idx][1] = mhMAX_Edge;
                    idx++;
                    range[idx][0] = mhMIN_Node;//getA();
                    range[idx][1] = mhMAX_Node;
                    idx++;
                    range[idx][0] = mhMIN_Node;//getB();
                    range[idx][1] = mhMAX_Node;
                }

                for (Object m_LeafChild : m_LeafChilds) {
                    LeafNode cur = (LeafNode) m_LeafChild;
                    idx++;
                    range[idx][0] = mhMIN_Edge;//getWeight();
                    range[idx][1] = mhMAX_Edge;
                }
            } else {
                int idx = 0;
                for (Object m_FunctChild : m_FunctChilds) {
                    FunctNode cur = (FunctNode) m_FunctChild;
                    range[idx][0] = mhMIN_Edge;//getWeight();
                    range[idx][1] = mhMAX_Edge;
                    idx++;
                }

                for (Object m_LeafChild : m_LeafChilds) {
                    LeafNode cur = (LeafNode) m_LeafChild;
                    range[idx][0] = mhMIN_Edge;//getWeight();
                    range[idx][1] = mhMAX_Edge;
                    idx++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting paramter" + e);
        }
        return range;
    }//end getParameters	

    /**
     * Setting the optimized parameters to the tree
     *
     * @param parameters the optimized parameter using meta-heuristic
     */
    public void setParameters(double[] parameters) {
        try {
            //System.out.print("Set Parameter"+m_FunctChilds.size());
            if (!m_WeightsOnly) {
                int idx = 0;
                m_Root.setA(parameters[idx]);
                idx++;
                m_Root.setB(parameters[idx]);

                for (Object m_FunctChild : m_FunctChilds) {
                    FunctNode cur = (FunctNode) m_FunctChild;
                    idx++;
                    //cur.setWeight(parameters[idx]/* *2 - 1*/);
                    cur.setWeight(parameters[idx]);
                    idx++;
                    cur.setA(parameters[idx]);
                    idx++;
                    cur.setB(parameters[idx]);
                }

                for (Object m_LeafChild : m_LeafChilds) {
                    LeafNode cur = (LeafNode) m_LeafChild;
                    idx++;
                    cur.setWeight(parameters[idx]);
                }
            } else {
                int idx = 0;

                for (Object m_FunctChild : m_FunctChilds) {
                    FunctNode cur = (FunctNode) m_FunctChild;
                    cur.setWeight(parameters[idx]);
                    idx++;
                }

                for (Object m_LeafChild : m_LeafChilds) {
                    LeafNode cur = (LeafNode) m_LeafChild;
                    cur.setWeight(parameters[idx]);
                    idx++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error Setting paramter" + e);
        }
    }//end set paramters    
}//end class FNT
