(function (angular) {
  "use strict";
  angular.module('io.wcm.config.editor', ['io.wcm.config.directives'])
    .controller("mainCtrl", ['$scope', "$filter", function($scope, $filter) {
      $scope.currentFilter = {};
      $scope.displayedCollection = [];

      $scope.parameterCollection = [];
      $scope.parameterCollection.push({
        "name": "Locked String Parameter",
        "value": "Test",
        group: "Dealer Locator",
        application: "/apps/viessmann",
        description:"Das ist ein parameter, für die konfoguration des Dealers",
        inherited: true,
        locked: true,
        "widgetType": "stringfield",
        lockedInherited: true
      });
      $scope.parameterCollection.push({
        "name": "String Parameter",
        "value": "Test",
        group: "Dealer Locator",
        application: "/apps/viessmann",
        description:"Das ist ein Texfield parameter, für die Konfiguration des Dealers",
        inherited: true,
        widgetType: "textfield"
      });
      $scope.parameterCollection.push({
        "name": "Textarea Parameter Only for Digits",
        "value": "123",
        group: "Link Handling",
        application: "/apps/wcm-io/linkhandler",
        description:"Das ist Textarea Prameter mit der Pattern-Validierung für maximal 5 numerische Zeichen",
        inherited: "false",
        locked: false,
        widgetType: "textarea",
        rows: "10",
        maxlength: "5",
        pattern: "/^[0-9]*$/",
        required: true
      });
      $scope.parameterCollection.push({
        "name": "Pathbrowser Parameter",
        "value": "",
        group: "Link Handling",
        application: "/apps/wcm-io/linkhandler",
        description:"Das ist ein parameter, für die Konfiguration eines Pfades",
        inherited: "false",
        locked: false,
        widgetType: "pathbrowser",
        rootPath: "/content"
      });
      $scope.parameterCollection.push({
        "name": "Checkbox Parameter",
        "value": true,
        group: "Link Handling",
        application: "/apps/wcm-io/linkhandler",
        description:"Das ist ein parameter, für die Konfiguration eines Boolen Wertes",
        inherited: "false",
        locked: false,
        widgetType: "checkbox",
        required: true
      });

      $scope.filters = [];

      var applicationFilter = {name: "Filter Application", filterParameter:"application"}
      var applications = [];
      applications.push({"value": "/apps/viessmann", "label": "Viessmann Responsive"})
      applications.push({"value": "/apps/wcm-io/linkhandler", "label": "Link Handler"})
      applicationFilter.options = applications;

      $scope.filters.push(applicationFilter);

      var groupFilter = {name: "Filter Group", filterParameter:"group"}
      var groups = [];
      groups.push({"value": "Link Handling", "label": "Link Handling"})
      groups.push({"value": "Dealer Locator", "label": "Dealer Locator"})
      groupFilter.options = groups;

      $scope.filters.push(groupFilter);

      $scope.save = function() {
        console.log("Sending data to backend")
      };

      /**
       * Filters the shown parameters based on the currently selected filtered.
       * Triggered when the $scope.currentFilter is modified
       * @param newValue
       * @param oldValue
       */
      function filterDisplayedParameters(newValue, oldValue) {

        var filteredParameters = $scope.parameterCollection;

        for (var propertyName in $scope.currentFilter) {
          if ($scope.currentFilter.hasOwnProperty(propertyName)) {
            var filterValues = $scope.currentFilter[propertyName];

            if (filterValues && filterValues.length > 0) {
              var isVisisible = function(parameter, index) {
                var value = parameter[propertyName];
                return filterValues.indexOf(value) !== -1;
              };
              filteredParameters = $filter("filter")(filteredParameters, isVisisible);
            }

          }
        }
        $scope.displayedCollection = filteredParameters;

      }

      $scope.$watch('currentFilter', filterDisplayedParameters, true);

    }]);
})(angular);
