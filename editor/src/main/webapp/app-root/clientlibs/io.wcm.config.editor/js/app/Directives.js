(function (angular) {
  "use strict";
  angular.module('io.wcm.config.directives', ["io.wcm.config.templateUrlList", "io.wcm.config.utilities", "io.wcm.config.templates"])
    .directive("filterDropDownList", ['templateUrlList', function (templateList) {
      return {
        restrict: "E",
        replace: true,
        scope: {
          model: "=",
          currentFilter: "="
        },
        templateUrl: templateList.filterDropDownList,
        link: function (scope, element, attr) {
          var widget;

          function handleSelectionChange() {
            scope.$apply(function() {
              scope.currentFilter[scope.model.filterParameter] = widget.getValue()
            });
          }

          function initWidget () {
            widget = new CUI.Select({ element: element[0] });
            widget.on("selected", handleSelectionChange);
            widget.on("itemremoved", handleSelectionChange);
          };

          scope.$evalAsync(initWidget);
        }
      }
    }])
    .directive("descriptionPopup", ['templateUrlList', "EditorUtilities", function (templateList, utils) {
      return {
        restrict: "E",
        replace: true,
        templateUrl: templateList.popupContainer,
        transclude: true,
        scope:{},
        link: function(scope, element, attr) {
          var widget;

          scope.id = utils.nextUid();
          scope.$evalAsync(function () {
            widget = new CUI.Popover({element: $("coral-Popover", element)});
          });
        }
      }
    }])
    .directive("popupContent", ['templateUrlList', function (templateList) {
      return {
        restrict: "E",
        replace: true,
        transclude: true,
        templateUrl: templateList.popupContent
      }
    }]);
})(angular);