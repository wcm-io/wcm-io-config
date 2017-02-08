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
package io.wcm.config.spi.helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import io.wcm.config.spi.ConfigurationFinderStrategy;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAbsoluteParentConfigurationFinderStrategyTest {

  private static final String APP_ID = "/apps/app1";

  @Mock
  private Resource resource;

  private ConfigurationFinderStrategy underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new AbstractAbsoluteParentConfigurationFinderStrategy(APP_ID, 1, 2) {
      // nothing to override
    };
  }

  @Test
  public void testGetApplicationId() {
    assertEquals(APP_ID, underTest.getApplicationId());
  }

  @Test
  public void testFindConfigurationIds() {

    assertConfigurationIds("/", new String[0]);
    assertConfigurationIds("/content", new String[0]);

    assertConfigurationIds("/content/region1",
        "/content/region1");

    assertConfigurationIds("/content/region1/site1",
        "/content/region1",
        "/content/region1/site1");

    assertConfigurationIds("/content/region1/site1/page1",
        "/content/region1",
        "/content/region1/site1");
  }

  private void assertConfigurationIds(String resourcePath, String... configurationIds) {
    List<String> expectedConfigurationIds = ImmutableList.copyOf(configurationIds);
    when(resource.getPath()).thenReturn(resourcePath);
    List<String> detectedConfigurationIds = ImmutableList.copyOf(underTest.findConfigurationIds(resource));
    assertEquals(expectedConfigurationIds, detectedConfigurationIds);
  }

}
