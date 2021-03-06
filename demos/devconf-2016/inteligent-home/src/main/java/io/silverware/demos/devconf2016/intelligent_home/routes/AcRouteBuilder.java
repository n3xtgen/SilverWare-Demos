/*
 * -----------------------------------------------------------------------\
 * SilverWare
 *  
 * Copyright (C) 2010 - 2013 the original author or authors.
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
package io.silverware.demos.devconf2016.intelligent_home.routes;

/**
 * @author <a href="mailto:pavel.macik@gmail.com">Pavel Macík</a>
 */
public class AcRouteBuilder extends IntelligentHomeRouteBuilder {
   @Override
   public void configure() throws Exception {
      from(restBaseUri() + "/ac/on?httpMethodRestrict=GET")
            .to("direct:ac-on");

      from(restBaseUri() + "/ac/off?httpMethodRestrict=GET")
            .to("direct:ac-off");

      from("direct:ac-on")
            .to("bulldog:gpio?pin=" + config.getFanGpioPin() + "&value=HIGH");

      from("direct:ac-off")
            .to("bulldog:gpio?pin=" + config.getFanGpioPin() + "&value=LOW");
   }
}