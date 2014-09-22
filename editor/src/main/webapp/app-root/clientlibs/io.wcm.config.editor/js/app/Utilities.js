(function (angular) {
  var uid = ['0', '0', '0' ];

  "use strict";
  /**
   * Utilities module
   */
  angular.module('io.wcm.config.utilities', [])

    .factory('EditorUtilities', function () {

      /**
       *
       * @returns unique Id
       */
      var nextUid = function() {
        var index = uid.length;
        var digit;

        while (index) {
          index--;
          digit = uid[index].charCodeAt(0);
          if (digit == 57 /*'9'*/) {
            uid[index] = 'A';
            return uid.join('');
          }
          if (digit == 90  /*'Z'*/) {
            uid[index] = '0';
          } else {
            uid[index] = String.fromCharCode(digit + 1);
            return uid.join('');
          }
        }
        uid.unshift('0');
        return uid.join('');
      };

      var loadAutocompleteOptions = function (path, callback) {
        jQuery.get(path + '.pages.json', {
            predicate: 'hierarchyNotFile'
          },
          function(data) {
            var pages = data.pages;
            var result = [];
            for(var i = 0; i < pages.length; i++) {
              result.push(pages[i].label);
            }
            if (callback) callback(result);
          }, 'json');
        return false;
      };


      return {
        nextUid: nextUid,
        loadAutocompleteOptions: loadAutocompleteOptions
      };
    });
})(angular);


