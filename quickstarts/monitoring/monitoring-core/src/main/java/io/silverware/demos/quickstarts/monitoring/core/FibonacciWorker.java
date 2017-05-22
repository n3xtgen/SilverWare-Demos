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
package io.silverware.demos.quickstarts.monitoring.core;

import io.opentracing.SpanContext;

import java.util.Map;

/**
 * Interface for clustered worker microservice which generates Fibonacci squence with given count of numbers.
 *
 * @author Jaroslav Dufek (email@n3xtgen.cz)
 */
public interface FibonacciWorker {

   /**
    * Gets string of Fibonacci sequence with given count of numbers.
    * @param numberCount count of fibonacci numbers
    * @return string "1 1 2 3 5 8 13 21 44 ..."
    */
   String getFibonacciSequence(int numberCount, Map contextMap);
}
