(function (angular) {
  "use strict";
  angular.module('io.wcm.config.templateUrlList', [])
    .constant('templateUrlList', {
      filterDropDownList: 'filterDropDownList.html',
      parameterTable: 'parameterTable.html',
      popupContainer: 'popupContainer.html',
      popupContent: 'popupContent.html'
    });
})(angular);

