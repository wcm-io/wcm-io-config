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
      popupContainer: 'popupContainer.html',
      popupContent: 'popupContent.html'
    });
})(angular);

