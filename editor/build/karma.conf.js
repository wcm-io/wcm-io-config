module.exports = function(config) {
  config.set({
    // base path, that will be used to resolve files and exclude
    basePath: '',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      'libs/angular.js',
      'libs/angular-mocks.js',
      'libs/jasmine-jquery.js',
      'libs/jquery-1.11.0.js',
      'libs/underscore.js',
      '../src/main/webapp/clientlibs-root/io.wcm.config.editor/js/**/*.js',
      'test/*.js',
      {pattern: 'test/fixtures/*.json', watched: true, served: true, included: false}
    ],

    preprocessors: {
      "../src/main/webapp/clientlibs-root/io.wcm.config.editor/js/**/*.js": "coverage"
    },

    // list of files / patterns to exclude
    exclude: [],

    // web server port
    port: 9876,

    // cli runner port
    runnerPort: 9100,

    reporters: ["dots", "coverage"],

    coverageReporter: {
      type: "html",
      dir: "../target/coverage"
    },
    junitReporter: {
      outputFile: "../target/surefire-reports/karma-results.xml"
    },

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: ['Chrome'],


    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: true
  });
};
