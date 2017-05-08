# TargetedInfluenceMaximization

The objective of influence maximization problem is to find a set of **highly influential nodes** that maximizes the spread of influence in a social network. 
Such a set of nodes is called **seed set**. **Targeted labeled influence maximization** problem is an extension that attempts to find a 
seed set that maximizes influence among certain labeled nodes. However, in certain application areas such as market and political
sciences, it is desirable to limit the spread of influence on certain set of nodes while maximizing the influence spread among different set of nodes. Motivated by this, in this work we formulate and study Constrained Targeted Influence
Maximization problem where a network has two types of nodes -- targets and non-targets. For a given **k** and **theta**, the objective is to find a k size seed set
which maximizes the influence over the targets and keeps the influence over the non-targets within the threshold **theta**. 

## Getting Started
 This project consists of implementations of various algorithms for influence maximization problem. 

### Prerequisites
 Java 1.8. 
 
 IDE IntelliJ/Eclipse

### Table Of Contents

* [Modules](#modules)
* [Usage](#usage)
  * [Creating Graph](#create-graph)
  * [Generating Labels](#generate-labels)
  * [Graph With Labels](#graph-labels)
  * [Independent Cascade Model](#icm)
 * [Algorithms for Influence Maximization problem](#algos-im)
    * [Greedy](#im-greedy)
    * [CELF Greedy](#im-celf)
    * [Degree Discount](#im-dd)
 * [Algorithms for CTIM problem](#algos-ctim)
    * [Baseline Greedy](#ctim-baseline-greedy)
    * [DAG Baseline Greedy](#ctim-dag-baseline)
    * [Two Phase Algorithm](#ctim-two-phase)
         * [Phase 1- Estimating Non Targets](#ctim-phase1)
              * [Estimating Non-Targets Using Simulation](#ctim-phase1-simulation)
              * [Estimating Non-Targets Using DAG](#ctim-phase1-dag)
              
 


 ### <a name="modules"> </a> Modules
 Our framework consists of four major modules.
 ### 1. Graph Generator Module: 
 This module is responsible for generating a graph from a file, generating labels for the nodes and 
 converting undirected graph to a directed graph.

### 2. Non-Target Estimator Module: 
This module is responsible for estimating the nontargets for every node in the graph (Phase 1). This module consists of two strategies
(implementations) to estimate the non-targets: 1) Using Simulation 2) Reachability Tests on random DAG's.

### 3. Max Influential Node Module: 
This module is responsible for finding the max influential node among the given set of nodes 
(crucial sub step in Phase 2). This module has three strategies : 1) Using Simulation 2) Reachability Tests on random DAG's 3) Degree
Discount Heuristic.

### 4. IMTree Module: 
This module is responsible for constructing the IMTree based on the strategy input and finding the seed set from 
constructed IMTree.This module has two variations for constructing the IMTree - one with pruning and other without pruning.
This module also consists of a utility to read/write IMTree from/to a file.

## <a name="usage"> </a> Usage

### <a name="create-graph"> </a> Creating Graph : 
Given a file (edges, vertices seperated by tab space) and propagation probability (0.0 - 1.0f), we can create a graph using the following

```
FileDataReader wikiVoteDataReader = new FileDataReader(filename, probability);
DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
```
We used datasets from [SNAP](http://snap.stanford.edu/data/index.html). 

### <a name="generate-labels"> </a> Generating Graph Labels :

Given a graph file and percentage (0.0f - 1.0f), we label the given percentage of vertices as **A** and other vertices as **B** at random.
```
GenerateGraphLabels generateGraphLabels = new GenerateGraphLabels();
generateGraphLabels.generateLabels("DBLP.txt",0.8f);
```
The generated file can be found in the main project directory with filename of the format **DBLP_0.8_labels.txt**

<a name="graph-labels"> </a> We can use the above generated label file and update the labels in the DirectedGraph using the following

```
FileDataReader wikiVoteDataReader = new FileDataReader(filename, probability);
DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
ReadLabelsFromFile readLabels = new ReadLabelsFromFile();
readLabels.read(graph, label_file);
```
### <a name="icm"> </a> Independent Cascade Model :

This is the implementation of diffusion model, Independent Cascade Model, proposed by Kempe. et al in the paper [Maximizing the Spread of Influence through a Social
Network](https://www.cs.cornell.edu/home/kleinber/kdd03-inf.pdf). Given a graph, seed set and number of monto carlo simulations we perform diffusion and 
outputs the activated nodes at the end of diffusion. The results are averaged among all the simulations. Our implementation supports caching, if you 
already have an activated set from previous experiments, you can pass the already activated set as the last paramter. If we pass an empty set, our algorithm
assumes no cache and do the diffusion completely.

```
Set<Integer> activeSet = IndependentCascadeModel.performDiffusion(graph,seedSet,10000, new HashSet<>());
```
## <a name="algos-im"> </a> Algorithms for Influence Maximization problem
This sections consists of various implementations of influence maximization algorithms.

### <a name="im-greedy"> </a> Greedy
This is the implemetation of Greedy algorithm for influence maximization problem described in the paper  [Maximizing the Spread of Influence through a Social
Network](https://www.cs.cornell.edu/home/kleinber/kdd03-inf.pdf). Given a graph, budget, target labels (make every label as target labels to ignore targets) and 
number of simulations, greedy algorithm outputs the seed set of the budget size. 

```
Greedy greedy = new Greedy();
Set<Integer> seedSet = greedy.findSeedSet(graph, budget, targetLabels, 10000);
```
### <a name="im-celf"> </a> CELF Greedy
This is the implemetation of CELF Greedy algorithm for influence maximization problem described in the paper  [Cost-effective Outbreak Detection in Networks](https://www.cs.cmu.edu/~jure/pubs/detect-kdd07.pdf). Given a graph, budget, target labels (make every label as target labels to ignore targets) and 
number of simulations, celf greedy algorithm outputs the seed set of the budget size. 

```
CELFGreedy celfGreedy = new CELFGreedy();
Set<Integer> seedSet = celfGreedy.findSeedSet(graph, budget, targetLabels, 10000);
```

### <a name="im-dd"> </a> Degree Discount
This is the implemetation of Degree Discount algorithm for influence maximization problem described in the paper  [Efficient Influence Maximization in Social Networks](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/06/kdd09_influence-1.pdf).
Given a graph, budget, target labels (make every label as target labels to ignore targets), degree discount algorithm outputs the seed set of the budget size. 

```
DegreeDiscount dd = new DegreeDiscount();
Set<Integer> seedSet = dd.findSeedSet(graph, budget, targetLabels);
```
## <a name="algos-ctim"> </a> Algorithms for CTIM problem :
This sections consists of various implementations of constrained targeted influence maximization algorithms.

### <a name="ctim-baseline-greedy"> </a> Baseline Greedy :
Given a graph, budget, nonTargetThreshold, target labels , non target labels and number of simulations, baseline greedy algorithm outputs the seed set.
```
NaiveGreedy greedy = new NaiveGreedy();
Set<Integer> seedSet = greedy.findSeedSet(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels, noOfSimulations);
```
### <a name="ctim-dag-baseline"> </a> DAG Baseline :
Given a graph, budget, nonTargetThreshold, target labels , non target labels and number of simulations, dag baseline algorithm outputs the seed set.
```
DAGBaselineGreedy dagBaseline = new DAGBaselineGreedy();
Set<Integer> seedSet = dagBaseline.findSeedSet(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels, noOfSimulations);
```
### <a name="ctim-two-phase"> </a> Two Phase Implementation for CTIM :
Simlulator is the main class for simulating the two phase implementation for the ctim problem. When you execute the Simulator.java , it takes the following as the input

```
Enter Graph File Name
ca-GrQc.txt
Enter the propagation probability
0.05
Enter percentage of A's to be in Graph
80
Enter budget of seed set
20
Enter non target threshold
10
Enter the NonTargetsEstimate filename
results\ca-GrQc-data.dat
Enter the Influence Maximization Strategy (1-6)
1
```
Based on the input of Influence Maximization Strategy, simulator runs the corresponding implementations of the two phase implementation.
The Simulator writes the constucted the Influence Maximization Tree in a file for which it prints the filename. For the two phase implementation, we can reuse the result of phase 1 (estimating non-targets) using the parameter NonTargetsEstimate filename in the simulator.

### <a name="ctim-phase1"> </a> Phase 1- Estimating Non Targets:
This is the implementation of Phase 1. We can generate the estimate file using the following strategies.

#### <a name="ctim-phase1-simulation"> </a> Estimating Non-Targets Using Simulation:

```
EstimateNonTargets egreedy = new EstimateNonTargetsUsingGreedy();
egreedy.estimate(graph, nonTargetLabels, 10000);

```

#### <a name="ctim-phase1-dag"> </a>Estimating Non-Targets Using DAG:
```
EstimateNonTargets edag = new EstimateNonTargetsUsingRandomDAG();
edag.estimate(graph, nonTargetLabels, 10000);
```
              
### <a name="ctim-phase-2"> </a> Phase 2 Constructing the IMTree :
The following are the various Influence Maximization Stratagies 
```
EstimateNonTargets egreedy = new EstimateNonTargetsUsingGreedy();
egreedy.estimate(graph, nonTargetLabels, 10000);

```
GREEDY_ESTIMATOR_AND_GREEDY_INFLUENTIAL(1),
GREEDY_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL(2),
GREEDY_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL(3),
GREEDY_ESTIMATOR_AND_CELF_INFLUENTIAL(4),
RANDOM_DAG_ESTIMATOR_AND_GREEDY_INFLUENTIAL(5),
RANDOM_DAG_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL(6),
RANDOM_DAG_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL(7),
RANDOM_DAG_ESTIMATOR_AND_CELF_INFLUENTIAL(8);
```




