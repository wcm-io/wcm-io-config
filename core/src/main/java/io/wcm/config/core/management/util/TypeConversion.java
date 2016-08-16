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
package io.wcm.config.core.management.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.annotation.versioning.ProviderType;

import com.google.common.collect.Iterators;

/**
 * Helps converting types from object to parameter type.
 */
@ProviderType
public final class TypeConversion {

  private static final char ARRAY_DELIMITER_CHAR = ';';
  private static final char KEY_VALUE_DELIMITER_CHAR = '=';
  private static final char ESCAPE_DELIMITER_CHAR = '\\';

  /**
   * Delimiter for string array values an map rows
   */
  public static final String ARRAY_DELIMITER = Character.toString(ARRAY_DELIMITER_CHAR);

  /**
   * Delimiter to separate key/value pairs in map rows
   */
  public static final String KEY_VALUE_DELIMITER = Character.toString(KEY_VALUE_DELIMITER_CHAR);

  /**
   * Delimiter to prefix escaped characters.
   */
  public static final String ESCAPE_DELIMITER = Character.toString(ESCAPE_DELIMITER_CHAR);

  private TypeConversion() {
    // static methods only
  }

  /**
   * Converts a string value to an object with the given parameter type.
   * @param value String value
   * @param type Parameter type
   * @return Converted value
   * @throws IllegalArgumentException If type is not supported
   */
  @SuppressWarnings("unchecked")
  public static <T> T stringToObject(String value, Class<T> type) {
    if (value == null) {
      return null;
    }
    if (type == String.class) {
      return (T)value;
    }
    else if (type == String[].class) {
      return (T)decodeString(splitPreserveAllTokens(value, ARRAY_DELIMITER_CHAR));
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
      String[] rows = splitPreserveAllTokens(value, ARRAY_DELIMITER_CHAR);
      Map<String, String> map = new LinkedHashMap<>();
      for (int i = 0; i < rows.length; i++) {
        String[] keyValue = splitPreserveAllTokens(rows[i], KEY_VALUE_DELIMITER_CHAR);
        if (keyValue.length == 2 && StringUtils.isNotEmpty(keyValue[0])) {
          map.put(decodeString(keyValue[0]), StringUtils.isEmpty(keyValue[1]) ? null : decodeString(keyValue[1]));
        }
      }
      return (T)map;
    }
    throw new IllegalArgumentException("Unsupported type: " + type.getName());
  }

