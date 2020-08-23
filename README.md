# bmc_software_exercise_4
BMC Software - Exercise 4


To launch the program use the class 'atedeschi.bmc.exercise_4.Main', there is also a JUnit 'atedeschi.bmc.exercise_4.MainTest' class useful to check the outcome of the program.


## THE SOLUTION
I have transformed the class TransactionProcess into a Runnable class with Thread so the Main thread can start multiple instance of TransactionProcess without wait for the end of the work of each

After launch of all TransactionProcess instances the Main thread waits for the end of their works, meanwhile each instance TransactionProcess has a 'notifyAll()' command to awake the Main thread from its 'wait()'.

After that a TransactionProcess instance notify that its work is done the Main thread checks the global progression via the map transactionStatus, if the size of transactionStatus is equals to the total amount of transaction defined via 'atedeschi.bmc.exercise_4.utils.Constants.NUM_TRANSACTIONS' the Main thread can exit from the loop of wait/check status to end its flow and the program.
