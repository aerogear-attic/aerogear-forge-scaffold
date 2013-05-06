'use strict';

angular.module('${project.projectName}.filters',[]).filter('startFrom', function() {
return function(input, start) {
start = +start; //parse to int
return input.slice(start);
};
});