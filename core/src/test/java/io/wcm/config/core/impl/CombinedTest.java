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

import static org.junit.Assert.assertNotNull;
import io.wcm.config.api.Configuration;
import io.wcm.config.core.management.impl.ConfigurationFinderImpl;
import io.wcm.config.core.management.impl.ParameterOverrideImpl;
import io.wcm.config.core.management.impl.ParameterPersistenceImpl;
import io.wcm.config.core.management.impl.ParameterResolverImpl;
import io.wcm.config.core.override.RequestHeaderOverrideProvider;
import io.wcm.config.core.override.SystemPropertyOverrideProvider;
import io.wcm.config.core.persistence.ToolsConfigPagePersistenceProvider;
import io.wcm.config.spi.ConfigurationFinderStrategy;
import io.wcm.testing.mock.aem.junit.AemContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.day.jcr.vault.util.Text;
import com.google.common.collect.ImmutableMap;

/**
 * Test all configuration services in combination.
 */
public class CombinedTest {

  private static final String CONFIG_ID = "/content/region1/site1/en";

  @Rule
  public final AemContext context = new AemContext();

  @Before
  public void setUp() throws Exception {
    // app-specific configuration finder strategy
    context.registerService(ConfigurationFinderStrategy.class, new SampleConfigurationFinderStrategy());

    // persistence providers
    context.registerInjectActivateService(new ToolsConfigPagePersistenceProvider(),
        ImmutableMap.<String, Object>builder()
        .put("enabled", true)
        .build());

    // override providers
    context.registerInjectActivateService(new RequestHeaderOverrideProvider(),
        ImmutableMap.<String, Object>builder()
        .put("enabled", true)
        .build());
    context.registerInjectActivateService(new SystemPropertyOverrideProvider(),
        ImmutableMap.<String, Object>builder()
        .put("enabled", true)
        .build());

    // management services
    context.registerInjectActivateService(new ParameterOverrideImpl());
    context.registerInjectActivateService(new ParameterPersistenceImpl());
    context.registerInjectActivateService(new ParameterResolverImpl());
    context.registerInjectActivateService(new ConfigurationFinderImpl());

    // adapter factory
    context.registerInjectActivateService(new ConfigurationAdapterFactory());

    // mount sample content
    context.jsonImporter().importTo("/sample-content.json", "/content");
    context.currentPage(CONFIG_ID);
  }

  @Test
  public void testConfig() {
    Configuration config = context.request().getResource().adaptTo(Configuration.class);
    assertNotNull(config);
  }

  // TODO: implement further tests!


  private static class SampleConfigurationFinderStrategy implements ConfigurationFinderStrategy {

    @Override
    public String getApplicationId() {
      return "sample";
    }

    @Override
    public Iterator<String> findConfigurationIds(Resource resource) {
      List<String> configurationIds = new ArrayList<>();
      addAbsoluteParent(configurationIds, resource, 3);
      addAbsoluteParent(configurationIds, resource, 2);
      addAbsoluteParent(configurationIds, resource, 1);
      return configurationIds.iterator();
    }

    private void addAbsoluteParent(List<String> configurationIds, Resource resource, int absoluteParent) {
      String configurationId = Text.getAbsoluteParent(resource.getPath(), absoluteParent);
      if (StringUtils.isNotEmpty(configurationId)) {
        configurationIds.add(configurationId);
      }
    }

  }

}
