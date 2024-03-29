/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MISC;

import MHAlgorithms.ABC;
import MHAlgorithms.GWO;
import MHAlgorithms.BFO;
import MHAlgorithms.DEvolution;
import MHAlgorithms.PSO;
import FNT.EvaluationFunction;
import FNT.FNT;
import FNT.GP;
//import FNT.PSO;
import Randoms.MersenneTwisterFast;
import java.util.ArrayList;
//import java.util.concurrent.*; used for ExecutorService

/**
 *
 * @author Varun
 */
public class TrainingModuleFNT {

    //Hints: for parrallel processing of the task
    /*class WorkerThread implements Runnable {

     private int workerNumber;
     A aObj;

     WorkerThread(int number, A obj) {
     workerNumber = number;
     aObj = obj;
     }

     public void run() {
     for (int i = 0; i <= 100; i += 20) {
     // Perform some work ...
     System.out.println("Worker number: " + workerNumber + ", percent complete: " + i + ", Return of " + aObj + " value" + aObj.method(i));
     try {
     Thread.sleep((int) (Math.random() * 1000));
     } catch (InterruptedException e) {
     }
     }
     }
     }

     class A {

     public int method(int i) {
     int j = 0;
     for (j = 0; j < 5; j++) {
     try {
     Thread.sleep((int) (Math.random() * 1000));
     } catch (InterruptedException e) {
     }
     }
     return j + i;
     }
     }*/
    private FNT[] treeModels;
    private EvaluationFunction evaluateModels;
    private int indexFit;
    private int m_OutputColumn;
    private int indexPointer;
    private boolean isClassification = false;
    private int typeCall = 0;
    private MersenneTwisterFast m_random;

    //for GP
    public double[][] MHAlgorithmsReturn(MersenneTwisterFast random, ArrayList mhParameters, ArrayList algpParameters, GP gp, double[] m_BestWeights, double[][] mhParameterRange, int m_Ensemble_Candidates, String m_Ensemble_Diversity) {
        m_random = random;
        typeCall = 0;// set GP return type
        //double returnRes[][];// = new double[m_Ensemble_Candidates][particleDimension];
        //System.out.print(" Start:");
        double[][] returnRes = optimizeParameters(gp, m_BestWeights, mhParameterRange, true, mhParameters, algpParameters, m_Ensemble_Candidates, m_Ensemble_Diversity);
        //System.out.println(" Finish:");
        return returnRes;
    }//train parameters

    //for Ensemble
    public double[] MHAlgorithmsReturn(MersenneTwisterFast random, int index, boolean classification, ArrayList mhParameters, ArrayList algpParameters, int m_Ensemble_Candidates, String m_Ensemble_Diversity) {
        m_random = random;
        typeCall = 1;// set Ensemble return type
        System.out.print(" Start:");
        indexPointer = index;
        isClassification = classification;
        double[][] mhParameterRange = (double[][]) mhParameters.get(4);
        double[][] returnRes = optimizeParameters(null, null, mhParameterRange, false, mhParameters, algpParameters, m_Ensemble_Candidates, m_Ensemble_Diversity);
        double weigths[] = new double[m_Ensemble_Candidates];
        System.arraycopy(returnRes[0], 0, weigths, 0, m_Ensemble_Candidates);
        System.out.println(" Finish:");
        return returnRes[0];
    }

    //for CV MH
    public FNT[] MHAlgorithmsReturn(MersenneTwisterFast random, FNT[] tree, EvaluationFunction ev, int Ot,ArrayList treeParameters,  ArrayList mhParameters, ArrayList algpParameters, int m_Ensemble_Candidates, String m_Ensemble_Diversity) {
        m_random = random;
        typeCall = 2;// set CV return type
        
        //Use to run task in parrallel
            /*int numWorkers = 5;
         int threadPoolSize = 5;
         ExecutorService tpes = Executors.newFixedThreadPool(threadPoolSize);

         WorkerThread[] workers = new WorkerThread[numWorkers];
         A[] a = new A[numWorkers];
         for (int i = 0; i < numWorkers; i++) {
         a[i] = new A();
         workers[i] = new WorkerThread(i, a[i]);
         System.out.println("worker :" + a[i] + " index:  " + i + " loaded");
         tpes.execute(workers[i]);
         }
         tpes.shutdown();*/
        treeModels = tree;
        evaluateModels = ev;
        m_OutputColumn = Ot;//output column to be trained

        boolean weightsOnly = (boolean) treeParameters.get(8); //int iteration      

        indexFit = 0;//initialize to checking the first model
        for (int j = 0; j < m_Ensemble_Candidates; j++) {//for all models
            indexFit = j;// check the ith model
            treeModels[j].setWeightsOnly(weightsOnly);
            int dimension = treeModels[j].getParametersCount();
            mhParameters.set(1, dimension);//seting dimension
            double[][] mhParameterRange = treeModels[j].getParametersRange(dimension, treeParameters);
            double[] m_BestWeights = treeModels[j].getParameters(dimension);
            //double[][] bound = treeModels[j].getBound(dimension);
            //mhParameters.set(1, dimension);//seting dimension
            double[][] bestPosition = optimizeParameters(this, m_BestWeights, mhParameterRange, false, mhParameters,  algpParameters, m_Ensemble_Candidates, m_Ensemble_Diversity);
            treeModels[j].setParameters(bestPosition[0]);
            treeModels[j].setFitness(treefitness(treeModels[j]));
            //System.out.print("Print fit "+treefitness(treeModels[j]));
        }//m_candidates
        return tree;
    }//return tree models

