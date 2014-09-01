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
import io.wcm.config.core.util.SortedServices;
import io.wcm.config.management.ParameterOverride;
import io.wcm.config.management.ParameterPersistence;
import io.wcm.config.management.ParameterResolver;
import io.wcm.config.spi.ParameterProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.google.common.collect.Iterators;

/**
 * Default implementation of {@link ParameterResolver}.
 */
@Component(metatype = false, immediate = true)
@Service(ParameterResolver.class)
public final class ParameterResolverImpl implements ParameterResolver {

  private static final String KEY_VALUE_DELIMITER = "=";

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
  private final SortedServices<ParameterProvider> parameterProviders = new SortedServices<>();

  @Override
  public Map<String, Object> getEffectiveValues(Collection<String> configurationIds) {
    Map<String, Parameter<?>> parameters = getAllParameters();
    Map<String, Object> parameterValues = new HashMap<>();

    // apply default values
    applyDefaultValues(parameters, parameterValues);
    applyOverrideSystemDefault(parameters, parameterValues);

    // apply configured values following inheritance hierarchy
    String[] configurationIdArray = Iterators.toArray(configurationIds.iterator(), String.class);
    for (int i = configurationIdArray.length - 1; i >= 0; i--) {
      String configurationId = configurationIdArray[i];
      applyConfiguredValues(configurationId, parameters, parameterValues);

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
    for (ParameterProvider provider : this.parameterProviders.get()) {
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
  private Object getParameterDefaultValue(Parameter<?> parameter) {
    String defaultOsgiConfigProperty = parameter.getDefaultOsgiConfigProperty();
    if (StringUtils.isNotBlank(defaultOsgiConfigProperty)) {
      String[] parts = StringUtils.split(defaultOsgiConfigProperty, ":");
      String className = parts[0];
      String propertyName = parts[1];
      ServiceReference ref = bundleContext.getServiceReference(className);
      if (ref != null) {
        Object value = ref.getProperty(propertyName);
        // only selected parameter types are supported
        if (parameter.getType() == String.class) {
          return PropertiesUtil.toString(value, (String)parameter.getDefaultValue());
        }
        if (parameter.getType() == String[].class) {
          return PropertiesUtil.toStringArray(value, (String[])parameter.getDefaultValue());
        }
        else if (parameter.getType() == Integer.class) {
          Integer defaultValue = (Integer)parameter.getDefaultValue();
          if (defaultValue == null) {
            defaultValue = 0;
          }
          return PropertiesUtil.toInteger(value, defaultValue);
        }
        else if (parameter.getType() == Long.class) {
          Long defaultValue = (Long)parameter.getDefaultValue();
          if (defaultValue == null) {
            defaultValue = 0L;
          }
          return PropertiesUtil.toLong(value, defaultValue);
        }
        else if (parameter.getType() == Double.class) {
          Double defaultValue = (Double)parameter.getDefaultValue();
          if (defaultValue == null) {
            defaultValue = 0d;
          }
          return PropertiesUtil.toDouble(value, defaultValue);
        }
        else if (parameter.getType() == Boolean.class || parameter.getType() == boolean.class) {
          Boolean defaultValue = (Boolean)parameter.getDefaultValue();
          if (defaultValue == null) {
            defaultValue = false;
          }
          return PropertiesUtil.toBoolean(value, defaultValue);
        }
        else if (parameter.getType() == Map.class) {
          Map<?, ?> defaultMap = (Map)parameter.getDefaultValue();
          String[] defaultValue;
          if (defaultMap == null) {
            defaultValue = new String[0];
          }
          else {
            defaultValue = new String[defaultMap.size()];
            Map.Entry<?, ?>[] entries = Iterators.toArray(defaultMap.entrySet().iterator(), Map.Entry.class);
            for (int i = 0; i < entries.length; i++) {
              defaultValue[i] = ObjectUtils.toString(entries[i].getKey()) + KEY_VALUE_DELIMITER + ObjectUtils.toString(entries[i].getValue());
            }
          }
          return PropertiesUtil.toMap(value, defaultValue);
        }
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
   * Apply configured values for given configuration id.
   * @param configurationId Configuration id
   * @param parameterValues Parameter values
   */
  private void applyConfiguredValues(String configurationId, Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues) {
    Map<String, Object> configuredValues = new HashMap<>(parameterPersistence.getValues(configurationId));
    ensureValidValueTypes(parameters, configuredValues);
    parameterValues.putAll(configuredValues);
  }

  /**
   * Make sure value types match with declared parameter types. Values which types do not match, or for which no
   * parameter definition exists are removed.
   * @param parameters Parameter definitions
   * @param parameterValues Parameter values
   */
  private void ensureValidValueTypes(Map<String, Parameter<?>> parameters, Map<String, Object> parameterValues) {
    Iterator<Map.Entry<String, Object>> valueIterator = parameterValues.entrySet().iterator();
    while (valueIterator.hasNext()) {
      Map.Entry<String,Object> value = valueIterator.next();
      if (value.getKey() == null || value.getValue() == null) {
        valueIterator.remove();
      }
      else {
        Parameter<?> parameter = parameters.get(value.getKey());
        if (parameter == null || !parameter.getType().isAssignableFrom(value.getValue().getClass())) {
          valueIterator.remove();
        }
      }
    }
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
