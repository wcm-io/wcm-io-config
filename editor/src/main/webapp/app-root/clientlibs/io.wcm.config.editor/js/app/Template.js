angular.module('io.wcm.config.templates', ['filterDropDownList.html', 'parameterValue.html', 'pathBrowser.html', 'popupContainer.html', 'popupContent.html', 'textMultifield.html']);

angular.module("filterDropDownList.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("filterDropDownList.html",
    "<span class=\"coral-Select\" ng-model=\"value\">\n" +
    "  <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\n" +
    "    <span class=\"coral-Select-button-text\">{{model.name}}</span>\n" +
    "  </button>\n" +
    "  <select class=\"coral-Select-select\" multiple=\"true\">\n" +
    "    <option ng-repeat=\"option in model.options\" value=\"{{option.value}}\" ng-attr-selected=\"{{option.selected}}\">{{option.label}}</option>\n" +
    "  </select>\n" +
    "</span>");
}]);

angular.module("parameterValue.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("parameterValue.html",
    "<div ng-switch on=\"type\">\n" +
    "\n" +
    "  <input ng-switch-when=\"textfield\"\n" +
    "         type=\"text\" class=\"coral-Textfield\"\n" +
    "         ng-model=\"parameter.value\"\n" +
    "         ng-maxlength=\"{{parameter.maxlength}}\"\n" +
    "         ng-minlength=\"{{parameter.minlength}}\"\n" +
    "         ng-pattern=\"{{parameter.pattern}}\"\n" +
    "         ng-required=\"{{parameter.required}}\"/>\n" +
    "\n" +
    "  <multifield ng-switch-when=\"textMultivalue\" parameter=\"parameter\" />\n" +
    "\n" +
    "\n" +
    "  <textarea ng-switch-when=\"textarea\" class=\"coral-Textfield coral-Textfield--multiline\" name=\"parameter\"\n" +
    "             ng-model=\"parameter.value\"\n" +
    "             ng-maxlength=\"{{parameter.maxlength}}\"\n" +
    "             ng-minlength=\"{{parameter.minlength}}\"\n" +
    "             ng-pattern=\"{{parameter.pattern}}\"\n" +
    "             ng-required=\"{{parameter.required}}\"\n" +
    "             ng-attr-rows=\"{{parameter.rows}}\"></textarea>\n" +
    "\n" +
    "  <path-browser ng-switch-when=\"pathbrowser\" parameter=\"parameter\" root-path=\"{{parameter.rootPath}}\"></path-browser>\n" +
    "\n" +
    "  <label ng-switch-when=\"checkbox\" class=\"coral-Checkbox\">\n" +
    "    <input ng-required=\"{{parameter.required}}\" class=\"coral-Checkbox-input\" ng-model=\"parameter.value\" type=\"checkbox\">\n" +
    "    <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "  </label>\n" +
    "\n" +
    "  <i ng-switch-when=\"disabledCheckbox\" ng-class=\"{'coral-Icon--check': parameter.value}\" class=\"coral-Icon coral-Icon--sizeS\"></i>\n" +
    "\n" +
    "  <span ng-switch-default>{{parameter.value}}</span>\n" +
    "</div>");
}]);

angular.module("pathBrowser.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("pathBrowser.html",
    "<span class=\"coral-Form-field coral-InputGroup wcm-io-editor-pathbrowser\" id=\"pathbrowser\">\n" +
    "    <input class=\"coral-InputGroup-input coral-Textfield js-coral-pathbrowser-input\" type=\"text\" ng-model=\"parameter.value\" autocomplete=\"off\">\n" +
    "</span>\n" +
    "");
}]);

angular.module("popupContainer.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("popupContainer.html",
    "<div>\n" +
    "  <button class=\"coral-MinimalButton\" data-target=\"#{{id}}\" data-toggle=\"popover\">\n" +
    "    <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "  </button>\n" +
    "  <div id=\"{{id}}\" class=\"coral-Popover\" ng-transclude></div>\n" +
    "</div>\n" +
    "");
}]);

angular.module("popupContent.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("popupContent.html",
    "<div class=\"coral-Popover-content u-coral-padding\" ng-transclude>\n" +
    "</div>\n" +
    "");
}]);

angular.module("textMultifield.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("textMultifield.html",
    "<div >\n" +
    "  <div class=\"wcm-io-editor-multifield\" ng-repeat=\"value in values\">\n" +
    "    <input type=\"text\" class=\"coral-Textfield\"\n" +
    "           ng-model=\"value.value\"\n" +
    "           ng-maxlength=\"{{parameter.maxlength}}\"\n" +
    "           ng-minlength=\"{{parameter.minlength}}\"\n" +
    "           ng-pattern=\"{{parameter.pattern}}\"\n" +
    "           ng-required=\"{{parameter.required}}\"/>\n" +
    "    <div class=\"coral-ButtonGroup\">\n" +
    "      <button ng-click=\"addNewValue(value)\" class=\"coral-ButtonGroup-item coral-Button coral-Button--secondary\">\n" +
    "        <i class=\"coral-Icon coral-Icon--add\"></i>\n" +
    "      </button>\n" +
    "      <button ng-click=\"removeValue(value)\" class=\"coral-ButtonGroup-item coral-Button coral-Button--secondary\">\n" +
    "        <i class=\"coral-Icon coral-Icon--minus\"></i>\n" +
    "      </button>\n" +
    "    </div>\n" +
    "  </div>\n" +
    "</div>\n" +
    "");
}]);
