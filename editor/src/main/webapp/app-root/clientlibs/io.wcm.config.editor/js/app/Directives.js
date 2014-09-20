(function (angular) {
  "use strict";
  angular.module('io.wcm.config.directives', ["io.wcm.config.templateUrlList", "io.wcm.config.utilities", "io.wcm.config.templates"])
  /**
   * Directive for displaying the filters on the editor page. Wraps internally the CUI.Select widget.
   * Parent Scope provides the model for the filter:
   * {
   *   name: "Filter Application",
   *   filterParameter:"application",
   *   options: [
   *     {
   *      "value": "/apps/viessmann",
   *      "label": "Viessmann Responsive"
   *     }
   *   ]
   * }
   *
   * filterParameter specifies the property name of the parameter, which will be used to apply the filtering
   * Every change of the selection is applied to the "currentFilter" property from the parent scope. The parent scope has a
   * watcher registered to this property and filters the displayed collection of the parameter n every change
   */
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
  /**
   * Directive to render the "i" button with a popover for the decription of the parameter. Wraps a coral UI Button and
   * CUI.Popover widget. This directive transcludes the popup content elements. Example:
   * <description-popup>
   *   <popup-content>
   *     Description Text
   *   </popup-content>
   * </description-popup>
   *
   */
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
  /**
   * Renders the wrapping elements for the coral ui popover. The content itself is transcluded. The content can also contain markup
   *   <popup-content>
   *     Description Text
   *   </popup-content>
   *
   */
    .directive("popupContent", ['templateUrlList', function (templateList) {
      return {
        restrict: "E",
        replace: true,
        transclude: true,
        templateUrl: templateList.popupContent
      }
    }])
    .directive("parameterValue", ['templateUrlList', function (templateList) {
      return {
        restrict: "A",
        replace: false,
        templateUrl: templateList.parameterValue,
        scope: {
          parameter: '=parameterValue',
          type: '@widgetType'
        },
        link: function(scope, element, attr) {
          scope.originalType = scope.type;
          scope.originalValue = scope.parameter.value;

          scope.$watch("parameter.inherited", function(newvalue, oldvalue){
            if (newvalue === true) {
              scope.type = "disabled";
              scope.parameter.value = scope.originalValue;
            } else {
              scope.type = scope.originalType;
            }
          });
          scope.$watch("parameter.locked", function(newvalue, oldvalue){
            if (newvalue === true) {
              scope.type = "disabled";
            } else if (scope.parameter.inherited === true) {
              scope.type = "disabled";
            } else {
              scope.type = scope.originalType;
            }
          });
        }
      }
    }]);
})(angular);