package atedeschi.bmc.exercise_4;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atedeschi.bmc.exercise_4.enums.Status;
import atedeschi.bmc.exercise_4.utils.Constants;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
	 * To run the program faster set the variable 'TIMEOUT_BETWEEN_THREADS' with a lesser integer value (BEAWARE: always positive)
	 * I set the value to 5 because on my machine it is the lowest value which the program do not overlap the logging
	 */
	private static final int TIMEOUT_BETWEEN_THREADS = 5;
	
	/** The status of transactions */
	private static Map<Integer, Status> transactionStatus = Collections.synchronizedMap(new HashMap<>());

	/** The status of transactions */
	private static List<Double> resultList = Collections.synchronizedList(new LinkedList<>());
	
	
	public static void main(String[] args) throws ProcessStillRunningException {
		doTransitions();
	}

	/**
	 * Main method. Display the result and execution time.
	 * 
	 * @param args (not used)
	 * @throws Exception
	 */
	public static Double doTransitions() throws ProcessStillRunningException {
		long startTime = System.currentTimeMillis();
		processTransactions();
		do { 
			//Just wait until all the transaction had returned their result
		} while (resultList.size() < Constants.NUM_TRANSACTIONS);
		Double resultSum = resultList.stream().collect(Collectors.summingDouble(Double::doubleValue));

		logger.info("The result is: {} . " + "Elapsed time: {} seconds", resultSum,
				(System.currentTimeMillis() - startTime) / 1000.0);

		Integer key = transactionStatus.entrySet().stream()
				.filter(e -> e.getValue().equals(atedeschi.bmc.exercise_4.enums.Status.RUNNING)).map(Map.Entry::getKey)
				.findFirst().orElse(null);

		if (key != null) {
			throw new ProcessStillRunningException(key.toString(), "There is at least one process still in RUNNING");
		}
		return resultSum;
	}

	/**
	 * Process all transactions
	 * 
	 * @return the output of all transactions
	 * @throws InterruptedException 
	 */
	public static double processTransactions() {
		double result = 0.0;
		for (int i = 0; i < Constants.NUM_TRANSACTIONS; i++) {
			try {
				TransactionProcessor tp = new TransactionProcessor(i, transactionStatus, Constants.NUM_TRANSACTIONS, resultList);
				tp.start();
				Thread.sleep(TIMEOUT_BETWEEN_THREADS);
			}catch (InterruptedException e) {
				logger.error("Thread {} interrupted.",i , e);
				Thread.currentThread().interrupt();
			}
		}
		return result;
	}
}
