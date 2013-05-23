'use strict';

function Search${entityName}Controller($scope,$filter,dataService) {
  $scope.filter = $filter;
  $scope.search={};
  $scope.currentPage = 0;
  $scope.pageSize= 10;
  $scope.searchResults = [];
  $scope.pageRange = [];
  $scope.numberOfPages = function() {
    var result = Math.ceil($scope.searchResults.length/$scope.pageSize);
    return (result == 0) ? 1 : result;
  };
  var ${entityName?uncap_first}Pipe = dataService.${entityName?uncap_first}Pipe;
  var ${entityName?uncap_first}Store = dataService.${entityName?uncap_first}Store;
<#list properties as property>
    <#if (property["many-to-one"]!"false") == "true" || (property["one-to-one"]!"false") == "true">
  var ${property.simpleType?uncap_first}Pipe = dataService.${property.simpleType?uncap_first}Pipe;
  ${property.simpleType?uncap_first}Pipe.read({
    success: function(data){
      $scope.${property.name}List = data;
      $scope.$apply();
    }
        <#if hasSecurity>,
      statusCode: {
        401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
          }
        }
        </#if>
    });
    </#if>
</#list>

  $scope.performSearch = function() {
    ${entityName?uncap_first}Pipe.read({
      success: function(data){
        $scope.searchResults = data;
        var max = $scope.numberOfPages();
        $scope.pageRange = [];
        for(var ctr=0;ctr
<max;ctr++) {
          $scope.pageRange.push(ctr);
        }
        $scope.$apply();
  }
<#if hasSecurity>,
      statusCode: {
        401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
        }
      }
</#if>
    });
  };

  $scope.previous = function() {
    if($scope.currentPage > 0) {
      $scope.currentPage--;
    }
  };

  $scope.next = function() {
    if($scope.currentPage < ($scope.numberOfPages() - 1) ) {
    $scope.currentPage++;
    }
  };

  $scope.setPage = function(n) {
    $scope.currentPage = n;
  };

  $scope.filterSearchResults = function(result) {
    var flag = true;
    for(var key in $scope.search){
      if($scope.search.hasOwnProperty(key)) {
        var expected = $scope.search[key];
        if(expected == null || expected === "") {
          continue;
        }
        var actual = result[key];
        if(angular.isObject(expected)) {
          flag = flag && angular.equals(expected,actual);
        }
        else {
          flag = flag && (actual.toString().indexOf(expected.toString()) != -1);
        }
        if(flag === false) {
          return false;
        }
      }
    }
    return true;
  };

  $scope.performSearch();
};

function New${entityName}Controller($scope,$location,dataService) {
  var ${entityName?uncap_first}Pipe = dataService.${entityName?uncap_first}Pipe;
  $scope.disabled = false;

<#list properties as property>
    <#if (property["many-to-one"]!"false") == "true" || (property["one-to-one"]!"false") == "true">
  var ${property.simpleType?uncap_first}Pipe = dataService.${property.simpleType?uncap_first}Pipe;
  ${property.simpleType?uncap_first}Pipe.read({
    success: function(data){
      $scope.${property.name}List = data;
      $scope.$apply();
    }
        <#if hasSecurity>,
    statusCode: {
      401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
        }
      }
    </#if>
    });
    </#if>
</#list>

  $scope.save = function() {
    ${entityName?uncap_first}Pipe.save($scope.${entityName?uncap_first},{
      success: function(data){
        $location.path('/${entityName}s');
        $scope.$apply();
      }
<#if hasSecurity>,
      statusCode: {
        401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
        }
      }
</#if>
    });
  };

  $scope.cancel = function() {
    $location.path("/${entityName}s");
  };
};

function Edit${entityName}Controller($scope,$routeParams,$location,dataService) {
  var self = this;
  $scope.disabled = false;
  var ${entityName?uncap_first}Pipe = dataService.${entityName?uncap_first}Pipe;
<#list properties as property>
    <#if (property["many-to-one"]!"false") == "true" || (property["one-to-one"]!"false") == "true">
  var ${property.simpleType?uncap_first}Pipe = dataService.${property.simpleType?uncap_first}Pipe;
    </#if>
</#list>

  $scope.get = function() {
    ${entityName?uncap_first}Pipe.read({
      id: $routeParams.${entityName}Id,
      success: function(data){
        self.original = data.entity;
        $scope.${entityName?uncap_first} = data.entity;
<#list properties as property>
    <#if (property["many-to-one"]!"false") == "true" || (property["one-to-one"]!"false") == "true">
      ${property.simpleType?uncap_first}Pipe.read({
          success: function(data){
            $scope.${property.name}List = data;
            angular.forEach($scope.${property.name}List, function(datum){
              if(angular.equals(datum,$scope.${entityName?uncap_first}.${property.name})) {
                $scope.${entityName?uncap_first}.${property.name} = datum;
                self.original.${property.name} = datum;
              }
            });
            $scope.$apply();
          }
        });
    </#if>
</#list>
        $scope.$apply();
      }
<#if hasSecurity>,
      statusCode: {
        401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
        }
      }
</#if>
    });
  };

  $scope.isClean = function() {
    return angular.equals(self.original, $scope.${entityName?uncap_first});
  };

  $scope.save = function() {
    ${entityName?uncap_first}Pipe.save($scope.${entityName?uncap_first},{
      success: function(data){
        $location.path('/${entityName}s');
        $scope.$apply();
  }
<#if hasSecurity>,
      statusCode: {
        401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
        }
      }
</#if>
    });
  };

  $scope.cancel = function() {
    $location.path("/${entityName}s");
  };

  $scope.remove = function() {
    ${entityName?uncap_first}Pipe.remove($scope.${entityName?uncap_first},{
      success: function(data){
        $location.path('/${entityName}s');
        $scope.$apply();
      }
<#if hasSecurity>,
      statusCode: {
        401: function( jqXHR ) {
          $( "#auth-error-box" ).modal();
        }
      }
</#if>
    });
  };
  $scope.get();
};