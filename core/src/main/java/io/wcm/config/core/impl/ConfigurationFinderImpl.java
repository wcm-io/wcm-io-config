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

import io.wcm.config.api.Configuration;
import io.wcm.config.management.ConfigurationFinder;

import java.util.Iterator;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ValueMap;

/**
 * Default implementation of {@link ConfigurationFinder}.
 */
@Component(metatype = false, immediate = true)
@Service(ConfigurationFinder.class)
public class ConfigurationFinderImpl implements ConfigurationFinder {

  @Override
  public Configuration find(String path) {
    // TODO: sseifert@sseifert replace with real implementation
    return new ConfigurationImpl(path, ValueMap.EMPTY);
  }

  @Override
  public Configuration find(String path, String applicationId) {
    // TODO: sseifert@sseifert implement
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public Iterator<Configuration> findAll(String path) {
    // TODO: sseifert@sseifert implement
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public Iterator<Configuration> findAll(String path, String applicationId) {
    // TODO: sseifert@sseifert implement
    throw new UnsupportedOperationException("Not implemented yet.");
  }

}
