'use strict';

var ${project.projectName} = angular.module('${project.projectName}', ['${project.projectName}.filters'])
.config(['$routeProvider', function($routeProvider) {
$routeProvider
<#list entityNames as entityName>
.when('/${entityName}s',{templateUrl:'partials/${entityName}/search.html',controller:Search${entityName}Controller})
.when('/${entityName}s/new',{templateUrl:'partials/${entityName}/detail.html',controller:New${entityName}Controller})
.when('/${entityName}s/edit/:${entityName}Id',{templateUrl:'partials/${entityName}/detail.html',controller:Edit${entityName}Controller})
</#list>
.otherwise({
redirectTo: '/'
});
}]);
