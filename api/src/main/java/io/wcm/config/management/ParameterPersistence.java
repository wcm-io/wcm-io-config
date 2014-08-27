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
package io.wcm.config.management;

import java.util.Map;

/**
 * Manages reading and storing parameter values for a single configuration (without inheritance).
 */
public interface ParameterPersistence {

  /**
   * Get all parameter values stored for a configuration.
   * @param configurationId Configuration id
   * @return Set of parameter values
   */
  Map<String, Object> getValues(String configurationId);

  /**
   * Writes parameter values for a configuration.
   * All existing parameter values are erased before writing the new ones.
   * @param configurationId Configuration id
   * @param values Parameter values
   */
  void setParameterValues(String configurationId, Map<String, Object> values);

  /**
   * Writes parameter values for a configuration.
   * @param configurationId Configuration id
   * @param values Parameter values
   * @param mergeWithExisting If true, existing parameter values are only overridden when they are contained in the
   *          set of parameter values. Otherwise all existing parameter values are erased before writing the new ones.
   */
  void setParameterValues(String configurationId, Map<String, Object> values, boolean mergeWithExisting);

}
