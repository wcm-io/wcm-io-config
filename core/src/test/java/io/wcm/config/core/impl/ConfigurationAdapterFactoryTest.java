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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import io.wcm.config.api.Configuration;
import io.wcm.config.management.ConfigurationFinder;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationAdapterFactoryTest {

  @Mock
  private Resource resource;
  @Mock
  private Configuration configuration;
  @Mock
  private ConfigurationFinder configurationFinder;

  @InjectMocks
  private ConfigurationAdapterFactory underTest;

  @Before
  public void setUp() {
    when(configurationFinder.find(resource)).thenReturn(configuration);
  }

  @Test
  public void testAdapt() {
    assertSame(configuration, underTest.getAdapter(resource, Configuration.class));
    assertNull(underTest.getAdapter(resource, ConfigurationFinder.class));
    assertNull(underTest.getAdapter(this, Configuration.class));
  }

}
