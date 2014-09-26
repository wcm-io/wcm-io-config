(function (angular) {
  "use strict";
  /**
   * Services module
   */
  angular.module('io.wcm.config.services', ['io.wcm.config.utilities'])
    .provider("parameters", function() {
      var config = {};

      function Parameter($http, $q, config, utils) {

        /**
         * Extracts the filter options for the specific filter
         * @param filter
         * @param propertyName
         * @param parameters
         */
        function extractFilter(filter, propertyName, parameters) {
          _.map(parameters, function(parameter){
            var value = parameter[propertyName];
            if (value) {
              var option = {value: value, label: value};
              if (!utils.contains(filter.options, option)) {
                filter.options.push(option);
              }
            }
          });
        }

        /**
         *
         * @param parameters
         */
        function extractFilters(parameters) {
          var applicationFilter = {
            name: "Filter Application", filterParameter:"application", options:[]
          };
          var groupFilter = {
            name: "Filter Group", filterParameter:"group", options:[]
          };
          extractFilter(groupFilter, "group", parameters);
          extractFilter(applicationFilter, "application", parameters);

          var result = [];
          result.push(groupFilter);
          result.push(applicationFilter);
          return result;
        }

        this.loadParameters = function () {
          return $http.get(config.url);
        };

        this.parseData = function(data) {
          var result = {
            filters: [],
            parameters: []
          };
          if (data.parameters){
            result.parameters = data.parameters;
            result.filters = extractFilters(data.parameters);
          }
          return result;
        };

        this.saveParameters = function(data) {

        };
      }

      this.setConfig = function(configData) {
        config = configData;
      };

      this.$get = ["$http", "$q", "EditorUtilities", function($http, $q, utils) {
        return new Parameter($http, $q, config, utils);
      }];
    });
})(angular);
