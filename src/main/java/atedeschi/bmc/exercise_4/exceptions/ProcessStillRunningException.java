package atedeschi.bmc.exercise_4.exceptions;

public class ProcessStillRunningException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String processId;

	public ProcessStillRunningException(String processId, String message) {
		super(message);
		this.processId = processId;
	}

	public String getProcessId() {
		return processId;
	}

	@Override
	public String toString() {
		return "ProcessStillRunningException - '" + getMessage() + ", ex: processId=" + processId + "'";
	}
	
}
