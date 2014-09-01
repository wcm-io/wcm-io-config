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
package io.wcm.config.core.management.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Configuration;
import io.wcm.config.management.ParameterResolver;
import io.wcm.config.spi.ConfigurationFinderStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationFinderImplTest {

  @Mock
  private Resource resource;
  @Mock
  private ParameterResolver parameterResolver;

  @Mock
  private ConfigurationFinderStrategy finderStrategy1;
  private static final Map<String, Object> SERVICE_PROPS_1 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 1L).put(Constants.SERVICE_RANKING, 10).build();
  private static final String APPLICATION_ID_1 = "app1";

  @Mock
  private ConfigurationFinderStrategy finderStrategy2;
  private static final Map<String, Object> SERVICE_PROPS_2 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 2L).put(Constants.SERVICE_RANKING, 5).build();
  private static final String APPLICATION_ID_2 = "app2";

  @InjectMocks
  private ConfigurationFinderImpl underTest;

  @Before
  public void setUp() {
    underTest.bindConfigurationFinderStrategy(finderStrategy1, SERVICE_PROPS_1);
    underTest.bindConfigurationFinderStrategy(finderStrategy2, SERVICE_PROPS_2);

    when(finderStrategy1.findConfigurationIds(resource)).thenReturn(ImmutableList.<String>builder()
        .add("/content/region1/region11/site")
        .build().iterator());
    when(finderStrategy1.getApplicationId()).thenReturn(APPLICATION_ID_1);

    when(finderStrategy2.findConfigurationIds(resource)).thenReturn(ImmutableList.<String>builder()
        .add("/content/region1/region11/site/language")
        .add("/content/region1")
        .build().iterator());
    when(finderStrategy2.getApplicationId()).thenReturn(APPLICATION_ID_2);

    when(parameterResolver.getEffectiveValues(anyCollectionOf(String.class)))
    .then(new Answer<Map<String,Object>>() {
      @SuppressWarnings("unchecked")
      @Override
      public Map<String, Object> answer(InvocationOnMock invocation) {
        Collection<String> configurationIds = (Collection<String>)invocation.getArguments()[0];
        Map<String, Object> props = new HashMap<>();
        props.put("path", configurationIds.iterator().next());
        return props;
      }
    });
  }

  @After
  public void tearDown() {
    underTest.unbindConfigurationFinderStrategy(finderStrategy1, SERVICE_PROPS_1);
    underTest.unbindConfigurationFinderStrategy(finderStrategy2, SERVICE_PROPS_2);
  }

  @Test
  public void testFindResource() {
    Configuration conf = underTest.find(resource);
    assertNotNull(conf);
    assertEquals("/content/region1/region11/site/language", conf.getConfigurationId());
    assertEquals("/content/region1/region11/site/language", conf.get("path", String.class));
  }

  @Test
  public void testFindResourceForApplication() {
    Configuration conf1 = underTest.find(resource, APPLICATION_ID_1);
    assertNotNull(conf1);
    assertEquals("/content/region1/region11/site", conf1.getConfigurationId());
    assertEquals("/content/region1/region11/site", conf1.get("path", String.class));

    Configuration conf2 = underTest.find(resource, APPLICATION_ID_2);
    assertNotNull(conf2);
    assertEquals("/content/region1/region11/site/language", conf2.getConfigurationId());
    assertEquals("/content/region1/region11/site/language", conf2.get("path", String.class));

    Configuration conf3 = underTest.find(resource, "invalidAppId");
    assertNull(conf3);
  }

  @Test
  public void testFindAllResource() {
    Iterator<Configuration> confs = underTest.findAll(resource);
    List<Configuration> confList = Lists.newArrayList(confs);
    assertEquals(3, confList.size());
    assertEquals("/content/region1/region11/site/language", confList.get(0).getConfigurationId());
    assertEquals("/content/region1/region11/site", confList.get(1).getConfigurationId());
    assertEquals("/content/region1", confList.get(2).getConfigurationId());
  }

  @Test
  public void testFindAllResourceForApplication() {
    Iterator<Configuration> confs = underTest.findAll(resource, APPLICATION_ID_2);
    List<Configuration> confList = Lists.newArrayList(confs);
    assertEquals(2, confList.size());
    assertEquals("/content/region1/region11/site/language", confList.get(0).getConfigurationId());
    assertEquals("/content/region1", confList.get(1).getConfigurationId());
  }

}
