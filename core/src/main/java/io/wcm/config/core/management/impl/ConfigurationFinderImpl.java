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

import io.wcm.config.api.Configuration;
import io.wcm.config.core.impl.ConfigurationImpl;
import io.wcm.config.core.util.SortedServices;
import io.wcm.config.management.ConfigurationFinder;
import io.wcm.config.management.ParameterResolver;
import io.wcm.config.spi.ConfigurationFinderStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;

/**
 * Default implementation of {@link ConfigurationFinder}.
 */
@Component(metatype = false, immediate = true)
@Service(ConfigurationFinder.class)
public final class ConfigurationFinderImpl implements ConfigurationFinder {

  /**
   * Ordering of configuration id by "closed match" - is simply a descending alphanumeric sort.
   */
  private static final Comparator<String> CONFIGURATION_ID_CLOSED_MATCH_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      return o2.compareTo(o1);
    }
  };

  /**
   * Configuration finder strategies provided by installed applications.
   */
  @Reference(name = "configurationFinderStrategy", referenceInterface = ConfigurationFinderStrategy.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final SortedServices<ConfigurationFinderStrategy> finderStrategies = new SortedServices<>();

  @Reference
  private ParameterResolver parameterResolver;

  @Override
  public Configuration find(Resource resource) {
    return find(resource, null);
  }

  @Override
  public Configuration find(Resource resource, String applicationId) {
    Set<String> allIds = getAllMatchingConfigurationIds(resource, applicationId);
    return readConfiguration(allIds);
  }

  @Override
  public Iterator<Configuration> findAll(Resource resource) {
    return findAll(resource, null);
  }

  @Override
  public Iterator<Configuration> findAll(Resource resource, String applicationId) {
    List<Configuration> configurations = new ArrayList<>();
    List<String> allIds = new LinkedList<String>(getAllMatchingConfigurationIds(resource, applicationId));
    while (!allIds.isEmpty()) {
      configurations.add(readConfiguration(allIds));
      allIds.remove(0);
    }
    return configurations.iterator();
  }

  private Set<String> getAllMatchingConfigurationIds(Resource resource, String applicationId) {
    Set<String> allIds = new TreeSet<>(CONFIGURATION_ID_CLOSED_MATCH_COMPARATOR);
    for (ConfigurationFinderStrategy finderStrategy : finderStrategies.get()) {
      if (matchesApplicationId(applicationId, finderStrategy.getApplicationId())) {
        Iterator<String> configIds = finderStrategy.findConfigurationIds(resource);
        while (configIds.hasNext()) {
          allIds.add(configIds.next());
        }
      }
    }
    return allIds;
  }

  private boolean matchesApplicationId(String expected, String actual) {
    if (expected == null) {
      return true;
    }
    else {
      return StringUtils.equals(expected, actual);
    }
  }

  private Configuration readConfiguration(Collection<String> configurationIds) {
    if (configurationIds.isEmpty()) {
      return null;
    }
    String topmostConfigurationId = configurationIds.iterator().next();
    Map<String, Object> values = this.parameterResolver.getEffectiveValues(configurationIds);
    return new ConfigurationImpl(topmostConfigurationId, values);
  }

  protected void bindConfigurationFinderStrategy(ConfigurationFinderStrategy service, Map<String, Object> props) {
    finderStrategies.bind(service, props);
  }

  protected void unbindConfigurationFinderStrategy(ConfigurationFinderStrategy service, Map<String, Object> props) {
    finderStrategies.unbind(service, props);
  }

}
