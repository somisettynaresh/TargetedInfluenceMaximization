package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.influence.maximization.models.IMTStrategy;
import org.apache.log4j.Logger;

/**
 * Created by Naresh on 1/23/2017.
 */
public class IMTInstanceByStrategy {
    final static Logger logger = Logger.getLogger(IMTInstanceByStrategy.class);

    public static IMWithTargetLabels getInstance(IMTStrategy strategy) {
        IMWithTargetLabels instance = null;
        switch (strategy) {
            case GREEDY_ESTIMATOR_AND_GREEDY_INFLUENTIAL:
                instance = new IMTGreedyEstimatorAndGreedyInfluential();
                logger.debug("Instantiating IMTStrategy : GREEDY_ESTIMATOR_AND_GREEDY_INFLUENTIAL");
                break;
            case GREEDY_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL:
                instance = new IMTGreedyEstimatorAndDegreeDiscount();
                logger.debug("Instantiating IMTStrategy : GREEDY_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL");
                break;
            case GREEDY_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL:
                instance = new IMTGreedyEstimatorAndRandomDAG();
                logger.debug("Instantiating IMTStrategy : GREEDY_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL");
                break;
            case GREEDY_ESTIMATOR_AND_CELF_INFLUENTIAL:
                instance = new IMTWithCELFGreedy();
                logger.debug("Instantiating IMTStrategy : GREEDY_ESTIMATOR_AND_CELF_INFLUENTIAL");
                break;
            case RANDOM_DAG_ESTIMATOR_AND_GREEDY_INFLUENTIAL:
                instance = new IMTRandomDAGEstimatorAndGreedyInfluential();
                logger.debug("Instantiating IMTStrategy : RANDOM_DAG_ESTIMATOR_AND_GREEDY_INFLUENTIAL");
                break;
            case RANDOM_DAG_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL:
                instance = new IMTRandomDAGEstimatorAndDegreeDiscount();
                logger.debug("Instantiating IMTStrategy : RANDOM_DAG_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL");
                break;
            case RANDOM_DAG_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL:
                instance = new IMTRandomDAGEstimatorAndRandomDAG();
                logger.debug("Instantiating IMTStrategy : RANDOM_DAG_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL");
                break;
            case RANDOM_DAG_ESTIMATOR_AND_CELF_INFLUENTIAL:
                instance = new IMTWithCELFGreedy();
                logger.debug("Instantiating IMTStrategy : RANDOM_DAG_ESTIMATOR_AND_CELF_INFLUENTIAL");
                break;

        }
        return instance;
    }
}
