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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Configuration;
import io.wcm.config.api.Parameter;
import io.wcm.config.api.ParameterBuilder;
import io.wcm.config.core.management.ApplicationFinder;
import io.wcm.config.core.management.ConfigurationFinder;
import io.wcm.config.core.management.ParameterPersistence;
import io.wcm.config.core.management.ParameterPersistenceData;
import io.wcm.config.core.management.ParameterResolver;
import io.wcm.config.editor.EditorNameConstants;
import io.wcm.config.editor.WidgetTypes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;

/**
 * Tests for the {@link EditorParameterProvider}
 */
@RunWith(MockitoJUnitRunner.class)
public class EditorParameterProviderTest {

  @Mock
  private ConfigurationFinder configurationFinder;

  @Mock
  private ApplicationFinder applicationFinder;

  @Mock
  private ParameterPersistence persistence;

  @Mock
  private ParameterResolver parameterResolver;
  private static final String APP_ID = "/app/test";
  private static final Parameter<String> NON_EDITABLE_PARAMETER = ParameterBuilder.create("string-param", String.class, APP_ID)
      .defaultValue("defaultValue").build();
  private static final Parameter<String> EDITABLE_PARAMETER_ONE = ParameterBuilder.create("string-param-1", String.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).defaultValue("defaultValue").build();
  private static final Parameter<String> EDITABLE_PARAMETER_TWO = ParameterBuilder.create("string-param-2", String.class, APP_ID)
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration()).defaultValue("defaultValue2").build();
  private static final Set<Parameter<?>> PARAMETERS = ImmutableSet.<Parameter<?>>of(EDITABLE_PARAMETER_ONE, EDITABLE_PARAMETER_TWO, NON_EDITABLE_PARAMETER);

  @Mock
  private Configuration configurationFirstLevel;
  @Mock
  private Configuration configurationSecondLevel;

  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private SlingHttpServletResponse response;
  @Mock
  private PrintWriter printWriter;

  @Mock
  private Resource siteResource;
  @Mock
  private Resource regionResource;

  @InjectMocks
  private EditorParameterProvider underTest;

  private JSONObject firstParameter;
  private JSONObject secondParameter;


  @Before
  public void setUp() throws IOException {
    when(parameterResolver.getAllParameters()).thenReturn(PARAMETERS);
    when(configurationFirstLevel.getConfigurationId()).thenReturn("/content/site");
    when(configurationFirstLevel.get(EDITABLE_PARAMETER_ONE.getName())).thenReturn(EDITABLE_PARAMETER_ONE.getDefaultValue());
    when(configurationFirstLevel.get(EDITABLE_PARAMETER_TWO.getName())).thenReturn(EDITABLE_PARAMETER_TWO.getDefaultValue());

    when(configurationSecondLevel.getConfigurationId()).thenReturn("/content/site/region");
    when(configurationSecondLevel.get(EDITABLE_PARAMETER_ONE.getName())).thenReturn(EDITABLE_PARAMETER_ONE.getDefaultValue());
    when(configurationSecondLevel.get(EDITABLE_PARAMETER_TWO.getName())).thenReturn("newValue");
    when(persistence.getData(any(ResourceResolver.class), eq("/content/site"))).then(new Answer<ParameterPersistenceData>() {

      @Override
      public ParameterPersistenceData answer(InvocationOnMock invocation) {
        Map<String, Object> values = new HashMap<>();
        SortedSet<String> lockedName = ImmutableSortedSet.<String>of("string-param-1");
        return new ParameterPersistenceData(values, lockedName);
      }
    });

    when(persistence.getData(any(ResourceResolver.class), eq("/content/site/region"))).then(new Answer<ParameterPersistenceData>() {
      @Override
      public ParameterPersistenceData answer(InvocationOnMock invocation) {
        Map<String, Object> values = new HashMap<>();
        values.put(EDITABLE_PARAMETER_TWO.getName(), "newValue");
        SortedSet<String> lockedName = ImmutableSortedSet.<String>of();
        return new ParameterPersistenceData(values, lockedName);
      }
    });

    when(configurationFinder.findAll(regionResource)).thenReturn(Iterators.forArray(configurationSecondLevel, configurationFirstLevel));
    when(configurationFinder.findAll(siteResource)).thenReturn(Iterators.forArray(configurationFirstLevel));

    doAnswer(new Answer<Void>() {

      @Override
      public Void answer(InvocationOnMock invocation) {
        String jsonString = (String)invocation.getArguments()[0];
        try {
          JSONObject result = new JSONObject(jsonString);
          JSONArray parameters = result.getJSONArray("parameters");
          firstParameter = parameters.getJSONObject(0);
          secondParameter = parameters.getJSONObject(1);
        }
        catch (JSONException ex) {
          firstParameter = new JSONObject();
          secondParameter = new JSONObject();
        }
        return null;
      }
    }).when(printWriter).write(anyString());
    when(response.getWriter()).thenReturn(printWriter);

    when(request.getResource()).thenReturn(regionResource);
  }

  @Test
  public void testReturnOnlyEditableParameters() throws JSONException {
    JSONArray parameters = new JSONArray();
    when(persistence.getData(any(ResourceResolver.class), anyString())).thenReturn(
        new ParameterPersistenceData(ImmutableMap.<String, Object>of(), ImmutableSortedSet.<String>of()));
    underTest.addParameters(parameters, configurationFirstLevel, request);
    assertEquals(2, parameters.length());
  }

  @Test
  public void testInheritedProperty() throws ServletException, IOException, JSONException {
    underTest.doGet(request, response);
    assertEquals(firstParameter.get(EditorNameConstants.PN_INHERITED), true);
    assertEquals(secondParameter.get(EditorNameConstants.PN_INHERITED), false);
  }

  @Test
  public void testLockedProperty() throws ServletException, IOException, JSONException {
    underTest.doGet(request, response);
    assertEquals(firstParameter.get(EditorNameConstants.PN_LOCKED), true);
    assertEquals(firstParameter.get(EditorNameConstants.PN_LOCKED_INHERITED), true);
    assertEquals(secondParameter.get(EditorNameConstants.PN_LOCKED), false);
    assertEquals(secondParameter.get(EditorNameConstants.PN_LOCKED_INHERITED), false);
  }

  @Test
  public void testValuesProperty() throws ServletException, IOException, JSONException {
    underTest.doGet(request, response);
    assertEquals(firstParameter.get(EditorNameConstants.PN_PARAMETER_VALUE), "defaultValue");
    assertEquals(firstParameter.get(EditorNameConstants.PN_INHERITED_VALUE), "defaultValue");
    assertEquals(secondParameter.get(EditorNameConstants.PN_PARAMETER_VALUE), "newValue");
    assertEquals(secondParameter.get(EditorNameConstants.PN_INHERITED_VALUE), "defaultValue2");

    when(request.getResource()).thenReturn(siteResource);
    underTest.doGet(request, response);
    assertEquals(firstParameter.get(EditorNameConstants.PN_PARAMETER_VALUE), "defaultValue");
    assertEquals(firstParameter.get(EditorNameConstants.PN_INHERITED_VALUE), "defaultValue");
    assertEquals(secondParameter.get(EditorNameConstants.PN_PARAMETER_VALUE), "defaultValue2");
    assertEquals(secondParameter.get(EditorNameConstants.PN_INHERITED_VALUE), "defaultValue2");
  }

}
