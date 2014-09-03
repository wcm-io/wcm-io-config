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
package io.wcm.config.core.util;

import static io.wcm.config.core.util.TypeUtil.overridePropertyToType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class TypeUtilOverridePropertyTest {

  @Test
  public void testOverridePropertyToType_String() {
    assertEquals("value", overridePropertyToType("value", String.class));
    assertNull(overridePropertyToType(null, String.class));
  }

  @Test
  public void testOverridePropertyToType_StringArray() {
    assertArrayEquals(new String[] {
        "value"
    }, overridePropertyToType("value", String[].class));
    assertArrayEquals(new String[] {
        "value1",
        "value2"
    }, overridePropertyToType("value1;value2", String[].class));
    assertArrayEquals(new String[] {
        "value1",
        "value2",
        ""
    }, overridePropertyToType("value1;value2;", String[].class));
    assertNull(overridePropertyToType(null, String[].class));
  }

  @Test
  public void testOverridePropertyToType_Integer() {
    assertEquals((Integer)55, overridePropertyToType("55", Integer.class));
    assertEquals((Integer)0, overridePropertyToType("wurst", Integer.class));
    assertNull(overridePropertyToType(null, Integer.class));
  }

  @Test
  public void testOverridePropertyToType_Long() {
    assertEquals((Long)55L, overridePropertyToType("55", Long.class));
    assertEquals((Long)0L, overridePropertyToType("wurst", Long.class));
    assertNull(overridePropertyToType(null, Long.class));
  }

  @Test
  public void testOverridePropertyToType_Double() {
    assertEquals((Double)55d, overridePropertyToType("55", Double.class));
    assertEquals((Double)55.123d, overridePropertyToType("55.123", Double.class));
    assertEquals((Double)0d, overridePropertyToType("wurst", Double.class));
    assertNull(overridePropertyToType(null, Double.class));
  }

  @Test
  public void testOverridePropertyToType_Boolean() {
    assertTrue(overridePropertyToType("true", Boolean.class));
    assertFalse(overridePropertyToType("wurst", Boolean.class));
    assertNull(overridePropertyToType(null, Boolean.class));
  }

  @Test
  public void testOverridePropertyToType_Map() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("key1", "abc");
    map.put("key2", "def");
    map.put("key3", null);
    assertEquals(map, overridePropertyToType("key1=abc;key2=def;key3=;;=xyz", Map.class));

    assertNull(overridePropertyToType(null, Map.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOverridePropertyToType_IllegalType() {
    overridePropertyToType("value", Date.class);
  }

}
