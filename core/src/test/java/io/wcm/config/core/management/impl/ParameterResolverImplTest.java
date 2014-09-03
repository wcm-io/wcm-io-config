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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Parameter;
import io.wcm.config.api.ParameterBuilder;
import io.wcm.config.api.management.ParameterOverride;
import io.wcm.config.api.management.ParameterPersistence;
import io.wcm.config.spi.ParameterProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class ParameterResolverImplTest {

  @Mock
  private ResourceResolver resolver;
  @Mock
  private ComponentContext componentContext;
  @Mock
  private BundleContext bundleContext;
  @Mock
  private ServiceReference serviceReference;
  @Mock
  private ParameterPersistence parameterPersistence;
  @Mock
  private ParameterOverride parameterOverride;

  @Mock
  private ParameterProvider parameterProvider1;
  private static final Map<String, Object> SERVICE_PROPS_1 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 1L).put(Constants.SERVICE_RANKING, 10).build();
  private static final Parameter<String> PARAM11 =
      ParameterBuilder.create("param11", String.class).build();
  private static final Parameter<String> PARAM12 =
      ParameterBuilder.create("param12", String.class).defaultValue("defValue").build();
  private static final Parameter<String> PARAM13 =
      ParameterBuilder.create("param13", String.class).defaultValue("defValue")
      .defaultOsgiConfigProperty("my.service:prop1").build();

  @Mock
  private ParameterProvider parameterProvider2;
  private static final Map<String, Object> SERVICE_PROPS_2 = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 2L).put(Constants.SERVICE_RANKING, 20).build();
  private static final Parameter<Integer> PARAM21 =
      ParameterBuilder.create("param21", Integer.class).defaultValue(55).build();

  @InjectMocks
  private ParameterResolverImpl underTest;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    when(componentContext.getBundleContext()).thenReturn(bundleContext);
    when(bundleContext.getServiceReference("my.service")).thenReturn(serviceReference);
    when(serviceReference.getProperty("prop1")).thenReturn("valueFromOsgiConfig");

    when(parameterPersistence.getValues(same(resolver), anyString())).thenReturn(Collections.<String, Object>emptyMap());
    when(parameterOverride.getOverrideSystemDefault(any(Parameter.class))).thenReturn(null);
    when(parameterOverride.getOverrideForce(anyString(), any(Parameter.class))).thenReturn(null);

    Set<Parameter<?>> params1 = new HashSet<>();
    params1.add(PARAM11);
    params1.add(PARAM12);
    params1.add(PARAM13);
    when(parameterProvider1.getParameters()).thenReturn(params1);

    Set<Parameter<?>> params2 = new HashSet<>();
    params2.add(PARAM21);
    when(parameterProvider2.getParameters()).thenReturn(params2);

    underTest.bindParameterProvider(parameterProvider1, SERVICE_PROPS_1);
    underTest.bindParameterProvider(parameterProvider2, SERVICE_PROPS_2);
  }

  @Test
  public void testDefaultValues() {
    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList("/config1"));
    assertNull(values.get("param11"));
    assertEquals("defValue", values.get("param12"));
    assertEquals("valueFromOsgiConfig", values.get("param13"));
    assertEquals(55, values.get("param21"));
  }

  @Test
  public void testOverrideSystemDefault() {
    when(parameterOverride.getOverrideSystemDefault(PARAM11)).thenReturn("override11");
    when(parameterOverride.getOverrideSystemDefault(PARAM12)).thenReturn("override12");
    when(parameterOverride.getOverrideSystemDefault(PARAM13)).thenReturn("override13");
    when(parameterOverride.getOverrideSystemDefault(PARAM21)).thenReturn(66);

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList("/config1"));
    assertEquals("override11", values.get("param11"));
    assertEquals("override12", values.get("param12"));
    assertEquals("override13", values.get("param13"));
    assertEquals(66, values.get("param21"));
  }

  @Test
  public void testConfiguredValues() {
    when(parameterPersistence.getValues(resolver, "/config1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "config11")
        .put("param12", "config12")
        .put("param13", "config13")
        .put("param21", 77)
        .build());

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList("/config1"));
    assertEquals("config11", values.get("param11"));
    assertEquals("config12", values.get("param12"));
    assertEquals("config13", values.get("param13"));
    assertEquals(77, values.get("param21"));
  }

  @Test
  public void testConfigurationHierarchy() {
    when(parameterPersistence.getValues(resolver, "/region1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "r11")
        .put("param12", "r12")
        .put("param21", 88)
        .build());
    when(parameterPersistence.getValues(resolver, "/region1/site1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "s11")
        .put("param21", 99)
        .build());
    when(parameterPersistence.getValues(resolver, "/region1/site1/config1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "c11")
        .build());

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList(
        "/region1/site1/config1", "/region1/site1", "/region1"));
    assertEquals("c11", values.get("param11"));
    assertEquals("r12", values.get("param12"));
    assertEquals("valueFromOsgiConfig", values.get("param13"));
    assertEquals(99, values.get("param21"));
  }

  @Test
  public void testOverrideForce() {
    when(parameterPersistence.getValues(resolver, "/config1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "config11")
        .put("param12", "config12")
        .put("param13", "config13")
        .put("param21", 77)
        .build());

    when(parameterOverride.getOverrideForce("/config1", PARAM11)).thenReturn("override11");
    when(parameterOverride.getOverrideForce("/config1", PARAM12)).thenReturn("override12");
    when(parameterOverride.getOverrideForce("/config1", PARAM13)).thenReturn("override13");
    when(parameterOverride.getOverrideForce("/config1", PARAM21)).thenReturn(66);

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList("/config1"));
    assertEquals("override11", values.get("param11"));
    assertEquals("override12", values.get("param12"));
    assertEquals("override13", values.get("param13"));
    assertEquals(66, values.get("param21"));
  }

  @Test
  public void testConfigurationHierarchyWithOverrides() {
    when(parameterPersistence.getValues(resolver, "/region1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "r11")
        .put("param12", "r12")
        .put("param21", 88)
        .build());
    when(parameterPersistence.getValues(resolver, "/region1/site1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "s11")
        .put("param21", 99)
        .build());
    when(parameterPersistence.getValues(resolver, "/region1/site1/config1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("param11", "c11")
        .build());

    when(parameterOverride.getOverrideSystemDefault(PARAM13)).thenReturn("override13");
    when(parameterOverride.getOverrideForce("/region1/site1", PARAM21)).thenReturn(66);

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList(
        "/region1/site1/config1", "/region1/site1", "/region1"));
    assertEquals("c11", values.get("param11"));
    assertEquals("r12", values.get("param12"));
    assertEquals("override13", values.get("param13"));
    assertEquals(66, values.get("param21"));
  }

  @Test
  public void testOsgiTypes() {
    when(parameterProvider1.getParameters()).thenReturn(ImmutableSet.<Parameter<?>>builder()
        .add(ParameterBuilder.create("stringParam", String.class).defaultOsgiConfigProperty("my.service:stringParam").build())
        .add(ParameterBuilder.create("stringParamUnset", String.class).defaultOsgiConfigProperty("my.service:stringParamUnset").build())
        .add(ParameterBuilder.create("stringArrayParam", String[].class).defaultOsgiConfigProperty("my.service:stringArrayParam").build())
        .add(ParameterBuilder.create("integerParam", Integer.class).defaultOsgiConfigProperty("my.service:integerParam").build())
        .build());

    when(serviceReference.getProperty("stringParam")).thenReturn("stringValue");
    when(serviceReference.getProperty("stringArrayParam")).thenReturn(new String[] {
        "v1", "v2"
    });
    when(serviceReference.getProperty("integerParam")).thenReturn("25");

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList("/config1"));
    assertEquals("stringValue", values.get("stringParam"));
    assertNull(values.get("stringParamUnset"));
    assertArrayEquals(new String[] {
        "v1", "v2"
    }, (String[])values.get("stringArrayParam"));
    assertEquals(25, values.get("integerParam"));
  }

  @Test
  public void testConfiguredValuesInvalidTypes() {
    when(parameterProvider1.getParameters()).thenReturn(ImmutableSet.<Parameter<?>>builder()
        .add(ParameterBuilder.create("stringParam", String.class).build())
        .add(ParameterBuilder.create("stringParamDefaultValue", String.class).defaultValue("def").build())
        .add(ParameterBuilder.create("stringArrayParam", String[].class).build())
        .add(ParameterBuilder.create("integerParam", Integer.class).build())
        .add(ParameterBuilder.create("integerParamDefaultValue", Integer.class).defaultValue(22).build())
        .build());

    when(parameterPersistence.getValues(resolver, "/config1")).thenReturn(ImmutableMap.<String, Object>builder()
        .put("stringParam", 55)
        .put("stringParamDefaultValue", 66L)
        .put("stringArrayParam", new int[] {
            1, 2, 3
        })
        .put("integerParam", "value1")
        .put("integerParamDefaultValue", "value2")
        .build());

    Map<String, Object> values = underTest.getEffectiveValues(resolver, Arrays.asList("/config1"));
    assertNull(values.get("stringParam"));
    assertEquals("def", values.get("stringParamDefaultValue"));
    assertNull(values.get("stringArrayParam"));
    assertNull(values.get("integerParam"));
    assertEquals(22, values.get("integerParamDefaultValue"));
  }

}
