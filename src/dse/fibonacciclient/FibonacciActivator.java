package dse.fibonacciclient;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

//import dse.fibonacciservice.service.FibonacciService;
import api.FibonacciService;

public class FibonacciActivator implements BundleActivator {

    ServiceReference<?> fibonacciServiceReference;
    public static BundleContext bc = null;    
    private FibonacciConsumerThread thread = null;
    FibonacciService fibonacciService;
    public static int sequencesize;
    public final static int defaultSequenceSize = 4;
    
    public void start(BundleContext context) throws Exception {
        System.out.println("Fibonacci Client-B!!");
        bc = context;
		if(FibonacciActivator.bc.getServiceReference(FibonacciService.class.getName()) != null){
			fibonacciServiceReference = context.getServiceReference(FibonacciService.class.getName());
			fibonacciService = (FibonacciService)context.getService(fibonacciServiceReference);
		}
              
        this.thread = new FibonacciConsumerThread();
        this.thread.start();
    }
    
    public void stop(BundleContext context) throws Exception {
        System.out.println("Client-B: Stopped");
        
        this.thread.stopThread();
        this.thread.join();
        context.ungetService(fibonacciServiceReference);
    }
}

class FibonacciConsumerThread extends Thread {

	private boolean running = true;
	private boolean serverStopped = false;
	public FibonacciConsumerThread() {}
	
	public void run() {
		while (running) {
			
		    ServiceReference<?> fibonacciServiceReference;
		    FibonacciService fibonacciService;
			if(FibonacciActivator.bc.getServiceReference(FibonacciService.class.getName()) != null){
				fibonacciServiceReference = FibonacciActivator.bc.getServiceReference(FibonacciService.class.getName());
				
			    if(FibonacciActivator.bc.getService(fibonacciServiceReference) != null){
			    	fibonacciService =(FibonacciService)FibonacciActivator.bc.getService(fibonacciServiceReference);

			    	// Make sure that the OSGi property hasn't changed
			        if(FibonacciActivator.bc.getProperty("dse.fibonacci.service.fibsize") != null){
			            FibonacciActivator.sequencesize = Integer.valueOf(FibonacciActivator.bc.getProperty("dse.fibonacci.service.fibsize"));        	
			        }
			        else{
			        	FibonacciActivator.sequencesize = FibonacciActivator.defaultSequenceSize;
			        }
			    	
			        // Output the number of fibonacci numbers given by the sequencesize field.
			    	for(int i=0; i < FibonacciActivator.sequencesize; ++i){
				    	System.out.println("Client-B: " + fibonacciService.getNextFib());
			    	}
			    }
			    
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.out.println("FibonacciClientBThread ERROR: " + e);
				}
			}
			else{
				// Only run once
				if(!serverStopped){
					System.out.println("Client-B: Fibonacci Server is paused");
					serverStopped = true;
				}
				
				// Wait 10 second before we re-check server availability.
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					System.out.println("FibonacciClientBThread ERROR: " + e);
				}
			}
		}
	}
	
	public void stopThread() {
		this.running = false;
	}
}