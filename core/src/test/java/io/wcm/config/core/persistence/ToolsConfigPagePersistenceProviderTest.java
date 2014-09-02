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
package io.wcm.config.core.persistence;

import static io.wcm.config.core.persistence.ToolsConfigPagePersistenceProvider.CONFIG_PAGE_NAME;
import static io.wcm.config.core.persistence.ToolsConfigPagePersistenceProvider.CONFIG_RESOURCE_NAME;
import static io.wcm.config.core.persistence.ToolsConfigPagePersistenceProvider.TOOLS_PAGE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import io.wcm.config.management.PersistenceException;
import io.wcm.testing.mock.aem.junit.AemContext;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.component.ComponentContext;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

@RunWith(MockitoJUnitRunner.class)
public class ToolsConfigPagePersistenceProviderTest {

  private static final String CONFIG_ID = "/content/site1";
  private static final String CONFIG_PAGE_TEMPLATE = "/apps/dummy/templates/config";
  private static final String TOOLS_PAGE_TEMPLATE = "/apps/dummy/templates/tools";

  @Rule
  public final AemContext context = new AemContext();

  @Mock
  ComponentContext componentContext;

  ToolsConfigPagePersistenceProvider underTest;

  @Before
  public void setUp() throws WCMException {
    Page contentPage = context.pageManager().create("/", "content", TOOLS_PAGE_TEMPLATE, "content", true);
    context.pageManager().create(contentPage.getPath(), "site1", CONFIG_PAGE_TEMPLATE, "site1", true);

    Dictionary<String, Object> serviceConfig = new Hashtable<>();
    serviceConfig.put(ToolsConfigPagePersistenceProvider.PROPERTY_ENABLED, true);
    serviceConfig.put(ToolsConfigPagePersistenceProvider.PROPERTY_CONFIG_PAGE_TEMPLATE, CONFIG_PAGE_TEMPLATE);
    serviceConfig.put(ToolsConfigPagePersistenceProvider.PROPERTY_TOOLS_PAGE_TEMPLATE, TOOLS_PAGE_TEMPLATE);
    when(componentContext.getProperties()).thenReturn(serviceConfig);

    underTest = new ToolsConfigPagePersistenceProvider();
    underTest.activate(componentContext);
  }

  @Test
  public void testGetNoPage() {
    assertNull(underTest.get(context.resourceResolver(), CONFIG_ID));
  }

  @Test
  public void testGetPageNoConfigResource() throws WCMException {
    Page toolsPage = context.pageManager().create(CONFIG_ID, TOOLS_PAGE_NAME, TOOLS_PAGE_TEMPLATE, TOOLS_PAGE_NAME, true);
    context.pageManager().create(toolsPage.getPath(), CONFIG_PAGE_NAME, CONFIG_PAGE_TEMPLATE, CONFIG_PAGE_NAME, true);
    assertEquals(Collections.EMPTY_MAP, underTest.get(context.resourceResolver(), CONFIG_ID));
  }

  @Test
  public void testGetStoreGetValues() throws Exception {
    Page toolsPage = context.pageManager().create(CONFIG_ID, TOOLS_PAGE_NAME, TOOLS_PAGE_TEMPLATE, TOOLS_PAGE_NAME, true);
    Page configPage = context.pageManager().create(toolsPage.getPath(), CONFIG_PAGE_NAME, CONFIG_PAGE_TEMPLATE, CONFIG_PAGE_NAME, true);

    Map<String,Object> props = new HashMap<>();
    props.put("props1", "value1");
    props.put("props2", 55L);
    context.resourceResolver().create(configPage.getContentResource(), CONFIG_RESOURCE_NAME, props);
    context.resourceResolver().commit();

    assertEquals(props, new HashMap<String, Object>(underTest.get(context.resourceResolver(), CONFIG_ID)));

    Map<String, Object> newProps = new HashMap<>();
    props.put("props1", "value2");
    props.put("props3", "value3");
    assertTrue(underTest.store(context.resourceResolver(), CONFIG_ID, newProps));

    assertEquals(newProps, new HashMap<String, Object>(underTest.get(context.resourceResolver(), CONFIG_ID)));
  }

  @Test
  public void testNoPageStoreGetValues() throws Exception {
    Map<String, Object> props = new HashMap<>();
    props.put("props1", "value1");
    props.put("props2", 55L);
    underTest.store(context.resourceResolver(), CONFIG_ID, props);

    assertEquals(props, new HashMap<String, Object>(underTest.get(context.resourceResolver(), CONFIG_ID)));

    assertNotNull(context.pageManager().getPage(CONFIG_ID + ToolsConfigPagePersistenceProvider.RELATIVE_CONFIG_PATH));
  }

  @Test(expected = PersistenceException.class)
  public void testNoParentPageStore() throws Exception {
    Map<String, Object> props = new HashMap<>();
    props.put("props1", "value1");
    props.put("props2", 55L);
    assertTrue(underTest.store(context.resourceResolver(), "/content/site2", props));
  }

  @Test
  public void testDisabled() throws Exception {
    Dictionary<String, Object> serviceConfig = new Hashtable<>();
    serviceConfig.put(ToolsConfigPagePersistenceProvider.PROPERTY_ENABLED, false);
    when(componentContext.getProperties()).thenReturn(serviceConfig);
    underTest = new ToolsConfigPagePersistenceProvider();
    underTest.activate(componentContext);

    Page toolsPage = context.pageManager().create(CONFIG_ID, TOOLS_PAGE_NAME, TOOLS_PAGE_TEMPLATE, TOOLS_PAGE_NAME, true);
    Page configPage = context.pageManager().create(toolsPage.getPath(), CONFIG_PAGE_NAME, CONFIG_PAGE_TEMPLATE, CONFIG_PAGE_NAME, true);

    Map<String, Object> props = new HashMap<>();
    props.put("props1", "value1");
    props.put("props2", 55L);
    context.resourceResolver().create(configPage.getContentResource(), CONFIG_RESOURCE_NAME, props);
    context.resourceResolver().commit();

    assertNull(underTest.get(context.resourceResolver(), CONFIG_ID));
    assertFalse(underTest.store(context.resourceResolver(), CONFIG_ID, props));
  }

}
