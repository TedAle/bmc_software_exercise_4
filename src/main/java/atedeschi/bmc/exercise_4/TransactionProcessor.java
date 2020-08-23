package atedeschi.bmc.exercise_4;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atedeschi.bmc.exercise_4.enums.Status;
import atedeschi.bmc.exercise_4.utils.Constants;

public class TransactionProcessor implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

	private Thread t;

	private final int i;
	/** Reference to the synchronizedMap transactionsStatus */
	private Map<Integer, Status> transactionStatus;
	private final int numTransactions;
	private double result;
	/** Reference to the synchronizedList list of all results returned from TransactionProcessor threads */
	private List<Double> resultList;
	private Main main;

	public TransactionProcessor(final int i, Map<Integer, Status> transactionStatus, final int numTransactions,
			final List<Double> resultList, Main main) {
		this.i = i;
		this.transactionStatus = transactionStatus;
		this.numTransactions = numTransactions;
		this.result = 0.0;
		this.resultList = resultList;
		this.main = main;
	}

	/**
	 * Perform the complex transaction. This method must be called by the exercise
	 * and cannot be changed * @param input the input of the transaction
	 * 
	 * @return the output of the transaction
	 */
	protected final double doTransaction(double input) throws InterruptedException { // --- You cannot modify this
																						// method ---
		Thread.sleep(10000);
		return input * Constants.OK_TRANSACTION_SCORE;
	}

	/**
	 * Print the number of transactions. This method must be called by the exercise
	 * and cannot be changed
	 * 
	 * @param transactions an object describing the transaction status
	 */
	protected final void printTransactions(Map<?, Status> transactions) {
		// --- You cannot modify this method ---
		EnumMap<Status, Integer> counts = new EnumMap<>(Status.class);
		for (Status s : Status.values()) {
			counts.put(s, 0);
		}
		for (Status s : transactions.values()) {
			counts.put(s, counts.get(s) + 1);
		}
		logger.info(
				"- {} Ok transactions, {} Running transactions, "
						+ "{} Failed transactions. Completed percentage: {}%",
				counts.get(Status.OK), counts.get(Status.RUNNING), counts.get(Status.FAILURE),
				(counts.get(Status.OK) + counts.get(Status.FAILURE)) * 100.0 / numTransactions);
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		logger.info("Thread {} started",i);
		try {
			transactionStatus.put(i, Status.RUNNING);
			this.result += doTransaction(i);
			transactionStatus.put(i, Status.OK);
			printTransactions(transactionStatus);
		} catch (InterruptedException ex) {
			logger.warn("Transaction failed");
			transactionStatus.put(i, Status.FAILURE);
			Thread.currentThread().interrupt();
		}
		synchronized (main) {
			main.notifyAll();
		}
		resultList.add(Double.valueOf(result));
		logger.info("Thread {} ended after {}",i,(System.currentTimeMillis() - startTime));
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
}