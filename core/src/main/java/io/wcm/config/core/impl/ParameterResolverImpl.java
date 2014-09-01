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

import io.wcm.config.api.Parameter;
import io.wcm.config.core.util.SortedServices;
import io.wcm.config.management.ParameterOverride;
import io.wcm.config.management.ParameterPersistence;
import io.wcm.config.management.ParameterResolver;
import io.wcm.config.spi.ParameterProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.google.common.collect.Iterators;

/**
 * Default implementation of {@link ParameterResolver}.
 */
@Component(metatype = false, immediate = true)
@Service(ParameterResolver.class)
public class ParameterResolverImpl implements ParameterResolver {

  @Reference
  private ParameterPersistence parameterPersistence;

  @Reference
  private ParameterOverride parameterOverride;

  private BundleContext bundleContext;

  /**
   * Configuration finder strategies provided by installed applications.
   */
  @Reference(name = "parameterProvider", referenceInterface = ParameterProvider.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final SortedServices<ParameterProvider> parameterProviders = new SortedServices<>();

  @Override
  public Map<String, Object> getEffectiveValues(Collection<String> configurationIds) {
    Set<Parameter<?>> parameters = getAllParameters();
    Map<String, Object> parameterValues = new HashMap<>();

    // apply default values
    applyDefaultValues(parameters, parameterValues);
    applyOverrideSystemDefault(parameters, parameterValues);

    // apply configured values following inheritance hierarchy
    String[] configurationIdArray = Iterators.toArray(configurationIds.iterator(), String.class);
    for (int i = configurationIdArray.length - 1; i >= 0; i--) {
      String configurationId = configurationIdArray[i];
      applyConfiguredValues(configurationId, parameterValues);

      // apply forced override values
      applyOverrideForce(configurationId, parameters, parameterValues);
    }

    return parameterValues;
  }

  /**
   * Get all parameter definitions from all parameter providers.
   * @return Parameter definitions
   */
  private Set<Parameter<?>> getAllParameters() {
    Set<Parameter<?>> parameters = new TreeSet<>();
    for (ParameterProvider provider : this.parameterProviders.get()) {
      parameters.addAll(provider.getParameters());
    }
    return parameters;
  }

  /**
   * Apply default values for all parameters.
   * @param parameters Parameters
   * @param parameterValues Parameter values
   */
  private void applyDefaultValues(Set<Parameter<?>> parameters, Map<String, Object> parameterValues) {
    for (Parameter<?> parameter : parameters) {
      parameterValues.put(parameter.getName(), getParameterDefaultValue(parameter));
    }
  }

  /**
   * Get default value for parameter - from OSGi configuration property or parameter definition itself.
   * @param parameter Parameter definition
   * @return Default value or null
   */
  private Object getParameterDefaultValue(Parameter<?> parameter) {
    String defaultOsgiConfigProperty = parameter.getDefaultOsgiConfigProperty();
    if (StringUtils.isNotBlank(defaultOsgiConfigProperty)) {
      String[] parts = StringUtils.split(defaultOsgiConfigProperty, ":");
      String className = parts[0];
      String propertyName = parts[1];
      ServiceReference ref = bundleContext.getServiceReference(className);
      if (ref != null) {
        return ref.getProperty(propertyName);
      }
    }
    return parameter.getDefaultValue();
  }

  /**
   * Apply system-wide overrides for default values.
   * @param parameters Parameters
   * @param parameterValues Parameter values
   */
  private void applyOverrideSystemDefault(Set<Parameter<?>> parameters, Map<String, Object> parameterValues) {
    for (Parameter<?> parameter : parameters) {
      Object overrideValue = parameterOverride.getOverrideSystemDefault(parameter);
      if (overrideValue != null) {
        parameterValues.put(parameter.getName(), overrideValue);
      }
    }
  }

  /**
   * Apply configured values for given configuration id.
   * @param configurationId Configuration id
   * @param parameterValues Parameter values
   */
  private void applyConfiguredValues(String configurationId, Map<String, Object> parameterValues) {
    parameterValues.putAll(parameterPersistence.getValues(configurationId));
  }

  /**
   * Apply forced overrides for a configurationId.
   * @param configurationId Configuration id
   * @param parameters Parameters
   * @param parameterValues Parameter values
   */
  private void applyOverrideForce(String configurationId, Set<Parameter<?>> parameters, Map<String, Object> parameterValues) {
    for (Parameter<?> parameter : parameters) {
      Object overrideValue = parameterOverride.getOverrideForce(configurationId, parameter);
      if (overrideValue != null) {
        parameterValues.put(parameter.getName(), overrideValue);
      }
    }
  }

  @Activate
  protected void activate(final ComponentContext ctx) {
    bundleContext = ctx.getBundleContext();
  }

  protected void bindParameterProvider(ParameterProvider service, Map<String, Object> props) {
    parameterProviders.bind(service, props);
  }

  protected void unbindParameterProvider(ParameterProvider service, Map<String, Object> props) {
    parameterProviders.unbind(service, props);
  }

}
