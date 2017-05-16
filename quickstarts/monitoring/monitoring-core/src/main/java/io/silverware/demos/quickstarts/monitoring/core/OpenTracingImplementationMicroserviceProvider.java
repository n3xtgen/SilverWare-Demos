package io.silverware.demos.quickstarts.monitoring.core;

import brave.Tracer;
import brave.opentracing.BraveTracer;
import io.opentracing.util.GlobalTracer;
import io.silverware.microservices.Context;
import io.silverware.microservices.providers.MicroserviceProvider;
import io.silverware.microservices.silver.TracingSilverService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * @author Jaroslav Dufek (email@n3xtgen.net)
 */
public class OpenTracingImplementationMicroserviceProvider implements MicroserviceProvider {

   private static final Logger log = LogManager.getLogger(OpenTracingImplementationMicroserviceProvider.class);

   /**
    * Initialize the OpenTracing implementation to SilverWare context.
    *
    * @param context SilverWare context
    */
   @Override
   public void initialize(final Context context) {
      OkHttpSender sender = OkHttpSender.create("http://127.0.0.1:9411/api/v1/spans");
      AsyncReporter reporter = AsyncReporter.builder(sender).build();

      Tracer braveTracer = Tracer.newBuilder()
            .localServiceName("NumbersApp")
            .reporter(reporter)
            .build();

      BraveTracer tracer = BraveTracer.wrap(braveTracer);

      GlobalTracer.register(tracer);
      log.info(tracer + " OpenTracer implementation set to GlobalTracer.");
   }

   /**
    * No need to run.
    */
   @Override
   public void run() {

   }
}
