angular.module('io.wcm.config.templates', ['filterDropDownList.html', 'parameterTable.html', 'parameterValue.html', 'popupContainer.html', 'popupContent.html']);

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

angular.module("parameterTable.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("parameterTable.html",
    "<table class=\"coral-Table coral-Table--hover\">\n" +
    "<thead>\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <th class=\"coral-Table-headerCell\">Parameter</th>\n" +
    "  <th class=\"coral-Table-headerCell\">Value</th>\n" +
    "  <th class=\"coral-Table-headerCell\">Inherited</th>\n" +
    "  <th class=\"coral-Table-headerCell\">Lock</th>\n" +
    "  <th class=\"coral-Table-headerCell\">Description</th>\n" +
    "  <th class=\"coral-Table-headerCell\">Group</th>\n" +
    "</tr>\n" +
    "</thead>\n" +
    "<tbody>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\" ng-repeat=\"parameter in parameters\">\n" +
    "  <td class=\"coral-Table-cell\" ng-repeat=\"column in columns\"></td>\n" +
    "</tr>\n" +
    "\n" +
    "\n" +
    "<tr class=\"coral-Table-row\" ng-repeat=\"parameter in displayedCollection\" ng-cloak>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    {{parameter.name}}\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\" input-widget-type=\"{{parameter.widgetType}}\">\n" +
    "    {{parameter.value}}\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <description-popup>\n" +
    "      <popup-content>\n" +
    "        {{parameter.description}}\n" +
    "      </popup-content>\n" +
    "    </description-popup>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    {{parameter.group}}\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    String Parameter\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <input type=\"text\" class=\"coral-Textfield\" value=\"Value 1\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">\n" +
    "        <h3>String Parameter</h3>\n" +
    "        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.\n" +
    "          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.</p>\n" +
    "        <ul>\n" +
    "          <li>Item 1</li>\n" +
    "          <li>Item 2</li>\n" +
    "          <li>Item 3</li>\n" +
    "        </ul>\n" +
    "        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.\n" +
    "          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.</p>\n" +
    "      </div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    String Parameter (inherited)\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Value 1\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c11\" value=\"1\" checked=\"\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    String Parameter (disabled)\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Value 1\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c5\" value=\"5\" disabled=\"disabled\" checked=\"\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    String Parameter (Data Error)\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <input type=\"text\" class=\"coral-Textfield is-invalid\" value=\"Value 1\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Multiline String Parameter\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <textarea class=\"coral-Textfield coral-Textfield--multiline\">Value 1\n" +
    "      Value 2\n" +
    "      Value 3</textarea>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">\n" +
    "        <h3>String Parameter</h3>\n" +
    "        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.\n" +
    "          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.</p>\n" +
    "        <ul>\n" +
    "          <li>Item 1</li>\n" +
    "          <li>Item 2</li>\n" +
    "          <li>Item 3</li>\n" +
    "        </ul>\n" +
    "        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.\n" +
    "          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus imperdiet interdum convallis.</p>\n" +
    "      </div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Single Selection\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "            <span class=\"coral-Select\" data-init=\"select\">\n" +
    "              <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\n" +
    "                <span class=\"coral-Select-button-text\">One</span>\n" +
    "              </button>\n" +
    "              <select class=\"coral-Select-select\">\n" +
    "                <option value=\"1\">One</option>\n" +
    "                <option value=\"2\">Two</option>\n" +
    "                <option value=\"3\">Three</option>\n" +
    "                <option value=\"4\">One Two Three Four Five Six Seven Eight Nine Ten</option>\n" +
    "              </select>\n" +
    "            </span>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Multiple Selection\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "            <span class=\"coral-Select\" data-init=\"select\">\n" +
    "              <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\n" +
    "                <span class=\"coral-Select-button-text\">Select</span>\n" +
    "              </button>\n" +
    "              <select class=\"coral-Select-select\" multiple=\"true\">\n" +
    "                <option value=\"1\" selected=\"\">One</option>\n" +
    "                <option value=\"2\">Two</option>\n" +
    "                <option value=\"3\" selected=\"\">Three</option>\n" +
    "              </select>\n" +
    "            </span>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Checkbox\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "<tr class=\"coral-Table-row\">\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Checkbox (Inherited)\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c12\" value=\"1\" checked=\"\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <label class=\"coral-Checkbox\">\n" +
    "      <input class=\"coral-Checkbox-input\" name=\"c1\" value=\"1\" type=\"checkbox\">\n" +
    "      <span class=\"coral-Checkbox-checkmark\"></span>\n" +
    "    </label>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    <button class=\"coral-MinimalButton\" data-target=\"#defaultPopover\" data-toggle=\"popover\">\n" +
    "      <i class=\"coral-Icon coral-Icon--infoCircle coral-Icon--sizeM coral-MinimalButton-icon\"></i>\n" +
    "    </button>\n" +
    "    <div id=\"defaultPopover\" class=\"coral-Popover\">\n" +
    "      <div class=\"coral-Popover-content u-coral-padding\">Description</div>\n" +
    "    </div>\n" +
    "  </td>\n" +
    "  <td class=\"coral-Table-cell\">\n" +
    "    Group\n" +
    "  </td>\n" +
    "  </td>\n" +
    "</tr>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "</tbody>\n" +
    "</table>\n" +
    "");
}]);

angular.module("parameterValue.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("parameterValue.html",
    "<div ng-switch on=\"type\">\n" +
    "  <input required=\"required\" ng-switch-when=\"stringfield\" type=\"text\" class=\"coral-Textfield\" ng-model=\"parameter.value\"/>\n" +
    "  <textarea ng-switch-when=\"textarea\" class=\"coral-Textfield coral-Textfield--multiline\" ng-model=\"parameter.value\"></textarea>\n" +
    "  <span ng-switch-default>{{parameter.value}}</span>\n" +
    "</div>");
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
