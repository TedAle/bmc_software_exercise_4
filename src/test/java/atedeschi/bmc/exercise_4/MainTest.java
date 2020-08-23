package atedeschi.bmc.exercise_4;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atedeschi.bmc.exercise_4.exceptions.ProcessStillRunningException;
import atedeschi.bmc.exercise_4.utils.Constants;

public class MainTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MainTest.class);
	
	private Double calculateExpectedResult (final int numOfTransactions, final Double okTransactionScore) {
		Double result = 0.0;
		for (int i = 0; i < numOfTransactions;i++) {
			result = result + (i*okTransactionScore);
		}
		
		return result;
	}

	@Test
	public void test() {
		Double expectedResult = calculateExpectedResult(Constants.NUM_TRANSACTIONS, Constants.OK_TRANSACTION_SCORE);
		
		logger.info("Expected result: {}", expectedResult);
		
		Double result = null;
		try {
			result = new atedeschi.bmc.exercise_4.Main().doTransitions();
		} catch (ProcessStillRunningException e) {
			logger.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
		Assert.assertTrue(Double.compare(result, expectedResult)==0);
		logger.info("Test succeeded");
	}

}
