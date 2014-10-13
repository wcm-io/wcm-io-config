angular.module('io.wcm.config.test.templates', ['editorTable.html']);

angular.module("editorTable.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("editorTable.html",
    "<form id=\"configuration\" name=\"configForm\">\n" +
    "  <table class=\"coral-Table coral-Table--hover\" ng-cloak>\n" +
    "    <tbody>\n" +
    "    <tr class=\"coral-Table-row\" ng-repeat=\"parameter in displayedCollection\" ng-cloak>\n" +
    "      <td class=\"coral-Table-cell\">\n" +
    "        {{parameter.label}}\n" +
    "      </td>\n" +
    "      <td class=\"coral-Table-cell\" parameter-value=\"parameter\" widget-type=\"{{parameter.widgetType}}\">\n" +
    "      </td>\n" +
    "    </tr>\n" +
    "    </tbody>\n" +
    "  </table>\n" +
    "</form>\n" +
    "");
}]);
