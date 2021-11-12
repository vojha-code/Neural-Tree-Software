/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MHAlgorithms;

import DataReader.Pattern;
import FNT.GP;
import Function.Function;
import MISC.TrainingModuleFNT;
import Randoms.*;
import java.util.ArrayList;
import java.util.Vector;

public class GA {

    //RANDOM algo Parameters
    MersenneTwisterFast randGen1;

    public void setSeed(MersenneTwisterFast rand) {
        randGen1 = rand;
    }
    JavaRand randGen = new JavaRand();
    //FNT Parameters
    private double[] FNTweight; //used for FNT
    GP m_GP;
    private TrainingModuleFNT m_MH;
    private boolean m_isGP;
    private int m_Ensemble_Candidates;
    private String m_Ensemble_Diversity = "";
    private boolean isClassification = false;
    //FNT Ensemble parameters
    private int num_predictors;
    private double[][] predictor;
    private double[] target;
    private int pattern_Length;
    //NN Network Parameters
    private Pattern[] patTrain;
    //OPT Function fun; 
    private String FunName;
    private boolean isOptimization = false;
    //MH Parameters
    private int dimension;//dimension of solution vector
    private double low[]; //lower bound 
    private double high[]; // upper bound
    int population; //population of solution vectors //NP = population = The number of colony size (employed bees+onlooker bees)
    private int maxIter; //The number of cycles for foraging {a stopping criteria}
    private int num_Function_Evaluation = 0;
    //MH Priniting Set-up
    private int printStep;//number of steps
    private boolean isPrintSteps = false;//wether to print steps of MH
    private boolean isPrintFinal = false;//whteher to print final optimial

    // GA parameters
    private double gaBestFitness;
    private double[] gaBestIndividual;
    private double[] funFitness;

    private int ELITISM = 2;
    private double CROSSOVER_PROB = 0.8;
    private double MUTATION_PROB = 1.0 - CROSSOVER_PROB;
    private int TOURNAMENT_SIZE = 4;
    private String CROSSOVER_TYPE = "Simulated Binary Crossover";
    private String Mutation_TYPE;
    private String SELECTION_TYPE;

    //seting optimization
    public void setFunction(String FunName) {
        this.FunName = FunName;
    }

    public void setOptimization(boolean optimizationTask) {
        isOptimization = optimizationTask;
    }
    public void setTrainingPattern(Pattern[] patTrain) {
        this.patTrain = patTrain;
    }

    //setting MH parameters
    public void setMHparameters(ArrayList mhParam) {
        this.dimension = (int) mhParam.get(0) + 1;//dimension + 1 for the objective values;
        this.population = (int) mhParam.get(1);//population;        
        this.maxIter = (int) mhParam.get(2);//maxIter;
        this.printStep = (int) mhParam.get(3);// printSteps;
        isPrintSteps = (boolean) mhParam.get(4);//printStep;
        isPrintFinal = (boolean) mhParam.get(5);//printFinal;
        this.low = new double[dimension];
        this.high = new double[dimension];
        this.low = (double[]) mhParam.get(6);//low;
        this.high = (double[]) mhParam.get(7);//high;
        //seting arrays of PSO
        FNTweight = new double[dimension];
        gaBestIndividual = new double[this.dimension - 1];
    }

    public void setAlgo(ArrayList algorithSetUp) {
        ELITISM = (int) algorithSetUp.get(0);
        CROSSOVER_PROB = (double) algorithSetUp.get(1);
        CROSSOVER_TYPE = (String) algorithSetUp.get(2);
        MUTATION_PROB = (double) algorithSetUp.get(3);
        Mutation_TYPE = (String) algorithSetUp.get(4);
        SELECTION_TYPE = (String) algorithSetUp.get(5);
        TOURNAMENT_SIZE = (int) algorithSetUp.get(6);
    }

    public double getBestFitness() {
        return gaBestFitness;
    }

    public double[] getBestParmeter() {
        return gaBestIndividual;
    }

    //Step 1 All individuals are  initialized 
    double[][] initial(int pop, int dim) {
        double[][] matrix = new double[pop][dim];
        int i = 0, j = 0;
        for (i = 0; i < pop; i++) {
            for (j = 0; j < dim - 1; j++) {
                double r = ((high[j] - low[j]) * randGen.nextDouble() + low[j]);//((double)Math.random()*32767 / ((double)32767+(double)(1)));
                //double r = ((maxX - minX) * randGen.nextDouble() + minX);//((double)Math.random()*32767 / ((double)32767+(double)(1)));
                matrix[i][j] = r;//*(maxX-minX)+minX;
            }
            matrix[i][j] = fitness(matrix[i]);
            //System.out.println("GA Stsrts :"+fitness(matrix[i]));
        }
        return matrix;
    }

