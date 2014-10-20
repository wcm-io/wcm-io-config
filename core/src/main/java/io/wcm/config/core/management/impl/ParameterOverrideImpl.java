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
import io.wcm.config.core.management.ParameterOverride;
import io.wcm.config.core.management.util.TypeConversion;
import io.wcm.config.spi.ParameterOverrideProvider;
import io.wcm.sling.commons.osgi.RankedServices;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

/**
 * Default implementation of {@link ParameterOverride}.
 */
@Component(immediate = true, metatype = false)
@Service(ParameterOverride.class)
public final class ParameterOverrideImpl implements ParameterOverride {

  /**
   * Parameter override providers implemented by installed applications.
   */
  @Reference(name = "parameterOverrideProvider", referenceInterface = ParameterOverrideProvider.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final RankedServices<ParameterOverrideProvider> parameterOverrideProviders = new RankedServices<>();

  @Override
  public <T> T getOverrideSystemDefault(Parameter<T> parameter) {
    return getOverrideValue(DEFAULT_SCOPE, parameter);
  }

  @Override
  public <T> T getOverrideForce(String configurationId, Parameter<T> parameter) {
    // try to get override for explicit configuration
    T value = getOverrideValue(configurationId, parameter);
    if (value == null) {
      // try to get override for all configurations
      value = getOverrideValue(null, parameter);
    }
    return value;
  }

  private <T> T getOverrideValue(String scope, Parameter<T> parameter) {
    String key = (scope != null ? "[" + scope + "]" : "") + parameter.getName();
    for (ParameterOverrideProvider provider : parameterOverrideProviders) {
      Map<String, String> overrideMap = provider.getOverrideMap();
      String value = overrideMap.get(key);
      if (value != null) {
        return TypeConversion.stringToObject(value, parameter.getType());
      }
    }
    return null;
  }

  void bindParameterOverrideProvider(ParameterOverrideProvider service, Map<String, Object> props) {
    parameterOverrideProviders.bind(service, props);
  }

  void unbindParameterOverrideProvider(ParameterOverrideProvider service, Map<String, Object> props) {
    parameterOverrideProviders.unbind(service, props);
  }

}
