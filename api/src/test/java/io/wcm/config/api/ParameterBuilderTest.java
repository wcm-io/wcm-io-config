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
package io.wcm.config.api;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class ParameterBuilderTest {

  @Test
  public void testBuilder() {
    Map<String, Object> props = new HashMap<>();
    props.put("prop1", "value1");

    Parameter<String> param = ParameterBuilder.create("param1", String.class)
        .applicationId("app1")
        .defaultOsgiConfigProperty("service:prop1")
        .defaultValue("defValue")
        .property("prop3", "value3")
        .properties(props)
        .property("prop2", "value2")
        .build();

    assertEquals("param1", param.getName());
    assertEquals(String.class, param.getType());
    assertEquals("app1", param.getApplicationId());
    assertEquals("service:prop1", param.getDefaultOsgiConfigProperty());
    assertEquals("defValue", param.getDefaultValue());
    assertEquals("value1", param.getProperties().get("prop1", String.class));
    assertEquals("value2", param.getProperties().get("prop2", String.class));
    assertEquals("value3", param.getProperties().get("prop3", String.class));
  }

  @Test
  public void testSort() {
    Set<Parameter> params = new TreeSet<>();
    params.add(ParameterBuilder.create("app5_param2", String.class).build());
    params.add(ParameterBuilder.create("app1_param2", String.class).build());
    params.add(ParameterBuilder.create("app5_param1", String.class).build());

    Parameter[] paramArray = params.toArray(new Parameter[params.size()]);
    assertEquals("app1_param2", paramArray[0].getName());
    assertEquals("app5_param1", paramArray[1].getName());
    assertEquals("app5_param2", paramArray[2].getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidName() {
    ParameterBuilder.create("param 1", String.class).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullName() {
    ParameterBuilder.create(null, String.class).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidType() {
    ParameterBuilder.create("param1", Date.class).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullType() {
    ParameterBuilder.create("param1", null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidApplicationId() {
    ParameterBuilder.create("param1", String.class).applicationId("app 1").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullApplicationId() {
    ParameterBuilder.create("param1", String.class).applicationId(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDefaultOsgiConfigProperty() {
    ParameterBuilder.create("param1", String.class).defaultOsgiConfigProperty("aaa").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefaultOsgiConfigProperty() {
    ParameterBuilder.create("param1", String.class).defaultOsgiConfigProperty(null).build();
  }

}
