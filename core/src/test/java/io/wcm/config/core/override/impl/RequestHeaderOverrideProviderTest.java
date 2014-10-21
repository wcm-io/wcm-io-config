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
package io.wcm.config.core.override.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import io.wcm.sling.commons.request.impl.RequestContextFilter;
import io.wcm.testing.mock.aem.junit.AemContext;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.component.ComponentContext;

@RunWith(MockitoJUnitRunner.class)
public class RequestHeaderOverrideProviderTest {

  @Rule
  public AemContext context = new AemContext();

  @Mock
  private ComponentContext componentContext;
  @Mock
  private Dictionary config;
  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private SlingHttpServletResponse response;

  private RequestContextFilter requestContextFilter;

  @Before
  public void setUp() {
    when(componentContext.getProperties()).thenReturn(config);
    Vector<String> headerNames = new Vector<>();
    headerNames.add(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[default]param1");
    headerNames.add(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[/config1]param2");
    when(request.getHeaderNames()).thenReturn(headerNames.elements());
    when(request.getHeader(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[default]param1")).thenReturn("value1");
    when(request.getHeader(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[/config1]param2")).thenReturn("value2");
    requestContextFilter = context.registerInjectActivateService(new RequestContextFilter());
  }

  @Test
  public void testEnabled() throws IOException, ServletException {
    final RequestHeaderOverrideProvider provider = context.registerInjectActivateService(new RequestHeaderOverrideProvider());
    when(config.get(RequestHeaderOverrideProvider.PROPERTY_ENABLED)).thenReturn(true);
    provider.activate(componentContext);

    requestContextFilter.doFilter(request, response, new FilterChain() {
      @Override
      public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
        Map<String, String> overrideMap = provider.getOverrideMap();
        assertEquals("value1", overrideMap.get("[default]param1"));
        assertEquals("value2", overrideMap.get("[/config1]param2"));
      }
    });
  }

  @Test
  public void testDisabled() throws IOException, ServletException {
    final RequestHeaderOverrideProvider provider = context.registerInjectActivateService(new RequestHeaderOverrideProvider());
    when(config.get(RequestHeaderOverrideProvider.PROPERTY_ENABLED)).thenReturn(false);
    provider.activate(componentContext);

    requestContextFilter.doFilter(request, response, new FilterChain() {
      @Override
      public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
        Map<String, String> overrideMap = provider.getOverrideMap();
        assertTrue(overrideMap.isEmpty());
      }
    });
  }

}
