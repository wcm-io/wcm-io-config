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

import io.wcm.config.api.Parameter;
import io.wcm.config.api.management.ParameterOverride;
import io.wcm.config.api.management.ParameterPersistence;
import io.wcm.config.api.management.ParameterPersistenceData;
import io.wcm.config.api.management.ParameterResolver;
import io.wcm.config.core.util.TypeConversion;
import io.wcm.config.spi.ParameterProvider;
import io.wcm.sling.commons.osgi.RankedServices;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;

/**
 * Default implementation of {@link ParameterResolver}.
 */
@Component(immediate = true, metatype = false)
@Service(ParameterResolver.class)
public final class ParameterResolverImpl implements ParameterResolver {

  private static final Logger log = LoggerFactory.getLogger(ParameterResolverImpl.class);

  @Reference
  private ParameterPersistence parameterPersistence;

  @Reference
  private ParameterOverride parameterOverride;

  private BundleContext bundleContext;

  /**
   * Parameter providers implemented by installed applications.
   */
  @Reference(name = "parameterProvider", referenceInterface = ParameterProvider.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final RankedServices<ParameterProvider> parameterProviders = new RankedServices<>();

  @Override
  public Map<String, Object> getEffectiveValues(ResourceResolver resolver, Collection<String> configurationIds) {
    Map<String, Parameter<?>> parameters = getAllParameters();
    Map<String, Object> parameterValues = new HashMap<>();

    // apply default values
    applyDefaultValues(parameters, parameterValues);
    applyOverrideSystemDefault(parameters, parameterValues);

    // apply configured values following inheritance hierarchy
    String[] configurationIdArray = Iterators.toArray(configurationIds.iterator(), String.class);
    SortedSet<String> lockedParameterNames = ImmutableSortedSet.<String>of();
    for (int i = configurationIdArray.length - 1; i >= 0; i--) {
      String configurationId = configurationIdArray[i];
      lockedParameterNames = applyConfiguredValues(resolver, configurationId, parameters, parameterValues, lockedParameterNames);

      // apply forced override values
      applyOverrideForce(configurationId, parameters, parameterValues);
    }

    return parameterValues;
  }

  /**
   * Get all parameter definitions from all parameter providers.
   * @return Parameter definitions (key = name, value = definition)
   */
  private Map<String, Parameter<?>> getAllParameters() {
    Set<Parameter<?>> parameters = new HashSet<>();
    for (ParameterProvider provider : this.parameterProviders) {
      parameters.addAll(provider.getParameters());
    }
    Map<String, Parameter<?>> parameterMap = new TreeMap<>();
    for (Parameter<?> parameter : parameters) {
      parameterMap.put(parameter.getName(), parameter);
    }
    return parameterMap;
  }

  /**
   * Apply default values for all parameters.
   * @param parameters Parameters
   * @param parameterValues Parameter values
   */
  private void applyDefaultValues(Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues) {
    for (Parameter<?> parameter : parameters.values()) {
      parameterValues.put(parameter.getName(), getParameterDefaultValue(parameter));
    }
  }

  /**
   * Get default value for parameter - from OSGi configuration property or parameter definition itself.
   * @param parameter Parameter definition
   * @return Default value or null
   */
  private <T> T getParameterDefaultValue(Parameter<T> parameter) {
    String defaultOsgiConfigProperty = parameter.getDefaultOsgiConfigProperty();
    if (StringUtils.isNotBlank(defaultOsgiConfigProperty)) {
      String[] parts = StringUtils.split(defaultOsgiConfigProperty, ":");
      String className = parts[0];
      String propertyName = parts[1];
      ServiceReference ref = bundleContext.getServiceReference(className);
      if (ref != null) {
        Object value = ref.getProperty(propertyName);
        return TypeConversion.osgiPropertyToObject(value, parameter.getType(), parameter.getDefaultValue());
      }
    }
    return parameter.getDefaultValue();
  }

  /**
   * Apply system-wide overrides for default values.
   * @param parameters Parameters
   * @param parameterValues Parameter values
   */
  private void applyOverrideSystemDefault(Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues) {
    for (Parameter<?> parameter : parameters.values()) {
      Object overrideValue = parameterOverride.getOverrideSystemDefault(parameter);
      if (overrideValue != null) {
        parameterValues.put(parameter.getName(), overrideValue);
      }
    }
  }

  /**
   * Apply configured values for given configuration id (except those for which the parameter names are locked on a
   * higher configuration level).
   * @param resolver Resource resolver
   * @param configurationId Configuration id
   * @param parameters Parameter definitions
   * @param parameterValues Parameter values
   * @param ancestorLockedParameterNames Set of locked parameter names on the configuration levels above.
   * @return Set of locked parameter names on this configuration level combined with the from the levels above.
   */
  private SortedSet<String> applyConfiguredValues(ResourceResolver resolver, String configurationId,
      Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues,
      SortedSet<String> ancestorLockedParameterNames) {

    // get data from persistence
    ParameterPersistenceData data = parameterPersistence.getData(resolver, configurationId);

    // ensure the types provided by persistence are valid
    Map<String, Object> configuredValues = ensureValidValueTypes(parameters, data.getValues());

    // put parameter values to map (respect locked parameter names that may be defined on ancestor level)
    if (!ancestorLockedParameterNames.isEmpty()) {
      for (Map.Entry<String, Object> entry : configuredValues.entrySet()) {
        if (!ancestorLockedParameterNames.contains(entry.getKey())) {
          parameterValues.put(entry.getKey(), entry.getValue());
        }
      }
    }
    else {
      parameterValues.putAll(configuredValues);
    }

    // aggregate set of locked parameter names from ancestor levels and this level
    SortedSet<String> lockedParameterNames = ancestorLockedParameterNames;
    if (!data.getLockedParameterNames().isEmpty()) {
      lockedParameterNames = new TreeSet<>();
      lockedParameterNames.addAll(ancestorLockedParameterNames);
      lockedParameterNames.addAll(data.getLockedParameterNames());
    }
    return lockedParameterNames;
  }

  /**
   * Make sure value types match with declared parameter types. Values which types do not match, or for which no
   * parameter definition exists are removed. Types are converted from persistence format if required.
   * @param parameters Parameter definitions
   * @param parameterValues Parameter values
   * @return Cleaned up parameter values
   */
  private Map<String, Object> ensureValidValueTypes(Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues) {
    Map<String, Object> transformedParameterValues = new HashMap<>();
    for (Map.Entry<String, Object> entry : parameterValues.entrySet()) {
      if (entry.getKey() == null || entry.getValue() == null) {
        continue;
      }
      else {
        Parameter<?> parameter = parameters.get(entry.getKey());
        if (parameter == null) {
          continue;
        }
        else {
          Object transformedValue = PersistenceTypeConversion.fromPersistenceType(entry.getValue(), parameter.getType());
          if (!parameter.getType().isAssignableFrom(transformedValue.getClass())) {
            continue;
          }
          transformedParameterValues.put(entry.getKey(), transformedValue);
        }
      }
    }
    return transformedParameterValues;
  }

  /**
   * Apply forced overrides for a configurationId.
   * @param configurationId Configuration id
   * @param parameters Parameters
   * @param parameterValues Parameter values
   */
  private void applyOverrideForce(String configurationId, Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues) {
    for (Parameter<?> parameter : parameters.values()) {
      Object overrideValue = parameterOverride.getOverrideForce(configurationId, parameter);
      if (overrideValue != null) {
        parameterValues.put(parameter.getName(), overrideValue);
      }
    }
  }

  /**
   * Validate that application ids and configuration names are unique over all providers.
   */
  private void validateParameterProviders() {
    Set<String> applicationIds = new HashSet<>();
    Set<String> parameterNames = new HashSet<>();
    for (ParameterProvider provider : this.parameterProviders) {
      Set<String> applicationIdsOfThisProvider = new HashSet<>();
      for (Parameter<?> parameter : provider.getParameters()) {
        if (StringUtils.isNotEmpty(parameter.getApplicationId())) {
          applicationIdsOfThisProvider.add(parameter.getApplicationId());
        }
        if (parameterNames.contains(parameter.getName())) {
          log.warn("Parameter name is not unique: {} (application: {})", parameter.getName(), parameter.getApplicationId());
        }
        else {
          parameterNames.add(parameter.getName());
        }
      }
      if (applicationIdsOfThisProvider.size() > 1) {
        log.warn("Parameter provider {} defines parameters with multiple application Ids: {}", provider,
            applicationIdsOfThisProvider.toArray(new String[applicationIdsOfThisProvider.size()]));
      }
      else if (applicationIdsOfThisProvider.size() == 1) {
        String applicationId = applicationIdsOfThisProvider.iterator().next();
        if (applicationIds.contains(applicationId)) {
          log.warn("Parameter provider application id is not unique: {}", applicationId);
        }
        else {
          applicationIds.add(applicationId);
        }
      }
    }
  }

  @Activate
  void activate(final ComponentContext ctx) {
    bundleContext = ctx.getBundleContext();
  }

  void bindParameterProvider(ParameterProvider service, Map<String, Object> props) {
    parameterProviders.bind(service, props);
    validateParameterProviders();
  }

  void unbindParameterProvider(ParameterProvider service, Map<String, Object> props) {
    parameterProviders.unbind(service, props);
  }

}
