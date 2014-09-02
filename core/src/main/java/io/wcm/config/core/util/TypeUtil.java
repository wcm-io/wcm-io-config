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


import static io.wcm.config.management.ParameterOverride.ARRAY_DELIMITER;
import static io.wcm.config.management.ParameterOverride.KEY_VALUE_DELIMITER;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.google.common.collect.Iterators;

/**
 * Helps converting types from object to parameter type.
 */
public final class TypeUtil {

  private TypeUtil() {
    // static methods only
  }

  /**
   * Converts a string value from an override string to the parameter type.
   * @param value String value
   * @param type Parameter type
   * @return Converted value
   * @throws IllegalArgumentException If type is not supported
   */
  @SuppressWarnings("unchecked")
  public static <T> T overridePropertyToType(String value, Class<T> type) {
    if (value == null) {
      return null;
    }
    if (type == String.class) {
      return (T)value;
    }
    else if (type == String[].class) {
      return (T)StringUtils.splitPreserveAllTokens(value, ARRAY_DELIMITER);
    }
    if (type == Integer.class) {
      return (T)(Integer)NumberUtils.toInt(value, 0);
    }
    if (type == Long.class) {
      return (T)(Long)NumberUtils.toLong(value, 0L);
    }
    if (type == Double.class) {
      return (T)(Double)NumberUtils.toDouble(value, 0d);
    }
    if (type == Boolean.class) {
      return (T)(Boolean)BooleanUtils.toBoolean(value);
    }
    if (type == Map.class) {
      String[] rows = StringUtils.splitPreserveAllTokens(value, ARRAY_DELIMITER);
      Map<String, String> map = new LinkedHashMap<>();
      for (int i = 0; i < rows.length; i++) {
        String[] keyValue = StringUtils.splitPreserveAllTokens(rows[i], KEY_VALUE_DELIMITER);
        if (keyValue.length == 2 && StringUtils.isNotEmpty(keyValue[0])) {
          map.put(keyValue[0], StringUtils.isEmpty(keyValue[1]) ? null : keyValue[1]);
        }
      }
      return (T)map;
    }
    throw new IllegalArgumentException("Unsupported type: " + type.getName());
  }

  /**
   * Converts a OSGi configuration property value to the parameter type.
   * @param value String value (not null)
   * @param type Parameter type
   * @param defaultValue Default value is used if not OSGi configuration value is set
   * @return Converted value
   * @throws IllegalArgumentException If type is not supported
   */
  @SuppressWarnings("unchecked")
  public static <T> T osgiPropertyToType(Object value, Class<T> type, T defaultValue) {
    // only selected parameter types are supported
    if (type == String.class) {
      return (T)PropertiesUtil.toString(value, (String)defaultValue);
    }
    if (type == String[].class) {
      return (T)PropertiesUtil.toStringArray(value, (String[])defaultValue);
    }
    else if (type == Integer.class) {
      Integer defaultIntValue = (Integer)defaultValue;
      if (defaultIntValue == null) {
        defaultIntValue = 0;
      }
      return (T)(Integer)PropertiesUtil.toInteger(value, defaultIntValue);
    }
    else if (type == Long.class) {
      Long defaultLongValue = (Long)defaultValue;
      if (defaultLongValue == null) {
        defaultLongValue = 0L;
      }
      return (T)(Long)PropertiesUtil.toLong(value, defaultLongValue);
    }
    else if (type == Double.class) {
      Double defaultDoubleValue = (Double)defaultValue;
      if (defaultDoubleValue == null) {
        defaultDoubleValue = 0d;
      }
      return (T)(Double)PropertiesUtil.toDouble(value, defaultDoubleValue);
    }
    else if (type == Boolean.class) {
      Boolean defaultBooleanValue = (Boolean)defaultValue;
      if (defaultBooleanValue == null) {
        defaultBooleanValue = false;
      }
      return (T)(Boolean)PropertiesUtil.toBoolean(value, defaultBooleanValue);
    }
    else if (type == Map.class) {
      Map<?, ?> defaultMap = (Map)defaultValue;
      String[] defaultMapValue;
      if (defaultMap == null) {
        defaultMapValue = new String[0];
      }
      else {
        defaultMapValue = new String[defaultMap.size()];
        Map.Entry<?, ?>[] entries = Iterators.toArray(defaultMap.entrySet().iterator(), Map.Entry.class);
        for (int i = 0; i < entries.length; i++) {
          defaultMapValue[i] = ObjectUtils.toString(entries[i].getKey())
              + KEY_VALUE_DELIMITER + ObjectUtils.toString(entries[i].getValue());
        }
      }
      return (T)PropertiesUtil.toMap(value, defaultMapValue);
    }
    throw new IllegalArgumentException("Unsupported type: " + type.getName());
  }

}