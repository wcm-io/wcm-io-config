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
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import io.wcm.config.management.PersistenceException;
import io.wcm.config.spi.ParameterPersistenceProvider;

import java.util.Collections;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class ParameterPersistenceImplTest {

  private static final String CONFIG_ID = "/config1";
  private static final Map<String, Object> SAMPLE_VALUES = ImmutableMap.<String, Object>builder()
      .put("prop1", "value1")
      .put("prop2", 55)
      .build();
  private static final Map<String, Object> SAMPLE_VALUES_2 = ImmutableMap.<String, Object>builder()
      .put("prop3", "value3")
      .put("prop2", 66)
      .build();

  @Mock
  private ResourceResolver resolver;

  @Mock
  private ParameterPersistenceProvider persistenceProvider1;
  private static final Map<String, Object> SERVICE_PROPS_1 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 1L).put(Constants.SERVICE_RANKING, 10).build();

  @Mock
  private ParameterPersistenceProvider persistenceProvider2;
  private static final Map<String, Object> SERVICE_PROPS_2 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 2L).put(Constants.SERVICE_RANKING, 20).build();

  @InjectMocks
  private ParameterPersistenceImpl underTest;

  @Before
  public void setUp() {
    underTest.bindParameterPersistenceProvider(persistenceProvider1, SERVICE_PROPS_1);
    underTest.bindParameterPersistenceProvider(persistenceProvider2, SERVICE_PROPS_2);
  }

  @Test
  public void testGetValues_FirstProvider() {
    when(persistenceProvider1.get(resolver, CONFIG_ID)).thenReturn(SAMPLE_VALUES);
    when(persistenceProvider2.get(resolver, CONFIG_ID)).thenReturn(SAMPLE_VALUES_2);
    assertEquals(SAMPLE_VALUES, underTest.getValues(resolver, CONFIG_ID));
  }

  @Test
  public void testGetValues_SecondProvider() {
    when(persistenceProvider1.get(resolver, CONFIG_ID)).thenReturn(null);
    when(persistenceProvider2.get(resolver, CONFIG_ID)).thenReturn(SAMPLE_VALUES_2);
    assertEquals(SAMPLE_VALUES_2, underTest.getValues(resolver, CONFIG_ID));
  }

  @Test
  public void testGetValues_NoProvider() {
    when(persistenceProvider1.get(resolver, CONFIG_ID)).thenReturn(null);
    when(persistenceProvider2.get(resolver, CONFIG_ID)).thenReturn(null);
    assertEquals(Collections.EMPTY_MAP, underTest.getValues(resolver, CONFIG_ID));
  }

  @Test
  public void testStoreValues_FirstProvider() throws PersistenceException {
    when(persistenceProvider1.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(true);
    when(persistenceProvider2.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(true);
    underTest.storeParameterValues(resolver, CONFIG_ID, SAMPLE_VALUES);
    verify(persistenceProvider1, times(1)).store(resolver, CONFIG_ID, SAMPLE_VALUES);
    verifyNoMoreInteractions(persistenceProvider2);
  }


  @Test
  public void testStoreValues_SecondProvider() throws PersistenceException {
    when(persistenceProvider1.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(false);
    when(persistenceProvider2.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(true);
    underTest.storeParameterValues(resolver, CONFIG_ID, SAMPLE_VALUES);
    verify(persistenceProvider1, times(1)).store(resolver, CONFIG_ID, SAMPLE_VALUES);
    verify(persistenceProvider2, times(1)).store(resolver, CONFIG_ID, SAMPLE_VALUES);
  }

  @Test(expected = PersistenceException.class)
  public void testStoreValues_NoProvider() throws PersistenceException {
    when(persistenceProvider1.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(false);
    when(persistenceProvider2.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(false);
    underTest.storeParameterValues(resolver, CONFIG_ID, SAMPLE_VALUES);
    verify(persistenceProvider1, times(1)).store(resolver, CONFIG_ID, SAMPLE_VALUES);
    verify(persistenceProvider2, times(1)).store(resolver, CONFIG_ID, SAMPLE_VALUES);
  }

  @Test
  public void testStoreValues_MergeWithExisting() throws PersistenceException {
    when(persistenceProvider1.get(resolver, CONFIG_ID)).thenReturn(SAMPLE_VALUES);
    when(persistenceProvider1.store(same(resolver), eq(CONFIG_ID), anyMapOf(String.class, Object.class))).thenReturn(true);
    underTest.storeParameterValues(resolver, CONFIG_ID, SAMPLE_VALUES_2, true);

    Map<String,Object> expecMap = ImmutableMap.<String,Object>builder()
        .put("prop1", "value1")
        .put("prop3", "value3")
        .put("prop2", 66)
        .build();
    verify(persistenceProvider1).store(resolver, CONFIG_ID, expecMap);
  }

}
