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
(function (angular) {
  "use strict";
  /**
   * Provides the list of available templates, used in directives
   */
  angular.module('io.wcm.config.templateUrlList', [])
    .constant('templateUrlList', {
      filterDropDownList: 'filterDropDownList.html',
      parameterValue: 'parameterValue.html',
      pathBrowser: 'pathBrowser.html',
      textMultifield: 'textMultifield.html',
      map: 'map.html',
      popupContainer: 'popupContainer.html',
      popupContent: 'popupContent.html'
    });
})(angular);

