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
package io.wcm.config.spi;

import static org.junit.Assert.assertEquals;
import io.wcm.config.api.Parameter;
import io.wcm.config.api.Visibility;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ParameterBuilderTest {

  @Test
  public void testBuilder() {
    Map<String, Object> props = new HashMap<>();
    props.put("prop1", "value1");

    Parameter<String> param = ParameterBuilder.create("param1", String.class)
        .applicationId("app1")
        .visibility(Visibility.FRONTEND)
        .defaultOsgiConfigProperty("service:prop1")
        .defaultValue("defValue")
        .property("prop3", "value3")
        .properties(props)
        .property("prop2", "value2")
        .build();

    assertEquals("param1", param.getName());
    assertEquals(String.class, param.getType());
    assertEquals("app1", param.getApplicationId());
    assertEquals(Visibility.FRONTEND, param.getVisibility());
    assertEquals("service:prop1", param.getDefaultOsgiConfigProperty());
    assertEquals("defValue", param.getDefaultValue());
    assertEquals("value1", param.getProperties().get("prop1", String.class));
    assertEquals("value2", param.getProperties().get("prop2", String.class));
    assertEquals("value3", param.getProperties().get("prop3", String.class));
  }

}
