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
package io.wcm.samples.configsampleapp.config;

import static io.wcm.config.api.ParameterBuilder.create;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.wcm.config.api.Parameter;
import io.wcm.config.editor.EditorProperties;
import io.wcm.config.editor.WidgetTypes;

/**
 * Defines some example paramters.
 */
public final class Params {

  private Params() {
    // contants only
  }

  /**
   * Application ID
   */
  public static final String APPLICATION_ID = "/apps/wcm-io-config-sample-app";

  /**
   * String parameter
   */
  public static final Parameter<String> STRING_PARAM = create("string-param", String.class, APPLICATION_ID)
      .property(EditorProperties.LABEL, "String Param")
      .property(EditorProperties.DESCRIPTION, "This is a simple String parameter with default value")
      .property(EditorProperties.GROUP, "Group 1").defaultValue("default value")
      .properties(WidgetTypes.TEXTFIELD.getDefaultWidgetConfiguration())
      .build();

  /**
   * Integer parameter
   */
  public static final Parameter<Integer> INTEGER_PARAM = create("int-param", Integer.class, APPLICATION_ID)
      .property(EditorProperties.GROUP, "Group 1")
      .property(EditorProperties.DESCRIPTION, "This is a simple Integer parameter with default value")
      .properties(WidgetTypes.TEXTFIELD.getDefaultWidgetConfiguration()).property(EditorProperties.PATTERN, "/^[0-9]*$/")
      .defaultValue(5)
      .build();

  /**
   * Double parameter
   */
  public static final Parameter<Double> DOUBLE_PARAM = create("double-param", Double.class, APPLICATION_ID)
      .property(EditorProperties.DESCRIPTION, "This is a simple Double parameter with default value")
      .property(EditorProperties.GROUP, "Group 1")
      .properties(WidgetTypes.TEXTFIELD.getDefaultWidgetConfiguration())
      .defaultValue(5.343d)
      .build();

  /**
   * Long parameter
   */
  public static final Parameter<Long> LONG_PARAM = create("long-param", Long.class, APPLICATION_ID)
      .property(EditorProperties.DESCRIPTION, "This is a simple Long parameter with default value")
      .property(EditorProperties.GROUP, "Group 1")
      .properties(WidgetTypes.TEXTFIELD.getDefaultWidgetConfiguration())
      .defaultValue(5L)
      .build();

  /**
   * Map parameter
   */
  public static final Parameter<Map> MAP_PARAM = create("map-param", Map.class, APPLICATION_ID)
      .property(EditorProperties.DESCRIPTION, "This is a Map parameter.")
      .property(EditorProperties.GROUP, "Group 1")
      .properties(WidgetTypes.MAP.getDefaultWidgetConfiguration())
      .defaultValue(ImmutableMap.of("key1", "value1", "key2", "value2"))
      .build();

  /**
   * Text field parameter
   */
  public static final Parameter<String> TEXT_PARAM = create("text-param", String.class, APPLICATION_ID)
      .property(EditorProperties.DESCRIPTION, "This is a String parameter. The delimiter between single entries is ';'")
      .property(EditorProperties.GROUP, "Group 1")
      .properties(WidgetTypes.TEXTAREA.getDefaultWidgetConfiguration())
      .build();

  /**
   * Multivalue field parameter
   */
  public static final Parameter<String[]> MULTIVALUE_PARAM = create("multivalue-param", String[].class, APPLICATION_ID)
      .property(EditorProperties.DESCRIPTION, "This is a multivalue parameter.")
      .property(EditorProperties.GROUP, "Group 1")
      .properties(WidgetTypes.TEXT_MULTIFIELD.getDefaultWidgetConfiguration())
      .build();


  /**
   * Checkbox field parameter
   */
  public static final Parameter<Boolean> CHECKBOX_PARAM = create("checkbox-param", Boolean.class, APPLICATION_ID)
      .properties(WidgetTypes.CHECKBOX.getDefaultWidgetConfiguration())
      .property(EditorProperties.GROUP, "Group 2")
      .property(EditorProperties.DESCRIPTION, "This is a simple Boolean parameter")
      .build();

  /**
   * Path Browser field parameter
   */
  public static final Parameter<String> PATHBROWSER_PARAM = create("pathbrowser-param", String.class, APPLICATION_ID)
      .property(EditorProperties.GROUP, "Group 2")
      .property(EditorProperties.DESCRIPTION, "This is a Pathbrowser parameter with default tree root set to '/content'. "
          + "Start typing with '/' to receive suggestions")
      .properties(WidgetTypes.PATHBROWSER.getDefaultWidgetConfiguration())
      .build();

}
