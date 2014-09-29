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

import static io.wcm.config.core.util.TypeConversion.osgiPropertyToObject;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class TypeConversionOsgiPropertyTest {

  @Test
  public void testString() {
    assertEquals("value", osgiPropertyToObject("value", String.class, null));
    assertNull(osgiPropertyToObject(null, String.class, null));
    assertEquals("defValue", osgiPropertyToObject(null, String.class, "defValue"));
  }

  @Test
  public void testStringArray() {
    assertArrayEquals(new String[] {
        "value"
    }, osgiPropertyToObject("value", String[].class, null));
    assertArrayEquals(new String[] {
        "value1",
        "value2"
    }, osgiPropertyToObject(new Object[] {
        "value1", "value2"
    }, String[].class, null));
    assertArrayEquals(new String[] {
        "value1",
        "value2",
        ""
    }, osgiPropertyToObject(new Object[] {
        "value1",
        "value2",
        ""
    }, String[].class, null));
    assertNull(osgiPropertyToObject(null, String[].class, null));
    assertArrayEquals(new String[] {
        "defValue"
    }, osgiPropertyToObject(null, String[].class, new String[] {
      "defValue"
    }));
  }

  @Test
  public void testInteger() {
    assertEquals((Integer)55, osgiPropertyToObject(55, Integer.class, null));
    assertEquals((Integer)55, osgiPropertyToObject(55L, Integer.class, null));
    assertEquals((Integer)55, osgiPropertyToObject("55", Integer.class, null));
    assertEquals((Integer)0, osgiPropertyToObject("wurst", Integer.class, null));
    assertEquals((Integer)66, osgiPropertyToObject("wurst", Integer.class, 66));
    assertEquals((Integer)0, osgiPropertyToObject(null, Integer.class, null));
    assertEquals((Integer)66, osgiPropertyToObject(null, Integer.class, 66));
  }

  @Test
  public void testLong() {
    assertEquals((Long)55L, osgiPropertyToObject(55, Long.class, null));
    assertEquals((Long)55L, osgiPropertyToObject(55L, Long.class, null));
    assertEquals((Long)55L, osgiPropertyToObject("55", Long.class, null));
    assertEquals((Long)0L, osgiPropertyToObject("wurst", Long.class, null));
    assertEquals((Long)66L, osgiPropertyToObject("wurst", Long.class, 66L));
    assertEquals((Long)0L, osgiPropertyToObject(null, Long.class, null));
    assertEquals((Long)66L, osgiPropertyToObject(null, Long.class, 66L));
  }

  @Test
  public void testDouble() {
    assertEquals(55d, osgiPropertyToObject(55, Double.class, null), 0.0001d);
    assertEquals(55d, osgiPropertyToObject(55L, Double.class, null), 0.0001d);
    assertEquals(55d, osgiPropertyToObject(55d, Double.class, null), 0.0001d);
    assertEquals(55.123d, osgiPropertyToObject(55.123d, Double.class, null), 0.0001d);
    assertEquals(55d, osgiPropertyToObject("55", Double.class, null), 0.0001d);
    assertEquals(0d, osgiPropertyToObject("wurst", Double.class, null), 0.0001d);
    assertEquals(66.123d, osgiPropertyToObject("wurst", Double.class, 66.123d), 0.0001d);
    assertEquals(0d, osgiPropertyToObject(null, Double.class, null), 0.0001d);
    assertEquals(66.123d, osgiPropertyToObject(null, Double.class, 66.123d), 0.0001d);
  }

  @Test
  public void testBoolean() {
    assertTrue(osgiPropertyToObject(true, Boolean.class, null));
    assertTrue(osgiPropertyToObject("true", Boolean.class, null));
    assertFalse(osgiPropertyToObject("wurst", Boolean.class, null));
    assertFalse(osgiPropertyToObject("wurst", Boolean.class, true));
    assertFalse(osgiPropertyToObject(null, Boolean.class, null));
    assertTrue(osgiPropertyToObject(null, Boolean.class, true));
  }

  @Test
  public void testMap() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("key1", "abc");
    map.put("key2", "def");
    map.put("key3", null);
    assertEquals(map, osgiPropertyToObject(new String[] {
        "key1=abc", "key2=def", "key3=", "", "=xyz"
    }, Map.class, null));

    map = new LinkedHashMap<>();
    assertEquals(map, osgiPropertyToObject(null, Map.class, null));

    map = new LinkedHashMap<>();
    map.put("key1", "abc");
    assertEquals(map, osgiPropertyToObject(null, Map.class, map));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalType() {
    osgiPropertyToObject("value", Date.class, null);
  }

}
