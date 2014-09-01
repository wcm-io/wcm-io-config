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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Parameter;
import io.wcm.config.spi.ParameterBuilder;
import io.wcm.config.spi.ParameterOverrideProvider;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class ParameterOverrideImplTest {

  @Mock
  private ParameterOverrideProvider provider1;
  private static final Map<String, Object> SERVICE_PROPS_1 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 1L).put(Constants.SERVICE_RANKING, 10).build();

  @Mock
  private ParameterOverrideProvider provider2;
  private static final Map<String, Object> SERVICE_PROPS_2 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 2L).put(Constants.SERVICE_RANKING, 5).build();

  private static final Parameter<String> PARAM1 = ParameterBuilder.create("param1", String.class).build();

  private ParameterOverrideImpl underTest;

  @Before
  public void setUp() {
    when(provider1.getOverrideMap()).thenReturn(ImmutableMap.<String, String>builder()
        .put("[default]param1", "value1")
        .put("[/config1]param1", "value11")
        .build());
    when(provider2.getOverrideMap()).thenReturn(ImmutableMap.<String, String>builder()
        .put("[default]param1", "value2")
        .build());

    underTest = new ParameterOverrideImpl();
    underTest.bindParameterOverrideProvider(provider1, SERVICE_PROPS_1);
    underTest.bindParameterOverrideProvider(provider2, SERVICE_PROPS_2);
  }

  @Test
  public void testOverrideSystemDefault() {
    assertEquals("value2", underTest.getOverrideSystemDefault(PARAM1));
  }

  @Test
  public void testOverrideForce() {
    assertEquals("value11", underTest.getOverrideForce("/config1", PARAM1));
    assertNull(underTest.getOverrideForce("/config2", PARAM1));
  }

  @Test
  public void testTypes() {
    when(provider1.getOverrideMap()).thenReturn(ImmutableMap.<String, String>builder()
        .put("[default]stringParam", "value1")
        .put("[default]stringArrayParam", "value1;value2;")
        .put("[default]integerParam", "55")
        .put("[default]longParam", "66")
        .put("[default]doubleParam", "1.23")
        .put("[default]booleanParam", "true")
        .put("[default]mapParam", "key1=abc;key2=def;key3=;;=xyz")
        .build());

    assertEquals("value1", underTest.getOverrideSystemDefault(
        ParameterBuilder.create("stringParam", String.class).build()));
    assertArrayEquals(new String[] {
        "value1", "value2", ""
    }, underTest.getOverrideSystemDefault(ParameterBuilder.create("stringArrayParam", String[].class).build()));
    assertEquals((Integer)55, underTest.getOverrideSystemDefault(
        ParameterBuilder.create("integerParam", Integer.class).build()));
    assertEquals((Long)66L, underTest.getOverrideSystemDefault(
        ParameterBuilder.create("longParam", Long.class).build()));
    assertEquals(1.23d, underTest.getOverrideSystemDefault(
        ParameterBuilder.create("doubleParam", Double.class).build()), 0.0001d);
    assertEquals(true, underTest.getOverrideSystemDefault(
        ParameterBuilder.create("booleanParam", Boolean.class).build()));
    Map<String, String> map = new LinkedHashMap<>();
    map.put("key1", "abc");
    map.put("key2", "def");
    map.put("key3", "");
    assertEquals(map, underTest.getOverrideSystemDefault(
        ParameterBuilder.create("mapParam", Map.class).build()));
  }

}
