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

import io.wcm.config.api.Configuration;
import io.wcm.config.api.Parameter;
import io.wcm.config.core.management.ConfigurationFinder;
import io.wcm.config.core.management.ParameterPersistence;
import io.wcm.config.core.management.ParameterPersistenceData;
import io.wcm.config.core.management.ParameterResolver;
import io.wcm.config.core.util.TypeConversion;
import io.wcm.wcm.commons.contenttype.FileExtension;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Persists configuration parameters
 */
@SlingServlet(
    resourceTypes = {
        "/apps/wcm-io/config/editor/components/page/editor"
    },
    extensions = FileExtension.JSON,
    selectors = "configProvider",
    methods = HttpConstants.METHOD_POST)
public class EditorParameterPersistence extends SlingAllMethodsServlet {

  @Reference
  private ConfigurationFinder configurationFinder;
  @Reference
  private ParameterPersistence persistence;
  @Reference
  private ParameterResolver parameterResolver;

  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
    sanityCheck(request, response);

    String configurationId = getCurrentConfigurationId(request);
    if (StringUtils.isEmpty(configurationId)) {
      response.sendError(500, "Could not find configuration id for resource " + request.getResource().getPath());
    }

    try {
      persistence.storeData(request.getResourceResolver(), configurationId, getPersistenceData(request), false);
    }
    catch (PersistenceException ex) {
      response.sendError(500);
    }
  }

  protected ParameterPersistenceData getPersistenceData(SlingHttpServletRequest request) {
    Map<String, Parameter<?>> parameters = getParametersMap(parameterResolver.getAllParameters());
    Enumeration<String> requestParameterNames = request.getParameterNames();
    Map<String, Object> values = new HashMap<>();
    SortedSet<String> lockedParameterNames = ImmutableSortedSet.<String>of();
    while (requestParameterNames.hasMoreElements()) {
      String parameterName = requestParameterNames.nextElement();
      Parameter<?> parameter = parameters.get(parameterName);
      if (parameter != null) {
        Object value = getValue(request.getParameterValues(parameterName), parameter);
        if (value != null) {
          values.put(parameterName, value);
        }
      }
      else if (StringUtils.equals(ParameterPersistence.PN_LOCKED_PARAMETER_NAMES, parameterName)) {
        lockedParameterNames = getLockedParameterNames(request.getParameterValues(parameterName));
      }
    }
    return new ParameterPersistenceData(values, lockedParameterNames);
  }

  private Map<String, Parameter<?>> getParametersMap(Set<Parameter<?>> allParameters) {
    Map<String, Parameter<?>> result = new HashMap<>();
    Iterator<Parameter<?>> iterator = allParameters.iterator();
    while (iterator.hasNext()) {
      Parameter parameter = iterator.next();
      result.put(parameter.getName(), parameter);
    }
    return result;
  }

  private SortedSet<String> getLockedParameterNames(
      String[] lockedParameterValues) {
    if (lockedParameterValues != null && lockedParameterValues.length > 0) {
      String[] namesArray = lockedParameterValues[0].split(TypeConversion.ARRAY_DELIMITER);
      return ImmutableSortedSet.copyOf(namesArray);
    }
    return ImmutableSortedSet.<String>of();
  }

  private Object getValue(String[] values, Parameter<?> parameter) {
    Object value = null;
    if (values != null && values.length > 0) {
      value = TypeConversion.stringToObject(values[0], parameter.getType());
    }
    return value;
  }

  private void sanityCheck(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    if (configurationFinder == null || persistence == null) {
      response.sendError(500, "Configuration services are not available");
    }
  }

  private String getCurrentConfigurationId(SlingHttpServletRequest request) {
    if (configurationFinder != null) {
      Resource resource = request.getResource();
      Configuration configuration = configurationFinder.find(resource);
      return configuration != null ? configuration.getConfigurationId() : StringUtils.EMPTY;
    }

    return StringUtils.EMPTY;

  }
}
