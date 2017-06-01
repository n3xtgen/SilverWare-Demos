/*
 * -----------------------------------------------------------------------\
 * Silverware
 *  
 * Copyright (C) 2010 - 2016 the original author or authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -----------------------------------------------------------------------/
 */
package io.silverware.demos.quickstarts.monitoring.worker;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.util.GlobalTracer;
import io.silverware.demos.quickstarts.monitoring.core.FibonacciWorker;
import io.silverware.microservices.annotations.Microservice;

import io.silverware.microservices.providers.metrics.utils.Metrics;
import io.silverware.microservices.providers.opentracing.utils.Tracing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Map;

/**
 * Default implementation of FibonacciWorker microservice.
 *
 * @author Jaroslav Dufek (email@n3xtgen.cz)
 */
@Microservice
public class DefaultFibonacciWorker implements FibonacciWorker {

   private static final Logger log = LogManager.getLogger(DefaultFibonacciWorker.class);

   private Timer responseTimer;

   private Histogram lengthHistogram;

   @PostConstruct
   public void onInit() {

      responseTimer = Metrics.timer("fibonacci.response.timer");

      lengthHistogram = Metrics.histogramWithReservoir("fibonacci.request.length", new ExponentiallyDecayingReservoir());

      log.info("Pi worker Service initialized");
   }

   /**
    * Own implementation for generating Fibonacci sequence.
    */
   @Override
   public String getFibonacciSequence(int numberCount, Map spanContextMap) {

      Span fibonacciRequestSpan = null;
      if (spanContextMap != null) {
         SpanContext spanContext = GlobalTracer.get().extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(spanContextMap));
         fibonacciRequestSpan = Tracing.createSpan("clusterFibonacciRequestServer", spanContext).setTag("span.kind", "server");
      }

      if (numberCount < 0) {
         throw new IllegalArgumentException("Count of numbers cannot be negative!");
      }

      log.info(Integer.toHexString(hashCode()) + " Was asked for Fibonacci sequence number with precision: " + numberCount);

      fibonacciRequestSpan.setTag("numberapp.fibonacci.count", numberCount);

      lengthHistogram.update(numberCount);
      final Timer.Context timeSpan = responseTimer.time();
      long startTime = System.currentTimeMillis();

      if (numberCount == 0) {
         return "";
      }

      BigInteger last = BigInteger.ZERO;
      BigInteger current = BigInteger.ONE;

      StringBuilder fiSequence = new StringBuilder(current.toString());

      for (int i = 1; i < numberCount; i++) {
         BigInteger next = last.add(current);
         fiSequence.append(" ").append(next);
         last = current;
         current = next;
      }

      timeSpan.stop();
      long duration = System.currentTimeMillis() - startTime;

      log.info(Integer.toHexString(hashCode()) + " Generated Fi sequence of " + numberCount + " numbers in: " + duration + "ms");

      if (fibonacciRequestSpan != null) {
         fibonacciRequestSpan.finish();
      }

      return fiSequence.toString();
   }
}