    private double[][] optimizeParameters(Object obj, double[] initialBest, double[][] mhParameterRange, boolean isGP, ArrayList mhGetParms, ArrayList algpParams, int candidates, String diversity) {
        String mhAlgo = (String) mhGetParms.get(0); //String algorithm
        int dimension = (int) mhGetParms.get(1);//int mh dimesion 
        int population = (int) mhGetParms.get(2); //int mh population 
        int iterations = (int) mhGetParms.get(3); //int iteration
        int m_PrintStepMH = (int) (iterations/10.0);
        double[][] returnRes = new double[candidates][];
        double[] low = new double[dimension];
        double[] high = new double[dimension];
        boolean stepPrint = false;
        boolean stepFinal = false;

        ArrayList mhParameters = new ArrayList();
        mhParameters.add(0, dimension);//0
        mhParameters.add(1, population);
        mhParameters.add(2, iterations);
        mhParameters.add(3, m_PrintStepMH);
        mhParameters.add(4, stepPrint);
        mhParameters.add(5, stepFinal);
//        System.out.println("Initial best");
//        for (int l = 0; l < dimension; l++) {
//            System.out.print(" "+initialBest[l]);
//        }// end for dimension
//        System.out.println("\nParameters");
        for (int l = 0; l < dimension; l++) {
            low[l] = mhParameterRange[l][0];
            high[l] = mhParameterRange[l][1];
//            System.out.print(low[l]+" ");
//            System.out.println(high[l]);
        }// end for dimension
        mhParameters.add(6, low);//1
        mhParameters.add(7, high);//2

        switch (mhAlgo) {
            case "ABC": {
                ABC mhobj = new ABC();
                mhobj.setSeed(m_random);
                mhobj.setMHparameters(mhParameters);
                mhobj.setAlgo(algpParams);
                if (typeCall != 1) {
                    mhobj.setFunction("FNT");
                    mhobj.setWeight(obj, initialBest, candidates, diversity, isGP);
                } else {//for ensemble only
                    mhobj.setFunction("RMSE");
                    mhobj.setEnsemble(candidates, indexPointer, isClassification);
                }
                mhobj.execute();//running algorithm
                if (typeCall != 1) {
                    returnRes = mhobj.getWeight();
                } else {//for ensemble only
                    returnRes = mhobj.getWeightEnsemble();
                }
                break;
            }
            case "DE": {
                DEvolution mhobj = new DEvolution();
                mhobj.setSeed(m_random);
                mhobj.setMHparameters(mhParameters);
                mhobj.setAlgo(algpParams);
                if (typeCall != 1) {
                    mhobj.setFunction("FNT");
                    mhobj.setWeight(obj, initialBest, candidates, diversity, isGP);
                } else {
                    mhobj.setFunction("RMSE");
                    mhobj.setEnsemble(candidates,indexPointer,isClassification);
                }
                mhobj.execute();//running algorithm
                if (typeCall != 1) {
                    returnRes = mhobj.getWeight();
                } else {
                    returnRes = mhobj.getWeightEnsemble();
                }
                mhobj = null;
                break;
            }
            case "BFO": {
                BFO mhobj = new BFO();
                mhobj.setSeed(m_random);
                mhobj.setMHparameters(mhParameters);
                mhobj.setAlgo(algpParams);
                if (typeCall != 1) {
                    mhobj.setFunction("FNT");
                    mhobj.setWeight(obj, initialBest, candidates, diversity, isGP);
                } else {
                    mhobj.setFunction("RMSE");
                    mhobj.setEnsemble(candidates, indexPointer, isClassification);
                }
                mhobj.execute();//running algorithm
                if (typeCall != 1) {
                    returnRes = mhobj.getWeight();
                } else {
                    returnRes = mhobj.getWeightEnsemble();
                }
                mhobj = null;
                break;
            }
            case "GWO": {
                GWO mhobj = new GWO();
                mhobj.setSeed(m_random);
                mhobj.setMHparameters(mhParameters);
                if (typeCall != 1) {
                    mhobj.setFunction("FNT");
                    mhobj.setWeight(obj, initialBest, candidates, diversity, isGP);
                } else {
                    mhobj.setFunction("RMSE");
                    mhobj.setEnsemble(candidates,indexPointer,isClassification);
                }
                mhobj.execute();//running algorithm
                if (typeCall != 1) {
                    returnRes = mhobj.getWeight();
                } else {
                    returnRes = mhobj.getWeightEnsemble();
                }
                mhobj = null;
                break;
            }
            case "PSO": {
                PSO mhobj = new PSO();
                mhobj.setSeed(m_random);
                mhobj.setMHparameters(mhParameters);
                mhobj.setAlgo(algpParams);
                if (typeCall != 1) {
                    mhobj.setFunction("FNT");
                    mhobj.setWeight(obj, initialBest, candidates, diversity, isGP);
                } else {
                    mhobj.setFunction("RMSE");
                    mhobj.setEnsemble(candidates, indexPointer, isClassification);
                }
                mhobj.execute();//running algorithm
                if (typeCall != 1) {
                    returnRes = mhobj.getWeight();
                } else {
                    returnRes = mhobj.getWeightEnsemble();
                }
                break;
            }
            default: {
                //do nothing
                break;
            }
        }//switch
        return returnRes;
    }

    public double getMHfitness(double[] position) {
        treeModels[indexFit].setParameters(position);
        //fitnessEvaluation(m_BestWeightsTree);
        return evaluateModels.evaluateFitness(treeModels[indexFit], m_OutputColumn);//m_BestWeightsTree.getFitness();
    }

    public double treefitness(FNT tree) {
        return evaluateModels.evaluateFitness(tree, m_OutputColumn);//m_BestWeightsTree.getFitness();
    }

}
