(function (angular) {
  "use strict";
  angular.module('io.wcm.config.editor', ['io.wcm.config.directives'])
    .controller("mainCtrl", ['$scope', "$filter", function($scope, $filter) {
      $scope.currentFilter = {};
      $scope.displayedCollection = [];

      $scope.parameterCollection = [];
      $scope.parameterCollection.push({"name": "String Parameter", "value": "Test",
        group: "Dealer Locator", application: "/apps/viessmann", description:"Das ist ein parameter, für die konfoguration des Dealers"});
      $scope.parameterCollection.push({"name": "String Parameter 2", "value": "Test 2",
        group: "Link Handling", application: "/apps/wcm-io/linkhandler", description:"Das ist ein parameter, für die konfoguration des Link Handlings"});

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
