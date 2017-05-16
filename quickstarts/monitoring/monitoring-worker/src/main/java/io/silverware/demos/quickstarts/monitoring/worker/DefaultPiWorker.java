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

import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import io.silverware.demos.quickstarts.monitoring.core.PiWorker;
import io.silverware.microservices.annotations.Microservice;
import io.silverware.microservices.providers.metrics.utils.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Default implementation of PiWorker microservice.
 *
 * @author Jaroslav Dufek (email@n3xtgen.cz)
 */
@Microservice
public class DefaultPiWorker implements PiWorker {

   private static final Logger log = LogManager.getLogger(DefaultPiWorker.class);

   private Timer responseTimer;

   private Histogram precisionHistogram;

   @PostConstruct
   public void onInit() {

      responseTimer = Metrics.timer("pi.response.timer");

      precisionHistogram = Metrics.histogramWithReservoir("pi.request.precision", new ExponentiallyDecayingReservoir());

      log.info("Pi worker Service initialized");
   }

   /**
    * Gets string of Pi number using Pi class.
    */
   @Override
   public String getPiForPrecision(int precision) {
      log.info(Integer.toHexString(hashCode()) + " Was asked for Pi number with precision: " + precision);

      precisionHistogram.update(precision);
      final Timer.Context timeSpan = responseTimer.time();
      long startTime = System.currentTimeMillis();

      BigDecimal pi = Pi.computePi(precision);

      timeSpan.stop();
      long duration = System.currentTimeMillis() - startTime;

      log.info(Integer.toHexString(hashCode()) + " Calculated Pi on " + precision + " decimals in: " + duration + "ms");

      return pi.toString();
   }
}
