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
import io.wcm.config.spi.ConfigurationFinderStrategy;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

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
  public void testFindConfigurationIds() throws Exception {
    List<String> configurationIds;

    when(resource.getPath()).thenReturn("/");
    configurationIds = ImmutableList.copyOf(underTest.findConfigurationIds(resource));
    assertEquals(0, configurationIds.size());

    when(resource.getPath()).thenReturn("/content");
    configurationIds = ImmutableList.copyOf(underTest.findConfigurationIds(resource));
    assertEquals(0, configurationIds.size());

    when(resource.getPath()).thenReturn("/content/region1");
    configurationIds = ImmutableList.copyOf(underTest.findConfigurationIds(resource));
    assertEquals(1, configurationIds.size());
    assertEquals("/content/region1", configurationIds.get(0));

    when(resource.getPath()).thenReturn("/content/region1/site1");
    configurationIds = ImmutableList.copyOf(underTest.findConfigurationIds(resource));
    assertEquals(2, configurationIds.size());
    assertEquals("/content/region1", configurationIds.get(0));
    assertEquals("/content/region1/site1", configurationIds.get(1));

    when(resource.getPath()).thenReturn("/content/region1/site1/page1");
    configurationIds = ImmutableList.copyOf(underTest.findConfigurationIds(resource));
    assertEquals(2, configurationIds.size());
    assertEquals("/content/region1", configurationIds.get(0));
    assertEquals("/content/region1/site1", configurationIds.get(1));
  }


}
