package com.kuma.cloud.lab.database;

/** Real JDBC and Spring transactions in dedicated PostgreSQL schemas, without an application context. */
public final class DatabaseLearningDemo {
    private DatabaseLearningDemo() {}

    public static void main(String[] args) throws Exception {
        String lesson = args.length == 0 ? "all" : args[0];
        System.setProperty("kuma.lab.database.lesson", lesson);
        switch (lesson) {
            case "crud" -> DatabaseOperations.crud();
            case "binding" -> DatabaseOperations.binding();
            case "batch" -> DatabaseOperations.batch();
            case "queries" -> DatabaseOperations.queries();
            case "constraints" -> DatabaseOperations.constraints();
            case "boundaries" -> TransactionExperiments.boundaries();
            case "transfer" -> TransactionExperiments.transfer();
            case "savepoint" -> TransactionExperiments.savepoint();
            case "isolation" -> TransactionExperiments.isolation();
            case "optimistic" -> TransactionExperiments.optimistic();
            case "locking" -> TransactionExperiments.locking();
            case "spring" -> SpringTransactionExperiments.run();
            case "operations" -> operations();
            case "transactions" -> transactions();
            case "all" -> { operations(); transactions(); }
            default -> throw new IllegalArgumentException("Choose all|operations|transactions|crud|binding|batch|queries|constraints|boundaries|transfer|savepoint|isolation|optimistic|locking|spring");
        }
        System.out.println("Database learning: " + lesson + " passed");
    }
    private static void operations() throws Exception {
        DatabaseOperations.crud(); DatabaseOperations.binding(); DatabaseOperations.batch();
        DatabaseOperations.queries(); DatabaseOperations.constraints();
    }
    private static void transactions() throws Exception {
        TransactionExperiments.boundaries(); TransactionExperiments.transfer(); TransactionExperiments.savepoint();
        TransactionExperiments.isolation(); TransactionExperiments.optimistic(); TransactionExperiments.locking();
        SpringTransactionExperiments.run();
    }
}
