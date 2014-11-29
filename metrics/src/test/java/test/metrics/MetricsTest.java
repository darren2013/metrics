package test.metrics;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;









import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class MetricsTest {

	private static MetricRegistry metricRegistry;
	
	
	@BeforeClass
	public static void init(){
		metricRegistry = new MetricRegistry();
		//startReport();
		
		startJmxReport();
	}
	
	private static void startJmxReport() {
		JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
		reporter.start();
	}

	@Test
	public void testMeter()throws Exception{
		
		Meter requests = metricRegistry.meter("requests");
		requests.mark();
		wait6Seconds();
	}
	
	
	@Test
	public void testGauage()throws Exception{
		QueueManager qm = new QueueManager(metricRegistry, "queueRealSize");
		qm.offer("hello");
		
		wait6Seconds();
	}
	
	@Test
	public void testCounter()throws Exception{
		Counter counter = metricRegistry.counter(MetricRegistry.name("monitoryJobSize", "pending-jobs"));
		
		counter.inc();
		
		wait6Seconds();
	}
	
	@Test
	public void testHistogram()throws Exception{
		Histogram histogram = metricRegistry.histogram(MetricRegistry.name("requestHandler", "responseSize"));
		histogram.update(100);
		wait6Seconds();
	}
	
	@Test
	public void testTimer()throws Exception{
		Timer timer = metricRegistry.timer("test");
		Timer.Context a;
	}
	
	
	private static  class QueueManager {
	    private final Queue<String> queue;

	    public QueueManager(MetricRegistry metrics, String name) {
	        this.queue = new LinkedBlockingQueue<String>();
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
	
	
	
	private  void wait6Seconds() {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private  static void startReport() {
		ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
									.convertRatesTo(TimeUnit.SECONDS)
									.convertDurationsTo(TimeUnit.MILLISECONDS)
									.build();
		reporter.start(2, TimeUnit.SECONDS);
		
	}
}
