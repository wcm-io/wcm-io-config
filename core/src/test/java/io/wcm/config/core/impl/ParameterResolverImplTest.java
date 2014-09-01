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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Parameter;
import io.wcm.config.management.ParameterOverride;
import io.wcm.config.management.ParameterPersistence;
import io.wcm.config.spi.ParameterBuilder;
import io.wcm.config.spi.ParameterProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

@RunWith(MockitoJUnitRunner.class)
public class ParameterResolverImplTest {

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
  private static final Map<String, Object> PARAMETER_PROVIDER_1_PROPS = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 1L)
      .put(Constants.SERVICE_RANKING, 10)
      .build();
  @Mock
  private ParameterProvider parameterProvider2;
  private static final Map<String, Object> PARAMETER_PROVIDER_2_PROPS = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_ID, 2L)
      .put(Constants.SERVICE_RANKING, 20)
      .build();

  @InjectMocks
  private ParameterResolverImpl underTest;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    when(componentContext.getBundleContext()).thenReturn(bundleContext);
    when(bundleContext.getServiceReference("my.service")).thenReturn(serviceReference);
    when(serviceReference.getProperty("prop1")).thenReturn("valueFromOsgiConfig");

    when(parameterPersistence.getValues(anyString())).thenReturn(Collections.<String, Object>emptyMap());
    when(parameterOverride.getOverrideSystemDefault(any(Parameter.class))).thenReturn(null);
    when(parameterOverride.getOverrideForce(anyString(), any(Parameter.class))).thenReturn(null);

    Set<Parameter<?>> params1 = new HashSet<>();
    params1.add(ParameterBuilder.create("param11", String.class).build());
    params1.add(ParameterBuilder.create("param12", String.class).defaultValue("defValue").build());
    params1.add(ParameterBuilder.create("param13", String.class).defaultValue("defValue")
        .defaultOsgiConfigProperty("my.service:prop1").build());
    when(parameterProvider1.getParameters()).thenReturn(params1);

    Set<Parameter<?>> params2 = new HashSet<>();
    params2.add(ParameterBuilder.create("param21", Integer.class).defaultValue(55).build());
    when(parameterProvider2.getParameters()).thenReturn(params2);

    underTest.bindParameterProvider(parameterProvider1, PARAMETER_PROVIDER_1_PROPS);
    underTest.bindParameterProvider(parameterProvider2, PARAMETER_PROVIDER_2_PROPS);
  }

  @Test
  public void testDefaultValues() {
    Map<String, Object> values = underTest.getEffectiveValues(Arrays.asList("/config1"));
    assertNull(values.get("param11"));
    assertEquals("defValue", values.get("param12"));
    assertEquals("valueFromOsgiConfig", values.get("param13"));
    assertEquals(55, values.get("param21"));
  }

  // TODO: implement further unit tests

}
