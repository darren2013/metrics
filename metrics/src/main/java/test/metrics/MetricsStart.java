package test.metrics;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class MetricsStart {
	private static  MetricRegistry metricRegistry = new MetricRegistry();
	
	public static void main(String[] args) {
		startReport();
		
		Meter requests = metricRegistry.meter("requests");
		requests.mark();
		
		QueueManager qm = new QueueManager(metricRegistry, "queueRealSize");
		qm.offer("hello");
		
		wait5Seconds();
		
		
		
	}

	private static void wait5Seconds() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void startReport() {
		ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
									.convertRatesTo(TimeUnit.SECONDS)
									.convertDurationsTo(TimeUnit.MILLISECONDS)
									.build();
		reporter.start(1, TimeUnit.SECONDS);
		
	}
	
	private static  class QueueManager {
	    private final Queue queue;

	    public QueueManager(MetricRegistry metrics, String name) {
	        this.queue = new LinkedBlockingQueue();
	        metrics.register(MetricRegistry.name(QueueManager.class, name, "size"),
	        				new Gauge<Integer>() {

								public Integer getValue() {
									return queue.size();
								}
							}
	        		);
	    }
	    
	    public void offer(String s){
	    	queue.offer(s);
	    }
	}
}
