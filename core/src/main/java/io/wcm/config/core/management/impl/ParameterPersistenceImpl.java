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
import io.wcm.config.api.management.ParameterPersistenceData;
import io.wcm.config.api.management.PersistenceException;
import io.wcm.config.spi.ParameterPersistenceProvider;
import io.wcm.sling.commons.osgi.RankedServices;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;

import com.google.common.collect.ImmutableSortedSet;

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
  public ParameterPersistenceData getData(ResourceResolver resolver, String configurationId) {
    // get values from first configuration provider that can provide them
    for (ParameterPersistenceProvider provider : parameterPersistenceProviders) {
      Map<String, Object> values = provider.get(resolver, configurationId);
      if (values != null) {
        return toData(values);
      }
    }
    return ParameterPersistenceData.EMPTY;
  }

  private ParameterPersistenceData toData(Map<String,Object> valuesFromProvider) {
    String[] lockedParameterNamesArray = (String[])valuesFromProvider.get(PN_LOCKED_PARAMETER_NAMES);
    if (lockedParameterNamesArray != null) {
      Map<String, Object> values = new HashMap<>(valuesFromProvider);
      values.remove(PN_LOCKED_PARAMETER_NAMES);
      return new ParameterPersistenceData(values, ImmutableSortedSet.copyOf(lockedParameterNamesArray));
    }
    else {
      return new ParameterPersistenceData(valuesFromProvider, ImmutableSortedSet.<String>of());
    }
  }

  @Override
  public void storeData(ResourceResolver resolver, String configurationId, ParameterPersistenceData data)
      throws PersistenceException {
    storeData(resolver, configurationId, data, false);
  }

  @Override
  public void storeData(ResourceResolver resolver, String configurationId, ParameterPersistenceData data,
      boolean mergeWithExisting) throws PersistenceException {

    // merge values with existing if requested
    Map<String, Object> valuesToStore = new HashMap<>();
    if (mergeWithExisting) {
      // values
      ParameterPersistenceData existingData = getData(resolver, configurationId);
      valuesToStore.putAll(existingData.getValues());
      valuesToStore.putAll(data.getValues());

      // locked parameter names
      SortedSet<String> lockedParameterNames = new TreeSet<>();
      lockedParameterNames.addAll(existingData.getLockedParameterNames());
      lockedParameterNames.addAll(data.getLockedParameterNames());
      if (!lockedParameterNames.isEmpty()) {
        valuesToStore.put(PN_LOCKED_PARAMETER_NAMES, toArray(lockedParameterNames));
      }
    }
    else {
      // values
      valuesToStore.putAll(data.getValues());

      // locked parameter names
      if (!data.getLockedParameterNames().isEmpty()) {
        valuesToStore.put(PN_LOCKED_PARAMETER_NAMES, toArray(data.getLockedParameterNames()));
      }
    }

    // ask providers to store the parameter values
    for (ParameterPersistenceProvider provider : parameterPersistenceProviders) {
      if (provider.store(resolver, configurationId, valuesToStore)) {
        return;
      }
    }
    throw new PersistenceException("No provider accepted to store parameter values for " + configurationId);
  }

  private String[] toArray(Set<String> set) {
    return set.toArray(new String[set.size()]);
  }

  void bindParameterPersistenceProvider(ParameterPersistenceProvider service, Map<String, Object> props) {
    parameterPersistenceProviders.bind(service, props);
  }

  void unbindParameterPersistenceProvider(ParameterPersistenceProvider service, Map<String, Object> props) {
    parameterPersistenceProviders.unbind(service, props);
  }

}
