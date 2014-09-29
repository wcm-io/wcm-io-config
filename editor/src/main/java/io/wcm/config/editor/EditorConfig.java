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
package io.wcm.config.editor;

import io.wcm.config.core.management.ParameterPersistence;
import io.wcm.sling.models.annotations.AemObject;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;

/**
 * {@link EditorConfig} implementation for the editor pages created under /tools/config
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class EditorConfig {

  private final String lockedNamesAttributeName;
  private final String providerUrl;

  /**
   * @param currentPage
   * @param resourceResolver
   * @param self
   */
  @Inject
  public EditorConfig(
      @AemObject Page currentPage,
      @SlingObject ResourceResolver resourceResolver,
      @Self Adaptable self) {
    lockedNamesAttributeName = ParameterPersistence.PN_LOCKED_PARAMETER_NAMES;
    providerUrl = "";
  }

  public String getLockedNamesAttributeName() {
    return lockedNamesAttributeName;
  }

  public String getProviderUrl() {
    return providerUrl;
  }
}
