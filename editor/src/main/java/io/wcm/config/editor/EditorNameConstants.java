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
package io.wcm.config.editor;

import io.wcm.sling.commons.resource.ImmutableValueMap;

import java.util.Map;

/**
 * Provides constants for the widget configuration property names
 */
public final class EditorNameConstants {

  private EditorNameConstants() {
    // only constants
  }

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

  static final Map<String, Object> DEFAULT_TEXTFIELD_CONFIGURATION = ImmutableValueMap.builder()
      .put(PN_WIDGET_TYPE, "textfield")
      .build();

  static final Map<String, Object> DEFAULT_MULTIFIELD_CONFIGURATION = ImmutableValueMap.builder()
      .put(PN_WIDGET_TYPE, "textMultivalue")
      .build();

  static final Map<String, Object> DEFAULT_TEXTAREA_CONFIGURATION = ImmutableValueMap.builder()
      .put(PN_WIDGET_TYPE, "textarea")
      .put(PN_ROWS, "10")
      .build();

  static final Map<String, Object> DEFAULT_CHECKBOX_CONFIGURATION = ImmutableValueMap.builder()
      .put(PN_WIDGET_TYPE, "checkbox")
      .build();

  static final Map<String, Object> DEFAULT_PATHBROWSER_CONFIGURATION = ImmutableValueMap.builder()
      .put(PN_WIDGET_TYPE, "pathbrowser")
      .put(PN_ROOT_PATH, "/content/")
      .build();

}
