package io.silverware.demos.quickstarts.monitoring.restservice;

import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import io.silverware.demos.quickstarts.monitoring.core.FibonacciWorker;
import io.silverware.demos.quickstarts.monitoring.core.PiWorker;
import io.silverware.microservices.annotations.InvocationPolicy;
import io.silverware.microservices.annotations.Microservice;
import io.silverware.microservices.annotations.MicroserviceReference;
import io.silverware.microservices.providers.metrics.utils.Metrics;
import io.silverware.microservices.silver.services.lookup.RandomRobinLookupStrategy;
import io.silverware.microservices.silver.services.lookup.RoundRobinLookupStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Microservice for instrumenting cluster workers.
 *
 * @author Jaroslav Dufek (email@n3xtgen.net)
 */
@Microservice
public class WorkersClusterService {

   private static final Logger log = LogManager.getLogger(WorkersClusterService.class);

   /**
    * Handle to SilverWare managed "piWorker" microservice cloud instance.
    * Changing target instance with RoundRobin lookup strategy.
    */
   @Inject
   @MicroserviceReference
   @InvocationPolicy(lookupStrategy = RoundRobinLookupStrategy.class)
   private PiWorker piWorker;

   /**
    * Handle to SilverWare managed "fibonacciWorker" microservice cloud instance.
    * Changing target instance with RandomRobin lookup strategy.
    */
   @Inject
   @MicroserviceReference
   @InvocationPolicy(lookupStrategy = RandomRobinLookupStrategy.class)
   private FibonacciWorker fibonacciWorker;

   private Timer piResponseTimer;

   private Timer fibonacciResponseTimer;

   @PostConstruct
   public void onInit() {

      Metrics.registerHealthCheck("workersAvailable", () -> {

         String pi = piWorker.getPiForPrecision(3);
         String fibonacci = fibonacciWorker.getFibonacciSequence(3);

         if (StringUtils.isNotEmpty(pi) && StringUtils.isNotEmpty(fibonacci)) {
            return HealthCheck.Result.healthy("Both Pi and Fibonacci workers available");
         } else {
            return HealthCheck.Result.unhealthy("One of the workers is not available");
         }
      });

      piResponseTimer = Metrics.timer("worker.pi.timer");

      fibonacciResponseTimer = Metrics.timer("worker.fibonacci.timer");

      log.info("Workers Cluster Service initialized");
   }

   /**
    * Gets Pi string with given precision from one of the cluster workers.
    */
   public String getPiForPrecision(int precision) {
      log.info("Calling for Pi Worker with precision: " + precision);

      final Timer.Context timeSpan = piResponseTimer.time();

      String pi = piWorker.getPiForPrecision(precision);

      timeSpan.stop();

      return pi;
   }

   /**
    * Gets Fibonacci string with given count of numbers from one of the cluster workers.
    */
   public String getFibonacciSequence(int numberCount) {
      log.info("Calling for Fibonacci Worker with number count: " + numberCount);

      final Timer.Context timeSpan = fibonacciResponseTimer.time();

      String fibonacci = fibonacciWorker.getFibonacciSequence(numberCount);

      timeSpan.stop();

      return fibonacci;
   }
}
