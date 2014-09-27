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
package io.wcm.config.editor.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Provides available widget types and their default configurations
 */
public enum WidgetTypes {

  /**
   * Text Field widget. Following properties are available for configuration (all empty per default):
   * - required = true or false
   * - maxlength = integer value
   * - minlength = integer value
   * - pattern = regular expression for the value validation. E.g. /^[0-9]*$/
   */
  TEXTFIELD(ImmutableMap.copyOf(Defaults.DEFAULT_TEXTFIELD_CONFIGURATION)),

  /**
   * Text Area widget. Following properties are available for configuration (all empty per default):
   * - requried = true or false
   * - maxlength = integer value
   * - minlength = integer value
   * - pattern = regular expression for the value validation. E.g. /^[0-9]*$/
   */
  TEXTAREA(ImmutableMap.copyOf(Defaults.DEFAULT_TEXTAREA_CONFIGURATION)),

  /**
   * Path Browser widget with autocomplete function.Following properties are available for configuration (all empty per
   * default):
   * - required = true or false
   * - rootPath = root path from where pages can be selected. Default: /content
   */
  PATHBROWSER(ImmutableMap.copyOf(Defaults.DEFAULT_PATHBROWSER_CONFIGURATION)),

  /**
   * Checkbox widget.Following properties are available for configuration (all empty per
   * default):
   * - required = true or false
   */
  CHECKBOX(ImmutableMap.copyOf(Defaults.DEFAULT_CHECKBOX_CONFIGURATION));


  private final Map<String, Object> params;


  WidgetTypes(Map<String, Object> confgurationParameters) {
    this.params = confgurationParameters;
  }

  /**
   * @param defaultOverrides
   * @return configuration properties of the widget with overridden default values
   */
  public Map<String, Object> getWidgetConfiguration(Map<String, Object> defaultOverrides) {
    Map<String, Object> parameters = new HashMap<>(this.params);
    for (String key : defaultOverrides.keySet()) {
      parameters.put(key, defaultOverrides.get(key));
    }
    return parameters;
  }

  /**
   * @return configuration properties of the widget
   */
  public Map<String, Object> getDefaultWidgetConfiguration() {
    return new HashMap<String, Object>(this.params);
  }

  /**
   * Provides constants for the widget configuration property names
   */
  public static final class Defaults {

    /**
     * Name of the property to set the widget type
     */
    public static final String PN_WIDGET_TYPE = "widgetType";

    /**
     * Name of the property to set the the application id of the parameter
     */
    public static final String PN_PARAMETER_NAME = "name";

    /**
     * Name of the property to set the the application id of the parameter
     */
    public static final String PN_PARAMETER_VALUE = "value";

    /**
     * Name of the property to set the the application id of the parameter
     */
    public static final String PN_INHERITED_VALUE = "inheritedValue";

    /**
     * Name of the property to set the the application id of the parameter
     */
    public static final String PN_APPLICATION_ID = "application";

    /**
     * Name of the property to set the the group of the parameter
     */
    public static final String PN_GROUP = "group";

    /**
     * Name of the property to set the the description of the parameter
     */
    public static final String PN_DESCRIPTION = "description";

    /**
     * Name of the property to set the minimum length value for the text field
     */
    public static final String PN_MINLENGTH = "minlength";

    /**
     * Name of the property to set the maximum length value for the text field
     */
    public static final String PN_MAXLENGTH = "minlength";

    /**
     * Name of the property to set the number of rows on the text area
     */
    public static final String PN_ROWS = "rows";

    /**
     * Name of the property to set the "required" flag
     */
    public static final String PN_REQUIRED = "required";

    /**
     * Name of the property to set the validation pattern
     */
    public static final String PN_PATTERN = "pattern";

    /**
     * Name of the property to set root path for the browser
     */
    public static final String PN_ROOT_PATH = "rootPath";

    /**
     * Name of the property to set the flag whether the parameter value is inherited
     */
    public static final String PN_INHERITED = "inherited";

    /**
     * Name of the property to set the flag whether the parameter value is locked
     */
    public static final String PN_LOCKED = "locked";

    /**
     * Name of the property to set the flag whether the parameter value was locked and cannot be unlocked
     */
    public static final String PN_LOCKED_INHERITED = "lockedInherited";

    private static final Map<String, Object> DEFAULT_TEXTFIELD_CONFIGURATION = new HashMap<>();
    static {
      DEFAULT_TEXTFIELD_CONFIGURATION.put(PN_WIDGET_TYPE, "textfield");
    }

    private static final Map<String, Object> DEFAULT_TEXTAREA_CONFIGURATION = new HashMap<>();
    static {
      DEFAULT_TEXTAREA_CONFIGURATION.put(PN_WIDGET_TYPE, "textarea");
      DEFAULT_TEXTAREA_CONFIGURATION.put(PN_ROWS, "10");
    }

    private static final Map<String, Object> DEFAULT_CHECKBOX_CONFIGURATION = new HashMap<>();
    static {
      DEFAULT_CHECKBOX_CONFIGURATION.put(PN_WIDGET_TYPE, "checkbox");
    }

    private static final Map<String, Object> DEFAULT_PATHBROWSER_CONFIGURATION = new HashMap<>();
    static {
      DEFAULT_PATHBROWSER_CONFIGURATION.put(PN_WIDGET_TYPE, "pathbrowser");
      DEFAULT_PATHBROWSER_CONFIGURATION.put(PN_ROOT_PATH, "/content/");
    }
  }
}