    //Step 1 Display population
    void display(double[][] matrix) {
        int N = matrix.length;
        int D = matrix[0].length;
        //System.out.println("Population " + N + " Dimension " + D);
        int i = 0, j = 0;
        for (i = 0; i < N; i++) {
            System.out.printf(" %4d : ", i);
            for (j = 0; j < D - 1; j++) {
                double val = matrix[i][j];
                if (val < 0) {
                    System.out.printf(" %.3f ", val);
                } else {
                    System.out.printf("  %.3f ", val);
                }
            }
            System.out.printf(" : %.3f \n", matrix[i][j]);
        }
    }//display

    //evalusting 
    public double fitness(double x[]) {
        num_Function_Evaluation++;
        double[] y = new double[x.length - 1];//becuase last column is objective fun value
        System.arraycopy(x, 0, y, 0, x.length - 1);
        double fitnessRet = 0.0;
        switch (FunName) {
            case "FNT":
                if (m_isGP) {
                    fitnessRet = m_GP.getMHfitness(y);
                } else {
                    fitnessRet = m_MH.getMHfitness(y);
                }
                break;
            case "RMSE":
                //System.out.println("Computing fitness:");
                if (!isClassification) {//RMSE
                    fitnessRet = Function.computeteRMSE(y, target, predictor, num_predictors, pattern_Length);
                } else {//Accuracy
                    fitnessRet = Function.computeteAccuracy(y, target, predictor, num_predictors, pattern_Length);
                }
                //System.out.println(" fitness:Computed");
                break;
            default:
                fitnessRet = Function.computeteFunction(y, FunName);
                break;
        }
        return fitnessRet;
    }

