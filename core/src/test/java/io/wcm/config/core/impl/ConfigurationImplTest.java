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

import static org.junit.Assert.assertEquals;
import io.wcm.config.api.Configuration;
import io.wcm.config.api.Parameter;
import io.wcm.config.spi.ParameterBuilder;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ConfigurationImplTest {

  private static final Map<String, Object> SAMPLE_PROPS = new HashMap<>();
  static {
    SAMPLE_PROPS.put("prop1", "value1");
    SAMPLE_PROPS.put("prop2", 55);
  }

  private static final Parameter<String> PARAM1 = ParameterBuilder.create("prop1", String.class).build();
  private static final Parameter<Integer> PARAM2 = ParameterBuilder.create("prop2", Integer.class).build();

  @Test
  public void testProperties() {
    Configuration config = new ConfigurationImpl("conf1", SAMPLE_PROPS);
    assertEquals("conf1", config.getConfigurationId());
    assertEquals("value1", config.get("prop1", String.class));
    assertEquals((Integer)55, config.get("prop2", Integer.class));
    assertEquals("value1", config.get(PARAM1));
    assertEquals((Integer)55, config.get(PARAM2));
  }

}
