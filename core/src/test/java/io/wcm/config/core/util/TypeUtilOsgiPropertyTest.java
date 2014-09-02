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

import static io.wcm.config.core.util.TypeUtil.osgiPropertyToType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class TypeUtilOsgiPropertyTest {

  @Test
  public void testOsgiPropertyToType_String() {
    assertEquals("value", osgiPropertyToType("value", String.class, null));
    assertNull(osgiPropertyToType(null, String.class, null));
    assertEquals("defValue", osgiPropertyToType(null, String.class, "defValue"));
  }

  @Test
  public void testOsgiPropertyToType_StringArray() {
    assertArrayEquals(new String[] {
        "value"
    }, osgiPropertyToType("value", String[].class, null));
    assertArrayEquals(new String[] {
        "value1",
        "value2"
    }, osgiPropertyToType(new Object[] {
        "value1", "value2"
    }, String[].class, null));
    assertArrayEquals(new String[] {
        "value1",
        "value2",
        ""
    }, osgiPropertyToType(new Object[] {
        "value1",
        "value2",
        ""
    }, String[].class, null));
    assertNull(osgiPropertyToType(null, String[].class, null));
    assertArrayEquals(new String[] {
        "defValue"
    }, osgiPropertyToType(null, String[].class, new String[] {
      "defValue"
    }));
  }

  @Test
  public void testOsgiPropertyToType_Integer() {
    assertEquals((Integer)55, osgiPropertyToType(55, Integer.class, null));
    assertEquals((Integer)55, osgiPropertyToType(55L, Integer.class, null));
    assertEquals((Integer)55, osgiPropertyToType("55", Integer.class, null));
    assertEquals((Integer)0, osgiPropertyToType("wurst", Integer.class, null));
    assertEquals((Integer)66, osgiPropertyToType("wurst", Integer.class, 66));
    assertEquals((Integer)0, osgiPropertyToType(null, Integer.class, null));
    assertEquals((Integer)66, osgiPropertyToType(null, Integer.class, 66));
  }

  @Test
  public void testOsgiPropertyToType_Long() {
    assertEquals((Long)55L, osgiPropertyToType(55, Long.class, null));
    assertEquals((Long)55L, osgiPropertyToType(55L, Long.class, null));
    assertEquals((Long)55L, osgiPropertyToType("55", Long.class, null));
    assertEquals((Long)0L, osgiPropertyToType("wurst", Long.class, null));
    assertEquals((Long)66L, osgiPropertyToType("wurst", Long.class, 66L));
    assertEquals((Long)0L, osgiPropertyToType(null, Long.class, null));
    assertEquals((Long)66L, osgiPropertyToType(null, Long.class, 66L));
  }

  @Test
  public void testOsgiPropertyToType_Double() {
    assertEquals(55d, osgiPropertyToType(55, Double.class, null), 0.0001d);
    assertEquals(55d, osgiPropertyToType(55L, Double.class, null), 0.0001d);
    assertEquals(55d, osgiPropertyToType(55d, Double.class, null), 0.0001d);
    assertEquals(55.123d, osgiPropertyToType(55.123d, Double.class, null), 0.0001d);
    assertEquals(55d, osgiPropertyToType("55", Double.class, null), 0.0001d);
    assertEquals(0d, osgiPropertyToType("wurst", Double.class, null), 0.0001d);
    assertEquals(66.123d, osgiPropertyToType("wurst", Double.class, 66.123d), 0.0001d);
    assertEquals(0d, osgiPropertyToType(null, Double.class, null), 0.0001d);
    assertEquals(66.123d, osgiPropertyToType(null, Double.class, 66.123d), 0.0001d);
  }

  @Test
  public void testOsgiPropertyToType_Boolean() {
    assertTrue(osgiPropertyToType(true, Boolean.class, null));
    assertTrue(osgiPropertyToType("true", Boolean.class, null));
    assertFalse(osgiPropertyToType("wurst", Boolean.class, true));
    assertFalse(osgiPropertyToType(null, Boolean.class, null));
    assertTrue(osgiPropertyToType(null, Boolean.class, true));
  }

  @Test
  public void testOsgiPropertyToType_Map() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("key1", "abc");
    map.put("key2", "def");
    map.put("key3", null);
    assertEquals(map, osgiPropertyToType(new String[] {
        "key1=abc", "key2=def", "key3=", "", "=xyz"
    }, Map.class, null));

    map = new LinkedHashMap<>();
    assertEquals(map, osgiPropertyToType(null, Map.class, null));

    map = new LinkedHashMap<>();
    map.put("key1", "abc");
    assertEquals(map, osgiPropertyToType(null, Map.class, map));
  }

}
