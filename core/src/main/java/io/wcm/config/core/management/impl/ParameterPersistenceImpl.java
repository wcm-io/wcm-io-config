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

import io.wcm.config.api.management.ParameterPersistence;
import io.wcm.config.api.management.PersistenceException;
import io.wcm.config.spi.ParameterPersistenceProvider;
import io.wcm.sling.commons.osgi.RankedServices;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Default implementation of {@link ParameterPersistence}.
 */
@Component(metatype = false, immediate = true)
@Service(ParameterPersistence.class)
public final class ParameterPersistenceImpl implements ParameterPersistence {

  /**
   * Parameter providers implemented by installed applications.
   */
  @Reference(name = "parameterPersistenceProvider", referenceInterface = ParameterPersistenceProvider.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final RankedServices<ParameterPersistenceProvider> parameterPersistenceProviders = new RankedServices<>();

  @Override
  public Map<String, Object> getValues(ResourceResolver resolver, String configurationId) {
    // get values from first configuration provider that can provide them
    for (ParameterPersistenceProvider provider : parameterPersistenceProviders) {
      Map<String, Object> values = provider.get(resolver, configurationId);
      if (values != null) {
        return values;
      }
    }
    return Collections.emptyMap();
  }

  @Override
  public void storeParameterValues(ResourceResolver resolver, String configurationId, Map<String, Object> values)
      throws PersistenceException {
    storeParameterValues(resolver, configurationId, values, false);
  }

  @Override
  public void storeParameterValues(ResourceResolver resolver, String configurationId, Map<String, Object> values,
      boolean mergeWithExisting) throws PersistenceException {

    // merge values with existing if requested
    Map<String, Object> valuesToStore;
    if (mergeWithExisting) {
      valuesToStore = new HashMap<>();
      valuesToStore.putAll(getValues(resolver, configurationId));
      valuesToStore.putAll(values);
    }
    else {
      valuesToStore = values;
    }

    // ask providers to store the parameter values
    for (ParameterPersistenceProvider provider : parameterPersistenceProviders) {
      if (provider.store(resolver, configurationId, valuesToStore)) {
        return;
      }
    }
    throw new PersistenceException("No provider accepted to store parameter values for " + configurationId);
  }

  void bindParameterPersistenceProvider(ParameterPersistenceProvider service, Map<String, Object> props) {
    parameterPersistenceProviders.bind(service, props);
  }

  void unbindParameterPersistenceProvider(ParameterPersistenceProvider service, Map<String, Object> props) {
    parameterPersistenceProviders.unbind(service, props);
  }

}