    //sorting population according to fitness
    public double[][] sortedgArray(double[][] matrix) {
        int N = matrix.length;
        int D = matrix[0].length;
        int objIndex = D - 1;
        try {//Sorting population
            double[] tmpVal = new double[dimension];
            boolean swapped = true;
            int j = 0;
            while (swapped) {
                swapped = false;
                j++;
                for (int i = 0; i < N - j; i++) {
                    if (matrix[i][objIndex] > matrix[i + 1][objIndex]) {
                        System.arraycopy(matrix[i], 0, tmpVal, 0, D);
                        System.arraycopy(matrix[i + 1], 0, matrix[i], 0, D);
                        System.arraycopy(tmpVal, 0, matrix[i + 1], 0, D);
                        swapped = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.print("\nError Sorting:" + e);
        }
        return matrix;
    }//end Sorting

    // Determine vector with smallest cost in current population
    private void updateBest(double[][] matrix) {
        int N = matrix.length;
        int D = matrix[0].length;
        int objIndex = D - 1;
        funFitness = new double[N];
        //System.out.println("Population " + N + " Dimension " + D);
        double thisFitness = matrix[0][objIndex];
        gaBestFitness = thisFitness;
        funFitness[0] = thisFitness;
        int bestIndex = 0;
        for (int i = 1; i < matrix.length; i++) {
            thisFitness = matrix[i][objIndex];
            funFitness[i] = thisFitness;
            if (thisFitness < gaBestFitness) {
                gaBestFitness = matrix[i][objIndex];
                bestIndex = i;
            }
        }
        System.arraycopy(matrix[bestIndex], 0, gaBestIndividual, 0, D - 1);
    }//find best

    public ArrayList optimize() {
        num_Function_Evaluation = 0;
        double[] stat = {0.0, 0.0};
        double[] storeBest = null;
        double[] storeMean = null;
        double[] storeStd = null;
        if (isOptimization) {
            storeBest = new double[101];
            storeMean = new double[101];
            storeStd = new double[101];
            //System.out.println("Initialization Done");
        }
        int storeIndex = 0;

        //Initialization
        double[][] gaMainPopulation = initial(population, dimension); // Step
//        display(gaMainPopulation);
//        System.out.println("Sorted population");
//        sortedgArray(gaMainPopulation);
//        display(gaMainPopulation);
        updateBest(gaMainPopulation);
        int iter = 0;
        for (iter = 0; iter < maxIter; iter++) {
            //printing steps
            if (isPrintSteps) {
                if (iter % printStep == 0) {
                    if (isOptimization) {
                        if (storeIndex < 101) {
                            storeBest[storeIndex] = gaBestFitness;
                            stat = computFitnessStat();
                            storeMean[storeIndex] = stat[0];
                            storeStd[storeIndex] = stat[1];
                            storeIndex++;
                        }
                    }
                    System.out.printf("   MH algo It: %5d Best: %.9f Mean: %.9f Std: %.9f \n", iter, gaBestFitness, stat[0], stat[1]);
                }//printsteps
            }//isPrintSteps
            gaMainPopulation = sortedgArray(gaMainPopulation);
            int pool = (int) (gaMainPopulation.length / 2);
            //System.out.println(" pool Size:" + pool);
            double[][] gaMetingPool = selection(gaMainPopulation, pool);
            double[][] gaChildPopulation = createNewPopulation(gaMetingPool);
            double[][] intermediatePopulation = mergePopulations(gaMainPopulation, gaChildPopulation);
            updateBest(intermediatePopulation);
            gaMainPopulation = surviedPopulation(intermediatePopulation, gaMainPopulation.length);
            //Checking better structre (if any)
            if (gaBestFitness + 0.000001 < 0.000001) {
                //System.out.println("Best Fitness found");
                //break;
            }
        }//for all iterations
        //printing final step
        if (isPrintFinal) {
            if (isOptimization) {
                if (storeIndex < 101) {
                    storeBest[storeIndex] = gaBestFitness;
                    stat = computFitnessStat();
                    storeMean[storeIndex] = stat[0];
                    storeStd[storeIndex] = stat[1];
                    storeIndex++;
                }
            }
            System.out.printf("   MH algo It: %5d Best: %.9f Mean: %.9f Std: %.9f \nTotal Fun Evaluations: %d\n", iter, gaBestFitness, stat[0], stat[1], num_Function_Evaluation);
        }
        ArrayList array = new ArrayList();
        array.add(storeBest);
        array.add(storeMean);
        array.add(storeStd);
        return array;
    }//main loop finished

    private double[] computFitnessStat() {
        //System.out.println("Computation Starts");
        double[] stat = new double[2];
        double sum = 0.0;
        for (int i = 0; i < funFitness.length; i++) {
            sum = sum + funFitness[i];
        }
        double mean = (double) (sum / funFitness.length);
        //System.out.println("Computation Done mean:" + mean);
        double var = 0.0;
        for (int i = 0; i < funFitness.length; i++) {
            var = var + Math.pow((funFitness[i] - mean), 2);
        }
        var = Math.sqrt(var / funFitness.length);//it is standard diviation becuase we take sqrt
        //System.out.println("Computation Done std:"+var);
        stat[0] = mean;
        stat[1] = var;
        //System.out.println("Computation Done");
        return stat;
    }//retrun

    private double[][] createNewPopulation(double[][] x) {
        //copying current population to new population     
        int N = x.length;
        int D = x[0].length;
        int objIndex = D - 1;
        int p = 0;
        Vector child = new Vector();
        boolean was_crossover = false;
        boolean was_mutation = false;
        for (int i = 0; i < N; i++) {
            //Initialize the children to be null vector.
            double[] child_1 = new double[D];
            double[] child_2 = new double[D];
            double[] child_3 = new double[D];
            //With 90 % probability perform crossover
            if (randGen.nextDouble() < CROSSOVER_PROB) {
                //Select the first parent 
                int[] parents = randomIntVector(0, N);
                //Make sure both the parents are not the same
                int parent1 = parents[0];
                int parent2 = parents[1];
                int parent2Index = 1;
                while (true) {
                    if (parent2Index != N) {
                        if (!isequal(x, parent1, parents[parent2Index])) {
                            parent2 = parents[parent2Index];
                            break;
                        } else {
                            parent2Index++;
                        }
                    } else {
                        //System.out.println("Error is no unique candidate found");
                        break;
                    }
                }//while
                ////System.out.println(parent1+" - "+parent2);
                //Get the chromosome information for each randomnly selected
                //parents
                double[] parent_1 = new double[D];
                double[] parent_2 = new double[D];
                System.arraycopy(x[parent1], 0, parent_1, 0, D);
                System.arraycopy(x[parent2], 0, parent_2, 0, D);
                //Perform corssover for each decision variable in the chromosome.
                double[][] xOverKids = Crossover.crossover(parent_1, parent_2, low, high, randGen, CROSSOVER_TYPE);
                System.arraycopy(xOverKids[0], 0, child_1, 0, D);
                System.arraycopy(xOverKids[1], 0, child_2, 0, D);
                //System.out.println();
                //Evaluate the objective function for the offsprings and as before
                //concatenate the offspring chromosome with objective value.                
                child_1[objIndex] = fitness(child_1);
                child_2[objIndex] = fitness(child_2);
                was_crossover = true;
                was_mutation = false;
            } else {//With 10 % probability perform mutation. Mutation is based on polynomial mutation. 
                //int parent3 = (int) ((N - 0) * Math.random() + 0);
                int parent3 = randGen.nextInt(N);// ((N - 0) * Math.random() + 0);
                double[] parent_3 = new double[D];
                //Get the chromosome information for the randomnly selected parent.                
                System.arraycopy(x[parent3], 0, parent_3, 0, D);
                child_3 = Mutation.mutation(parent_3, low, high, randGen, MUTATION_PROB, Mutation_TYPE);
                //System.out.println();
                //Evaluate the objective function for the offspring and as before
                //concatenate the offspring chromosome with objective value.  
                child_3[objIndex] = fitness(child_3);
                was_crossover = false;
                was_mutation = true;
            }//if  
            if (was_crossover) {
                child.add(child_1);
                child.add(child_2);
                p = p + 2;
            } else if (was_mutation) {
                child.add(child_3);
                p = p + 1;
            }//if
        }//for i
        //System.out.println(p + " = " + child.size());
        double[][] offspring = new double[p][D];
        for (int i = 0; i < p; i++) {
            //System.out.print(i + " :");
            double[] childVal = (double[]) (Object) child.get(i);
            System.arraycopy(childVal, 0, offspring[i], 0, D); //System.out.printf(" %.2f", childVal[j]);
            //for j 
            //System.out.println();
        }//for i
        return offspring;//return offspring
    }//end create new population

    private boolean isequal(double[][] x, int parent1, int parent2) {
        int D = x[parent1].length;
        boolean equal = true;
        for (int i = 0; i < D; i++) {
            if (!(x[parent1][i] == x[parent2][i])) {
                equal = false;
            }//if
        }//for
        return equal;
    }//faun: isqual

    private double[][] selection(double[][] x, int pool) {
        int N = x.length;
        int D = x[0].length;
        double[][] matingPool = new double[pool][D];
        //Until the mating pool is filled, perform tournament selection
        for (int i = 0; i < pool; i++) {
            matingPool[i] = tournamentSelection(x);
        }
        return matingPool;
    }

    private double[] tournamentSelection(double[][] x) {
        int N = x.length;
        int D = x[0].length;
        int objIndex = D - 1;
        // declares an array of integers
        double[][] tournament;
        double[] winner;
        // allocates memory for 10 integers
        tournament = new double[TOURNAMENT_SIZE][D];
        winner = new double[D];
        int[] candidate = randomIntVector(0, N);
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            System.arraycopy(x[candidate[i]], 0, tournament[i], 0, D);
        }

        //minimum
        System.arraycopy(tournament[0], 0, winner, 0, D);
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            if (tournament[i][objIndex] < winner[objIndex]) {
                System.arraycopy(tournament[i], 0, winner, 0, dimension);
            }
        }
        return winner;
    }

    private double[][] mergePopulations(double[][] x, double[][] y) {
        int Nx = x.length;
        int Ny = y.length;
        int N = Nx + Ny;
        int D = x[0].length;
        double[][] mergePop = new double[N][D];
        for (int i = 0; i < N; i++) {
            if (i < Nx) {
                System.arraycopy(x[i], 0, mergePop[i], 0, D);
            } else {
                System.arraycopy(y[i - Nx], 0, mergePop[i], 0, D);
            }
        }
        return mergePop;
    }

    private double[][] surviedPopulation(double[][] x, int mainPopLen) {
        int N = x.length;
        int D = x[0].length;
        x = sortedgArray(x);
        double[][] survived = new double[mainPopLen][D];
        for (int i = 0; i < mainPopLen; i++) {
            System.arraycopy(x[i], 0, survived[i], 0, D);
        }
        return survived;
    }

    public int[] randomIntVector(int min, int max) {
        int a[] = new int[max];
        for (int i = 0; i < max; i++) {
            if (i == 0) {
                a[i] = randGen.random(min, max, false);
            } else {
                while (true) {
                    boolean flag = false;
                    int r = randGen.random(min, max, false);
                    for (int j = 0; j < i; j++) {
                        if (r == a[j]) {
                            flag = true;
                            break;
                        }//if	
                    }//for
                    if (!flag) {
                        a[i] = r;
                        break;
                    }//if   
                }//while				
            }//else 	
            //System.out.println(i + " - " + a[i] + " ");
            //a[i] = i;System.out.println(i+" - "+a[i]+" ");
        }//for
        return a;
    }//permutation

    public static void main(String artgs[]) {
    }
}
