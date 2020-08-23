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
import atedeschi.bmc.exercise_4.exceptions.ProcessStillRunningException;
import atedeschi.bmc.exercise_4.utils.Constants;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
	 * To run the program faster set the variable 'TIMEOUT_BETWEEN_THREADS' with a lesser integer value (BEAWARE: always positive)
	 * I set the value to 5 because on my machine it is the lowest value which the program do not overlap the logging
	 */
	private static final int TIMEOUT_BETWEEN_THREADS = 5;
	
	/** The synchronizedMap with the status of each TransactionProcessor */
	private static Map<Integer, Status> transactionStatus = Collections.synchronizedMap(new HashMap<>());

	/** The synchronizedList list of all results returned from TransactionProcessor threads */
	private static List<Double> resultList = Collections.synchronizedList(new LinkedList<>());
	
	/**
	 * Main method. Display the result and execution time.
	 * 
	 * @param args (not used)
	 * @throws Exception
	 */
	public static void main(String[] args) throws ProcessStillRunningException {
		new Main().doTransitions();
	}

	/**
	 * 
	 * @return the sum of each TransactionProcessor's result
	 * @throws ProcessStillRunningException
	 */
	public Double doTransitions() throws ProcessStillRunningException {
		long startTime = System.currentTimeMillis();
		
		//1) start all the TransactionProcessor threads
		processTransactions(this);
		
		//2) wait until each TransactionProcessor thread ends its work
		waitUntilSizeEquals(resultList, Constants.NUM_TRANSACTIONS);
		
		//3) retrieve the sum of all results of all TransactionProcessor threads
		Double resultSum = resultList.stream().collect(Collectors.summingDouble(Double::doubleValue));

		//4) print the summary of the result with the elapsed time
		logger.info("The result is: {} . " + "Elapsed time: {} seconds", resultSum,
				(System.currentTimeMillis() - startTime) / Constants.MILLIS_IN_SEC);

		//5.a) check if there are TransactionProcessor threads still in RUNNING state
		Integer key = transactionStatus.entrySet().stream()
				.filter(e -> e.getValue().equals(atedeschi.bmc.exercise_4.enums.Status.RUNNING)).map(Map.Entry::getKey)
				.findFirst().orElse(null);
		
		//5.b) if find at least one TransactionProcessor thread still in RUNNING state throws the custom exception
		// 		with the TransactionProcessor thread ID
		if (key != null) {
			throw new ProcessStillRunningException(key.toString(), "There is at least one process still in RUNNING");
		}
		
		return resultSum;
	}

	/**
	 * Method to wait until the size of the first argument is equal to the second argument 
	 * @param list
	 * @param numTransactions
	 */
	private void waitUntilSizeEquals(List<Double> list, final int numTransactions) {
		try {
			do {
				synchronized(this) {
					this.wait();
				}
				
				//Command added only for debug purpose
				if (logger.isDebugEnabled() && list.size() < numTransactions) {
					logger.debug("Only {} TransactionProcessor have ended their works", list.size());
				}
				
				//Wait until all the transaction had returned their result
			} while (list.size() < numTransactions);
		} catch (InterruptedException e) {
			logger.error("Main Thread interrupted.", e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Process all transactions
	 * @param main 
	 * 
	 * @return the output of all transactions
	 * @throws InterruptedException 
	 */
	public double processTransactions(Main main) {
		double result = 0.0;
		for (int i = 0; i < Constants.NUM_TRANSACTIONS; i++) {
			try {
				TransactionProcessor tp = new TransactionProcessor(i, transactionStatus, Constants.NUM_TRANSACTIONS, resultList, main);
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