  /**
   * Converts a typed value to it's string representation.
   * @param value Typed value
   * @return String value
   */
  public static String objectToString(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      return (String)value;
    }
    if (value instanceof String[]) {
      return StringUtils.join(encodeString((String[])value), ARRAY_DELIMITER);
    }
    else if (value instanceof Integer) {
      return Integer.toString((Integer)value);
    }
    else if (value instanceof Long) {
      return Long.toString((Long)value);
    }
    else if (value instanceof Double) {
      return Double.toString((Double)value);
    }
    else if (value instanceof Boolean) {
      return Boolean.toString((Boolean)value);
    }
    else if (value instanceof Map) {
      Map<?, ?> map = (Map<?, ?>)value;
      StringBuilder stringValue = new StringBuilder();
      Map.Entry<?, ?>[] entries = Iterators.toArray(map.entrySet().iterator(), Map.Entry.class);
      for (int i = 0; i < entries.length; i++) {
        Map.Entry<?, ?> entry = entries[i];
        String entryKey = encodeString(Objects.toString(entry.getKey(), ""));
        String entryValue = encodeString(Objects.toString(entry.getValue(), ""));
        stringValue.append(entryKey).append(KEY_VALUE_DELIMITER).append(entryValue);
        if (i < entries.length - 1) {
          stringValue.append(ARRAY_DELIMITER);
        }
      }
      return stringValue.toString();
    }
    throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
  }

  /**
   * Converts a OSGi configuration property value to an object with the given parameter type.
   * @param value String value (not null)
   * @param type Parameter type
   * @param defaultValue Default value is used if not OSGi configuration value is set
   * @return Converted value
   * @throws IllegalArgumentException If type is not supported
   */
  @SuppressWarnings("unchecked")
  public static <T> T osgiPropertyToObject(Object value, Class<T> type, T defaultValue) {
    // only selected parameter types are supported
    if (type == String.class) {
      return (T)PropertiesUtil.toString(value, (String)defaultValue);
    }
    if (type == String[].class) {
      return (T)decodeString(PropertiesUtil.toStringArray(value, (String[])defaultValue));
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
          defaultMapValue[i] = encodeString(Objects.toString(entries[i].getKey(), ""))
              + KEY_VALUE_DELIMITER + encodeString(Objects.toString(entries[i].getValue(), ""));
        }
      }
      String[] rawMap = PropertiesUtil.toStringArray(value, defaultMapValue);
      Map<String, String> finalMap = new HashMap<String, String>(rawMap.length);
      for (int i = 0; i < rawMap.length; i++) {
        String[] keyValue = splitPreserveAllTokens(rawMap[i], KEY_VALUE_DELIMITER_CHAR);
        if (keyValue.length == 2 && StringUtils.isNotEmpty(keyValue[0])) {
          finalMap.put(decodeString(keyValue[0]), StringUtils.isEmpty(keyValue[1]) ? null : decodeString(keyValue[1]));
        }
      }
      return (T)finalMap;
    }
    throw new IllegalArgumentException("Unsupported type: " + type.getName());
  }

  /**
   * String tokenizer that preservers all tokens and ignores escaped separator chars.
   * @param value Value to tokenize
   * @param separatorChar Separator char
   * @return String parts. Never null.
   */
  static String[] splitPreserveAllTokens(String value, char separatorChar) {
    if (value == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    int len = value.length();
    if (len == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    List<String> list = new ArrayList<>();
    int i = 0;
    int start = 0;
    boolean match = false;
    boolean lastMatch = false;
    int escapeStart = -2;
    while (i < len) {
      char c = value.charAt(i);
      if (c == ESCAPE_DELIMITER_CHAR) {
        escapeStart = i;
      }
      if (c == separatorChar && escapeStart != i - 1) {
        lastMatch = true;
        list.add(value.substring(start, i));
        match = false;
        start = ++i;
        continue;
      }
      match = true;
      i++;
    }
    if (match || lastMatch) {
      list.add(value.substring(start, i));
    }
    return list.toArray(new String[list.size()]);
  }

  /**
   * Escape all delimiter chars in string.
   * @param values Strings
   * @return Encoded strings
   */
  static String[] encodeString(String[] values) {
    if (values == null) {
      return null;
    }
    String[] encodedValues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      encodedValues[i] = encodeString(values[i]);
    }
    return encodedValues;
  }

  /**
   * Escape all delimiter chars in string.
   * @param value String
   * @return Encoded string
   */
  static String encodeString(String value) {
    if (value == null) {
      return null;
    }
    StringBuilder encoded = new StringBuilder();
    int len = value.length();
    for (int i = 0; i < len; i++) {
      char c = value.charAt(i);
      switch (c) {
        case ARRAY_DELIMITER_CHAR:
        case KEY_VALUE_DELIMITER_CHAR:
        case ESCAPE_DELIMITER_CHAR:
          encoded.append(ESCAPE_DELIMITER_CHAR);
          break;
        default:
          // just append char
      }
      encoded.append(c);
    }
    return encoded.toString();
  }

  /**
   * Unescape all delimiter chars in string.
   * @param values Strings
   * @return Decoded strings
   */
  static String[] decodeString(String[] values) {
    if (values == null) {
      return null;
    }
    String[] decodedValues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      decodedValues[i] = decodeString(values[i]);
    }
    return decodedValues;
  }

  /**
   * Unescape all delimiter chars in string.
   * @param value String
   * @return Decoded string
   */
  static String decodeString(String value) {
    if (value == null) {
      return null;
    }
    StringBuilder decoded = new StringBuilder();
    int len = value.length();
    int escapeStart = -2;
    for (int i = 0; i < len; i++) {
      char c = value.charAt(i);
      if (c == ESCAPE_DELIMITER_CHAR && escapeStart != i - 1) {
        escapeStart = i;
      }
      else {
        decoded.append(c);
      }
    }
    return decoded.toString();
  }

}
