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
import io.wcm.config.spi.ParameterOverrideProvider;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

/**
 * Default implementation of {@link ParameterOverride}.
 */
@Component(metatype = false, immediate = true)
@Service(ParameterOverride.class)
public final class ParameterOverrideImpl implements ParameterOverride {

  /**
   * Parameter override providers implemented by installed applications.
   */
  @Reference(name = "parameterOverrideProvider", referenceInterface = ParameterOverrideProvider.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final SortedServices<ParameterOverrideProvider> parameterOverrideProviders = new SortedServices<>();

  @Override
  public <T> T getOverrideSystemDefault(Parameter<T> parameter) {
    return getOverrideValue(DEFAULT_SCOPE, parameter);
  }

  @Override
  public <T> T getOverrideForce(String configurationId, Parameter<T> parameter) {
    return getOverrideValue(configurationId, parameter);
  }

  private <T> T getOverrideValue(String scope, Parameter<T> parameter) {
    String key = "[" + scope + "]" + parameter.getName();
    for (ParameterOverrideProvider provider : parameterOverrideProviders.get()) {
      Map<String, String> overrideMap = provider.getOverrideMap();
      String value = overrideMap.get(key.toString());
      if (value != null) {
        return toType(value, parameter.getType());
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T> T toType(String value, Class<T> type) {
    if (type == String.class) {
      return (T)value;
    }
    else if (type == String[].class) {
      return (T)StringUtils.splitPreserveAllTokens(value, ARRAY_DELIMITER);
    }
    if (type == Integer.class) {
      return (T)(Integer)NumberUtils.toInt(value, 0);
    }
    if (type == Long.class) {
      return (T)(Long)NumberUtils.toLong(value, 0L);
    }
    if (type == Double.class) {
      return (T)(Double)NumberUtils.toDouble(value, 0d);
    }
    if (type == Boolean.class) {
      return (T)(Boolean)BooleanUtils.toBoolean(value);
    }
    if (type == Map.class) {
      String[] rows = StringUtils.splitPreserveAllTokens(value, ARRAY_DELIMITER);
      Map<String, String> map = new LinkedHashMap<>();
      for (int i = 0; i < rows.length; i++) {
        String[] keyValue = StringUtils.splitPreserveAllTokens(rows[i], KEY_VALUE_DELIMITER);
        if (keyValue.length == 2 && StringUtils.isNotEmpty(keyValue[0])) {
          map.put(keyValue[0], keyValue[1]);
        }
      }
      return (T)map;
    }
    return null;
  }

  protected void bindParameterOverrideProvider(ParameterOverrideProvider service, Map<String, Object> props) {
    parameterOverrideProviders.bind(service, props);
  }

  protected void unbindParameterOverrideProvider(ParameterOverrideProvider service, Map<String, Object> props) {
    parameterOverrideProviders.unbind(service, props);
  }

}
