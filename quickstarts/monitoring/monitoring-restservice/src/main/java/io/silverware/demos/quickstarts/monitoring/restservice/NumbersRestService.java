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
package io.silverware.demos.quickstarts.monitoring.restservice;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import io.opentracing.contrib.jaxrs2.server.Traced;
import io.silverware.demos.quickstarts.monitoring.core.NumbersService;
import io.silverware.microservices.annotations.Microservice;
import io.silverware.microservices.annotations.MicroserviceReference;
import io.silverware.microservices.providers.metrics.utils.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST microservice for Numbers service.
 *
 * @author Jaroslav Dufek (email@n3xtgen.cz)
 */
@Path("numbers")
@Microservice
public class NumbersRestService {

   private static final Logger log = LogManager.getLogger(NumbersRestService.class);

   @Inject
   @MicroserviceReference
   WorkersClusterService workersClusterService;

   private Counter piRequestCount;

   private Counter fibonacciRequestCount;

   @PostConstruct
   public void onInit() {

      piRequestCount = Metrics.counter("request.pi.count");

      fibonacciRequestCount = Metrics.counter("request.fibonacci.count");

      log.info("REST Service initialized");
   }

   @GET
   @Path("pi")
   @Produces(MediaType.TEXT_PLAIN)
   @Traced(operationName = "piWithPrecision")
   public Response piWithPrecision(@QueryParam("precision") int precision) {

      log.info(this + " REST cluster service was asked for Pi with precision: " + precision);

      piRequestCount.inc();

      String pi = workersClusterService.getPiForPrecision(precision);

      return Response.ok(pi).build();
   }

   @GET
   @Path("fibonacci")
   @Produces(MediaType.TEXT_PLAIN)
   @Traced(operationName = "fibonacciSequence")
   public Response fibonacciSequence(@QueryParam("count") int numberCount) {

      log.info(this + " REST cluster service was asked for Fibonacci sequence with number count: " + numberCount);

      fibonacciRequestCount.inc();

      String fibonacciSequence = workersClusterService.getFibonacciSequence(numberCount);

      return Response.ok(fibonacciSequence).build();
   }
}
