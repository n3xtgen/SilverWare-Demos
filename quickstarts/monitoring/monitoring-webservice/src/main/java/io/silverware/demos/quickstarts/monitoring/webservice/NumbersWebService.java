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
package io.silverware.demos.quickstarts.monitoring.webservice;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import io.opentracing.contrib.jaxrs2.server.Traced;
import io.silverware.demos.quickstarts.monitoring.core.NumbersService;
import io.silverware.microservices.annotations.Microservice;
import io.silverware.microservices.annotations.MicroserviceReference;
import io.silverware.microservices.providers.metrics.utils.Metrics;
import io.silverware.microservices.providers.rest.annotation.ServiceConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Scanner;

/**
 * Web service with simple page for Numbers service.
 *
 * @author Jaroslav Dufek (email@n3xtgen.cz)
 */
@Path("")
@Microservice
public class NumbersWebService {

   private static final Logger log = LogManager.getLogger(NumbersWebService.class);

   @Inject
   @MicroserviceReference
   @ServiceConfiguration(endpoint = "http://localhost:8081/silverware/rest/numbers/")
   private NumbersService numbersService;

   private Counter webRequestsCount;

   private Timer webResponseTimer;

   @PostConstruct
   public void onInit() {

      Metrics.registerHealthCheck("indexFileAvailable", () -> {
         if (getClass().getClassLoader().getResource("index.html") != null) {
            return HealthCheck.Result.healthy("Index file is available");
         } else {
            return HealthCheck.Result.unhealthy("Index file is missing");
         }
      });

      webRequestsCount = Metrics.counter("request.count");

      webResponseTimer = Metrics.timer("response.timer");

      log.info("Web Service initialized");
   }

   @GET
   @Path("")
   @Produces(MediaType.TEXT_HTML)
   @Traced(operationName = "indexPageRequest")
   public Response interfacePage(@QueryParam("piPrecision") String piPrecision, @QueryParam("fibonacciCount") String fibonacciCount) {

      log.info("User requested webpage with params - piPrecision: " + piPrecision + " fibonacciCount: " + fibonacciCount);

      webRequestsCount.inc();
      final Timer.Context timeSpan = webResponseTimer.time();
      long startTime = System.currentTimeMillis();

      boolean showPi = StringUtils.isNotBlank(piPrecision);
      boolean showFibonacci = StringUtils.isNotBlank(fibonacciCount);

      // TODO Show how to ask for both results at the same time with object, now its two consecutive REST calls.
      String pi = showPi ? numbersService.piWithPrecision(Integer.parseInt(piPrecision)) : "";
      String fibonacci = showFibonacci ? numbersService.fibonacciSequence(Integer.parseInt(fibonacciCount)) : "";

      String html = "";
      ClassLoader classLoader = getClass().getClassLoader();
      // Hack with reading resource InputStream to String via start of file delimeter
      Scanner s = new Scanner(classLoader.getResourceAsStream("index.html"), Charset.defaultCharset().name()).useDelimiter("\\A");
      html = s.hasNext() ? s.next() : html;

      // Adds data to quickstart html file
      piPrecision = piPrecision != null ? piPrecision : "6000";
      fibonacciCount = fibonacciCount != null ? fibonacciCount : "3000";
      String page = MessageFormat.format(html, showPi ? "visible" : "hidden", piPrecision, pi, showFibonacci ? "visible" : "hidden", fibonacciCount, fibonacci);

      Response htmlResponse = Response.ok(page).build();

      long duration = System.currentTimeMillis() - startTime;
      timeSpan.stop();

      log.info("Serving webpage with params - "
            + (showPi ? "piPrecision: " + piPrecision : "")
            + (showFibonacci ? " fibonacciCount: " + fibonacciCount : "")
            + " after " + duration + "ms");

      return htmlResponse;
   }

}
