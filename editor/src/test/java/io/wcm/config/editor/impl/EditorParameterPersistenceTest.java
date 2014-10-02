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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Parameter;
import io.wcm.config.api.ParameterBuilder;
import io.wcm.config.core.management.ConfigurationFinder;
import io.wcm.config.core.management.ParameterPersistenceData;
import io.wcm.config.core.management.ParameterResolver;
import io.wcm.config.editor.WidgetTypes;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

/**
 * Tests for the {@link EditorParameterPersistence}
 */
@RunWith(MockitoJUnitRunner.class)
public class EditorParameterPersistenceTest {

  @Mock
  private ParameterResolver parameterResolver;
  private static final String APP_ID = "/app/test";
  private static final Parameter<String> NON_EDITABLE_PARAMETER = ParameterBuilder.create("some-param", String.class, APP_ID)
      .defaultValue("defaultValue").build();
  private static final Parameter<Map> PARAMETER_MAP = ParameterBuilder.create("map-param", Map.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Parameter<String[]> PARAMETER_MULTIVALUE = ParameterBuilder.create("multivalue-param", String[].class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Parameter<String> PARAMETER_STRING = ParameterBuilder.create("string-param", String.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Parameter<Boolean> PARAMETER_BOOLEAN = ParameterBuilder.create("boolean-param", Boolean.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Parameter<Integer> PARAMETER_INTEGER = ParameterBuilder.create("integer-param", Integer.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Parameter<Long> PARAMETER_LONG = ParameterBuilder.create("long-param", Long.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Parameter<Double> PARAMETER_DOUBLE = ParameterBuilder.create("double-param", Double.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).build();
  private static final Set<Parameter<?>> PARAMETERS = ImmutableSet.<Parameter<?>>of(PARAMETER_BOOLEAN, PARAMETER_DOUBLE, PARAMETER_INTEGER, PARAMETER_LONG,
      PARAMETER_MAP, PARAMETER_MULTIVALUE, PARAMETER_STRING, NON_EDITABLE_PARAMETER);

  @Mock
  private ConfigurationFinder configurationFinder;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private Resource resource;

  @InjectMocks
  private EditorParameterPersistence underTest;

  @Before
  public void setUp() {
    when(request.getResourceResolver()).thenReturn(resourceResolver);
    when(parameterResolver.getAllParameters()).thenReturn(PARAMETERS);

    Enumeration<String> requestParameterNames = Iterators.asEnumeration(ImmutableSet.<String>of("map-param", "multivalue-param", "string-param",
        "boolean-param", "integer-param", "long-param", "double-param")
        .iterator());
    when(request.getParameterNames()).thenReturn(requestParameterNames);
    when(request.getParameterValues("map-param")).thenReturn(new String[] {
        "key1=value1;key2=value2"
    });
    when(request.getParameterValues("multivalue-param")).thenReturn(new String[] {
        "value1;value2"
    });
    when(request.getParameterValues("string-param")).thenReturn(new String[] {
        "value"
    });
    when(request.getParameterValues("integer-param")).thenReturn(new String[] {
        "1"
    });
    when(request.getParameterValues("long-param")).thenReturn(new String[] {
        "5"
    });
    when(request.getParameterValues("double-param")).thenReturn(new String[] {
        "3.454"
    });
    when(request.getParameterValues("boolean-param")).thenReturn(new String[] {
        "true"
    });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testMapValue() {
    ParameterPersistenceData data = underTest.getPersistenceData(request);
    Map<String, Object> mapParam = (Map<String, Object>)data.getValues().get("map-param");
    assertEquals(mapParam.get("key1"), "value1");
    assertEquals(mapParam.get("key2"), "value2");
  }

  @Test
  public void testMultiValue() {
    ParameterPersistenceData data = underTest.getPersistenceData(request);
    String[] multiParam = (String[])data.getValues().get("multivalue-param");
    assertEquals(multiParam[0], "value1");
    assertEquals(multiParam[1], "value2");
  }

  @Test
  public void testBooleanValue() {
    ParameterPersistenceData data = underTest.getPersistenceData(request);
    Boolean value = (Boolean)data.getValues().get("boolean-param");
    assertEquals(value, true);
  }

  @Test
  public void testIntegerValue() {
    ParameterPersistenceData data = underTest.getPersistenceData(request);
    Integer value = (Integer)data.getValues().get("integer-param");
    assertEquals(value, Integer.valueOf(1));
  }

  @Test
  public void testLongValue() {
    ParameterPersistenceData data = underTest.getPersistenceData(request);
    Long value = (Long)data.getValues().get("long-param");
    assertEquals(value, Long.valueOf(5L));
  }

  @Test
  public void testDoubleValue() {
    ParameterPersistenceData data = underTest.getPersistenceData(request);
    Double value = (Double)data.getValues().get("double-param");
    assertEquals(value, Double.valueOf(3.454));
  }
}
