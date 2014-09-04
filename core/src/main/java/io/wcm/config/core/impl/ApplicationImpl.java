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
package io.wcm.config.core.impl;

import io.wcm.config.api.Application;

/**
 * Default implementation of {@link Application}.
 */
public class ApplicationImpl implements Application {

  private final String applicationId;
  private final String label;

  /**
   * @param applicationId Application id
   * @param label Label
   */
  public ApplicationImpl(String applicationId, String label) {
    this.applicationId = applicationId;
    this.label = label;
  }

  @Override
  public String getApplicationId() {
    return this.applicationId;
  }

  @Override
  public String getLabel() {
    return this.label;
  }

}
