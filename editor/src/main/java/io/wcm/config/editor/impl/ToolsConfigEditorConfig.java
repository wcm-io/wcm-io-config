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
package io.wcm.config.editor.impl;

import io.wcm.config.editor.EditorConfig;
import io.wcm.sling.models.annotations.AemObject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;
import com.day.jcr.vault.util.Text;

/**
 * {@link EditorConfig} implementation for the editor pages created under /tools/config
 */
@Model(adaptables = {
    SlingHttpServletRequest.class,
    Resource.class
}, adapters = EditorConfig.class)
public class ToolsConfigEditorConfig implements EditorConfig {

  @AemObject
  private Page currentPage;

  @SlingObject
  private ResourceResolver resourceResolver;


  @Override
  public Resource getResourceForConfigurationFinder() {
    String path = Text.getRelativeParent(currentPage.getPath(), 2);
    return resourceResolver.getResource(path);
  }
}
