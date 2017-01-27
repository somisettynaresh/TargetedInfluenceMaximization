package edu.iastate.research.influence.maximization.models;

/**
 * Created by Naresh on 1/23/2017.
 */
public enum IMTStrategy {
    GREEDY_ESTIMATOR_AND_GREEDY_INFLUENTIAL(1),
    GREEDY_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL(2),
    GREEDY_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL(3),
    GREEDY_ESTIMATOR_AND_CELF_INFLUENTIAL(4),
    RANDOM_DAG_ESTIMATOR_AND_GREEDY_INFLUENTIAL(5),
    RANDOM_DAG_ESTIMATOR_AND_DEGREE_DISCOUNT_INFLUENTIAL(6),
    RANDOM_DAG_ESTIMATOR_AND_RANDOM_DAG_INFLUENTIAL(7),
    RANDOM_DAG_ESTIMATOR_AND_CELF_INFLUENTIAL(8);

    private final int value;

    IMTStrategy(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

    public static IMTStrategy byValue(int strategy) {
        for (IMTStrategy imtStrategy : IMTStrategy.values()) {
            if (strategy == imtStrategy.getValue()) {
                return imtStrategy;
            }
        }
        return null;
    }
}
