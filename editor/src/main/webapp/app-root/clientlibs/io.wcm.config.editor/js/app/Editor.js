(function (angular) {

  "use strict";
  angular.module('io.wcm.config.editor', ['io.wcm.config.services', 'io.wcm.config.directives'])
    .run(["$rootScope", "parameters", function($rootScope, Parameters) {
      $rootScope.confirmModal = new CUI.Modal({ element:'#confirmModal', visible: false });
      $rootScope.errorModal = new CUI.Modal({ element:'#errorModal', type: "error", visible: false });
      /**
       * Use the parameters service to load and parse data from backend
       */
      Parameters.loadParameters().then(
        function success(result){
          var parsedData = Parameters.parseData(result.data);
          $rootScope.$evalAsync(function() {
            $rootScope.filters = parsedData.filters;
            $rootScope.parameterCollection = parsedData.parameters;
          });
        },
        function error() {
          $rootScope.errorModal.show();
        }
      );

    }])
    .controller("mainCtrl", ['$scope', "$filter", "parameters", function($scope, $filter, Parameters) {
      $scope.currentFilter = {};
      $scope.displayedCollection = [];

      $scope.save = function() {
        Parameters.saveParameters($scope.parameterCollection).then(
          function success()  {
            $scope.confirmModal.show();
          },
          function error() {
            $scope.errorModal.show();
          }
        );
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
      $scope.$watch('parameterCollection', filterDisplayedParameters, true);

    }]);
})(angular);
