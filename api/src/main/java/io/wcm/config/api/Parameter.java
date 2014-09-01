/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.config.api;

import org.apache.sling.api.resource.ValueMap;

/**
 * Defines a configuration parameter.
 * @param <T> Parameter value type
 */
public interface Parameter<T> extends Comparable<Parameter> {

  /**
   * @return Parameter name
   */
  String getName();

  /**
   * @return Parameter type
   */
  Class<T> getType();

  /**
   * @return Application Id
   */
  String getApplicationId();

  /**
   * @return Parameter visibility
   */
  Visibility getVisibility();

  /**
   * References OSGi configuration property which is checked for default value if this parameter is not set
   * in any configuration.
   * @return OSGi configuration parameter name with syntax {serviceClassName}:{propertyName}
   */
  String getDefaultOsgiConfigProperty();

  /**
   * @return Default value if parameter is not set for configuration
   *         and no default value is defined in OSGi configuration
   */
  T getDefaultValue();

  /**
   * @return Further properties for documentation and configuration of behavior in configuration editor.
   */
  ValueMap getProperties();

}
