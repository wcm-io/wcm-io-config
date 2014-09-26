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
package io.wcm.config.core.persistence;

import io.wcm.config.api.management.PersistenceException;
import io.wcm.config.spi.ParameterPersistenceProvider;

import java.util.Dictionary;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

/**
 * Persistence provider that stores configuration values in pages in a path tools/config relative to the config id.
 */
@Component(immediate = true, metatype = true,
label = "wcm.io Configuration Persistence Provider: /tools/config Pages",
description = "Allows to read and store configurations in /tools/config pages.")
@Service(ParameterPersistenceProvider.class)
public final class ToolsConfigPagePersistenceProvider implements ParameterPersistenceProvider {

  static final String CONFIG_PAGE_NAME = "config";
  static final String CONFIG_RESOURCE_NAME = "config";
  static final String TOOLS_PAGE_NAME = "tools";
  static final String RELATIVE_CONFIG_PATH = "/" + TOOLS_PAGE_NAME + "/" + CONFIG_PAGE_NAME;

  @Property(label = "Enabled", boolValue = ToolsConfigPagePersistenceProvider.DEFAULT_ENABLED,
      description = "Enable parameter persistence provider")
  static final String PROPERTY_ENABLED = "enabled";
  static final boolean DEFAULT_ENABLED = false;

  @Property(label = "Service Ranking", intValue = ToolsConfigPagePersistenceProvider.DEFAULT_RANKING,
      description = "Priority of parameter persistence providers (lower = higher priority)",
      propertyPrivate = false)
  static final String PROPERTY_RANKING = Constants.SERVICE_RANKING;
  static final int DEFAULT_RANKING = 1000;

  @Property(label = "Config Template", value = ToolsConfigPagePersistenceProvider.DEFAULT_CONFIG_PAGE_TEMPLATE,
      description = "Template that is used for a configuration page.")
  static final String PROPERTY_CONFIG_PAGE_TEMPLATE = "configPageTemplate";
  // TODO: default value for config page template?
  static final String DEFAULT_CONFIG_PAGE_TEMPLATE = "";

  @Property(label = "Tools Template", value = ToolsConfigPagePersistenceProvider.DEFAULT_TOOLS_PAGE_TEMPLATE,
      description = "Template that is used for tools page.")
  static final String PROPERTY_TOOLS_PAGE_TEMPLATE = "toolsPageTemplate";
  // TODO: default value for tools page template?
  static final String DEFAULT_TOOLS_PAGE_TEMPLATE = "";

  private boolean enabled;
  private String configPageTemplate;
  private String toolsPageTemplate;

  @Override
  public Map<String, Object> get(ResourceResolver resolver, String configurationId) {
    if (!enabled) {
      return null;
    }
    Page configPage = getConfigPage(resolver, configurationId);
    if (configPage != null) {
      return getConfigMap(configPage);
    }
    return null;
  }

  @Override
  public boolean store(ResourceResolver resolver, String configurationId, Map<String, Object> values)
      throws PersistenceException {
    if (!enabled) {
      return false;
    }
    Page configPage = getOrCreateConfigPage(resolver, configurationId);
    storeValues(resolver, configPage, values);
    return true;
  }

  private String getConfigPagePath(String configurationId) {
    return configurationId + RELATIVE_CONFIG_PATH;
  }

  private Page getConfigPage(ResourceResolver resolver, String configurationId) {
    String path = getConfigPagePath(configurationId);
    Resource resource = resolver.getResource(path);
    if (resource != null) {
      return resource.adaptTo(Page.class);
    }
    return null;
  }

  private Map<String, Object> getConfigMap(Page page) {
    Resource configResource = page.getContentResource(CONFIG_RESOURCE_NAME);
    if (configResource != null) {
      return configResource.getValueMap();
    }
    else {
      return ValueMap.EMPTY;
    }
  }

  private Page getOrCreateConfigPage(ResourceResolver resolver, String configurationId) throws PersistenceException {
    Page configPage = getConfigPage(resolver, configurationId);
    if (configPage == null) {
      PageManager pageManager = resolver.adaptTo(PageManager.class);
      String configPagePath = getConfigPagePath(configurationId);
      String toolsPagePath = ResourceUtil.getParent(configPagePath);

      Resource toolsPageResource = resolver.getResource(toolsPagePath);
      if (toolsPageResource == null) {
        try {
          pageManager.create(configurationId, TOOLS_PAGE_NAME, this.toolsPageTemplate, TOOLS_PAGE_NAME, true);
        }
        catch (WCMException ex) {
          throw new PersistenceException("Creating page at " + toolsPagePath + " failed.", ex);
        }
      }

      try {
        configPage = pageManager.create(toolsPagePath, CONFIG_PAGE_NAME, this.configPageTemplate, CONFIG_PAGE_NAME, true);
      }
      catch (WCMException ex) {
        throw new PersistenceException("Creating page at " + configPagePath + " failed.", ex);
      }
    }
    return configPage;
  }

  private void storeValues(ResourceResolver resolver, Page configPage, Map<String, Object> values) throws PersistenceException {
    try {
      Resource configResource = configPage.getContentResource(CONFIG_RESOURCE_NAME);
      if (configResource != null) {
        resolver.delete(configResource);
      }
      configResource = resolver.create(configPage.getContentResource(), CONFIG_RESOURCE_NAME, values);
      resolver.commit();
    }
    catch (org.apache.sling.api.resource.PersistenceException ex) {
      throw new PersistenceException("Storing configuration values to " + configPage.getPath() + " failed.", ex);
    }
  }

  @Activate
  void activate(final ComponentContext ctx) {
    Dictionary config = ctx.getProperties();
    this.enabled = PropertiesUtil.toBoolean(config.get(PROPERTY_ENABLED), DEFAULT_ENABLED);
    this.configPageTemplate = PropertiesUtil.toString(config.get(PROPERTY_CONFIG_PAGE_TEMPLATE), DEFAULT_CONFIG_PAGE_TEMPLATE);
    this.toolsPageTemplate = PropertiesUtil.toString(config.get(PROPERTY_TOOLS_PAGE_TEMPLATE), DEFAULT_TOOLS_PAGE_TEMPLATE);
  }

}
