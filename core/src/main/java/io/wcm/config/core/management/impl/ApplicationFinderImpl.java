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

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.wcm.config.core.management.Application;
import io.wcm.config.core.management.ApplicationFinder;
import io.wcm.config.spi.ApplicationProvider;
import io.wcm.sling.commons.osgi.RankedServices;

/**
 * Default implementation of {@link ApplicationFinder}.
 */
@Component(immediate = true, metatype = false)
@Service(ApplicationFinder.class)
public final class ApplicationFinderImpl implements ApplicationFinder {

  private static final Application APPLICATION_NOT_FOUND = new Application("APPLICATION_NOT_FOUND", null);

  @Reference(name = "applicationProvider", referenceInterface = ApplicationProvider.class,
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  private final RankedServices<ApplicationProvider> applicationProviders = new RankedServices<>();

  // apply a simple cache mechanism for looking up application per resource path
  private final Cache<String, Application> applicationFindCache = CacheBuilder.newBuilder()
      .maximumSize(10000)
      .expireAfterWrite(10, TimeUnit.SECONDS)
      .build();

  @Override
  public Application find(final Resource resource) {
    try {
      Application result = applicationFindCache.get(resource.getPath(), new Callable<Application>() {
        @Override
        public Application call() {
          for (ApplicationProvider provider : applicationProviders) {
            if (provider.matches(resource)) {
              return new Application(provider.getApplicationId(), provider.getLabel());
            }
          }
          return APPLICATION_NOT_FOUND;
        }
      });
      if (result == APPLICATION_NOT_FOUND) {
        return null;
      }
      else {
        return result;
      }
    }
    catch (ExecutionException ex) {
      throw new RuntimeException("Error finding application.", ex.getCause());
    }
  }

  @Override
  public Set<Application> getAll() {
    SortedSet<Application> allApps = new TreeSet<>();
    for (ApplicationProvider provider : applicationProviders) {
      allApps.add(new Application(provider.getApplicationId(), provider.getLabel()));
    }
    return allApps;
  }

  void bindApplicationProvider(ApplicationProvider service, Map<String, Object> props) {
    applicationProviders.bind(service, props);
  }

  void unbindApplicationProvider(ApplicationProvider service, Map<String, Object> props) {
    applicationProviders.unbind(service, props);
  }

}
