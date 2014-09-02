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

import io.wcm.config.spi.ParameterOverrideProvider;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

/**
 * Provide parameter override map from current request header.
 */
@Component(metatype = true, immediate = true)
@Service({
  ParameterOverrideProvider.class, Filter.class
})
public final class RequestHeaderOverrideProvider implements ParameterOverrideProvider, Filter {

  /**
   * Prefix for override request header
   */
  public static final String REQUEST_HEADER_PREFIX = "config.override.";

  private static final ThreadLocal<Map<String, String>> OVERRIDE_MAP_THREADLOCAL = new ThreadLocal<Map<String, String>>() {
    @Override
    protected Map<String, String> initialValue() {
      return new HashMap<String, String>();
    }
  };

  @Property(label = "Enabled", boolValue = RequestHeaderOverrideProvider.DEFAULT_ENABLED,
      description = "Enable parameter override provider")
  private static final String PROPERTY_ENABLED = "enabled";
  private static final boolean DEFAULT_ENABLED = false;

  @Property(label = "Service Ranking", intValue = RequestHeaderOverrideProvider.DEFAULT_RANKING,
      description = "Priority of parameter override providers (lower = higher priority)")
  private static final String PROPERTY_RANKING = Constants.SERVICE_RANKING;
  private static final int DEFAULT_RANKING = 1000;

  private boolean enabled;

  @Override
  public Map<String, String> getOverrideMap() {
    return OVERRIDE_MAP_THREADLOCAL.get();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (this.enabled) {
      // build override map from request headers
      if (request instanceof HttpServletRequest) {
        Map<String, String> map = OVERRIDE_MAP_THREADLOCAL.get();
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        Enumeration keys = httpRequest.getHeaderNames();
        while (keys.hasMoreElements()) {
          Object keyObject = keys.nextElement();
          if (keyObject instanceof String) {
            String key = (String)keyObject;
            if (StringUtils.startsWith(key, REQUEST_HEADER_PREFIX)) {
              map.put(StringUtils.substringAfter(key, REQUEST_HEADER_PREFIX), httpRequest.getHeader(key));
            }
          }
        }
      }
    }
    try {
      chain.doFilter(request, response);
    }
    finally {
      if (this.enabled) {
        // clear override map
        OVERRIDE_MAP_THREADLOCAL.get().clear();
      }
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do
  }

  @Override
  public void destroy() {
    // nothing to do
  }

  @Activate
  void activate(final ComponentContext ctx) {
    Dictionary config = ctx.getProperties();
    this.enabled = PropertiesUtil.toBoolean(config.get(PROPERTY_ENABLED), DEFAULT_ENABLED);
  }

}
