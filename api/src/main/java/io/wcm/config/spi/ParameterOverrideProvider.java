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
package io.wcm.config.spi;

import java.util.Map;

/**
 * Provides configuration override values (default or forced).
 */
public interface ParameterOverrideProvider {

  /**
   * Returns a map with key value pairs for configuration parameter override.
   * <p>
   * Key:
   * </p>
   * <ul>
   * <li>Syntax: [{scope}]{parameterName}</li>
   * <li>{scope}: if "default", the system default parameter is overriden. Otherwise {scope} may define a configuration
   * id (path), in this case the configuration parameter is overwritten by force for this configuration level.</li>
   * <li>{parameterName}: Parameter name (from parameter definitions)</li>
   * </ul>
   * <p>
   * Value:
   * </p>
   * <ul>
   * <li>Override value</li>
   * <li>Has to be convertible to the parameter's type</li>
   * </ul>
   * @return Map (never null)
   */
  Map<String, String> getOverrideMap();

}
