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
package io.wcm.config.core.override;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.component.ComponentContext;

@RunWith(MockitoJUnitRunner.class)
public class RequestHeaderOverrideProviderTest {

  @Mock
  private ComponentContext componentContext;
  @Mock
  private Dictionary config;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;

  @Before
  public void setUp() {
    when(componentContext.getProperties()).thenReturn(config);
    Vector<String> headerNames = new Vector<>();
    headerNames.add(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[default]param1");
    headerNames.add(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[/config1]param2");
    when(request.getHeaderNames()).thenReturn(headerNames.elements());
    when(request.getHeader(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[default]param1")).thenReturn("value1");
    when(request.getHeader(RequestHeaderOverrideProvider.REQUEST_HEADER_PREFIX + "[/config1]param2")).thenReturn("value2");
  }

  @Test
  public void testEnabled() throws IOException, ServletException {
    final RequestHeaderOverrideProvider provider = new RequestHeaderOverrideProvider();
    when(config.get(RequestHeaderOverrideProvider.PROPERTY_ENABLED)).thenReturn(true);
    provider.activate(componentContext);

    provider.doFilter(request, response, new FilterChain() {
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
    final RequestHeaderOverrideProvider provider = new RequestHeaderOverrideProvider();
    when(config.get(RequestHeaderOverrideProvider.PROPERTY_ENABLED)).thenReturn(false);
    provider.activate(componentContext);

    provider.doFilter(request, response, new FilterChain() {
      @Override
      public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
        Map<String, String> overrideMap = provider.getOverrideMap();
        assertNull(overrideMap.get("[default]param1"));
        assertNull(overrideMap.get("[/config1]param2"));
      }
    });
  }

}
