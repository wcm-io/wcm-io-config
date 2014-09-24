describe("parameters service", function() {
  var parameters, httpBackend;

  beforeEach(function() {
    module("io.wcm.config.services", "testApp");

    angular.module("testApp", function() { }).config(function(parametersProvider){
      parametersProvider.setConfig({url:"http://localhost/test.json"});
    });

    jasmine.getJSONFixtures().fixturesPath='/base/test/fixtures';

    inject(function(_parameters_, $httpBackend) {
      httpBackend = $httpBackend;
      parameters = _parameters_;

      httpBackend.whenGET("http://localhost/test.json").respond(
        getJSONFixture('parameters.json')
      );
    });
  });

  it("should load parameters from configured url", function() {
    parameters.loadParameters().then(function(respond) {
      expect(respond.data.parameters).not.toBeUndefined();
      expect(respond.data.parameters.length).toEqual(5);
    });
    httpBackend.flush();
  });

  it("should extract group filters from parameters", function() {
    parameters.loadParameters().then(function(respond) {
      var results = parameters.parseData(respond.data);
      expect(results).not.toBeUndefined();

      expect(results.filters).not.toBeUndefined();
      expect(results.filters[0]).not.toBeUndefined();
      expect(results.filters[0].options.length).toBe(2);
    });
    httpBackend.flush();

  });

  it("should extract application filters from parameters", function() {
    parameters.loadParameters().then(function(respond) {
      var results = parameters.parseData(respond.data);
      expect(results).not.toBeUndefined();

      expect(results.filters).not.toBeUndefined();
      expect(results.filters[1]).not.toBeUndefined();
      expect(results.filters[1].options.length).toBe(2);
    });
    httpBackend.flush();
  });

  it("should add parameters to the result", function() {
    parameters.loadParameters().then(function(respond) {
      var results = parameters.parseData(respond.data);
      expect(results).not.toBeUndefined();

      expect(results.parameters).not.toBeUndefined();
      expect(results.parameters.length).toBe(5);
    });
    httpBackend.flush();
  });

});