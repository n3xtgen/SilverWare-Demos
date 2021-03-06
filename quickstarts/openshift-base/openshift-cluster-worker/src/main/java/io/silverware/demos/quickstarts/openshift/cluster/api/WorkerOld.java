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
package io.silverware.demos.quickstarts.openshift.cluster.api;

import static io.silverware.demos.quickstarts.openshift.cluster.api.WorkerInterface.VERSION_1;

import io.silverware.microservices.annotations.Microservice;
import io.silverware.microservices.annotations.MicroserviceVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Older implementation of worker.
 *
 * NOTE: Do not mix multiple version of services in same project.
 *
 * @author Slavomír Krupa (slavomir.krupa@gmail.com)
 */
@Microservice
@MicroserviceVersion(api = VERSION_1)
public class WorkerOld implements WorkerInterface {

   private static final Logger log = LogManager.getLogger(WorkerOld.class);

   @Override
   public String callHello() {
      log.info("Hello!");
      return HELLO;

   }

   @Override
   public String getVersion() {
      return VERSION_1;
   }

   @Override
   public CustomObject customSerialization(final CustomObject customObject) {
      CustomObject result = CustomObject.randomObject();
      log.info("Received: {} returning: {} ", customObject, result);
      return result;
   }
}
