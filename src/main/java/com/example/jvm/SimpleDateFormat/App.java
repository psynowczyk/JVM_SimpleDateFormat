package com.example.jvm.SimpleDateFormat;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
	
	static UncaughtExceptionHandler UncaughtException = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(t.getName()+": "+e);
        }
    };
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
	public static class unsafeTask extends Thread implements Runnable {
		public void run() {
			try {
        		String result = dateFormat.parse("07-04-2016").toString();
        		if (!result.equals("Thu Apr 07 00:00:00 CEST 2016")) System.out.println(result);
        	}
        	catch (ParseException e) { e.printStackTrace(); }
		}
	}
	
	public static class safeTask extends Thread implements Runnable {
		public void run() {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        		String result = dateFormat.parse("07-04-2016").toString();
        		if (!result.equals("Thu Apr 07 00:00:00 CEST 2016")) System.out.println(result);
        	}
        	catch (ParseException e) { e.printStackTrace(); }
		}
	}
	
	public static void unsafeUsage() {
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		System.out.println("Unsafe method errors:");
		unsafeTask uT = new unsafeTask();
    	for (int x = 0; x < 100; x++) executorService.execute(uT);
    	executorService.shutdown();
    	// wait 1 second for threads termination
    	try { executorService.awaitTermination(1, TimeUnit.SECONDS); }
    	catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public static void safeUsage() {
		final ExecutorService executorService = Executors.newFixedThreadPool(100);
		System.out.println("Safe method errors:");
		safeTask sT = new safeTask();
    	for (int x = 0; x < 100; x++) executorService.execute(sT);
    	executorService.shutdown();
	}
	
    public static void main( String[] args ) throws InterruptedException {
    	Thread.setDefaultUncaughtExceptionHandler(UncaughtException);
    	System.out.println("Expected date parsed: Thu Apr 07 00:00:00 CEST 2016");
    	unsafeUsage();
    	safeUsage();
    }
}
