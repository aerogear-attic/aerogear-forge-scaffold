<!doctype html>
<html lang="en" ng-app="${project.projectName}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${project.projectName}</title>
    <link href="styles/bootstrap.css" rel="stylesheet" media="screen">
    <link href="styles/main.css" rel="stylesheet" media="screen">
    <link href="styles/bootstrap-responsive.css" rel="stylesheet" media="screen">
    <script src="scripts/vendor/jquery.min.js"></script>
    <script src="scripts/vendor/aerogear.js"></script>
    <script src="scripts/vendor/angular.js"></script>
    <script src="scripts/vendor/bootstrap.min.js"></script>
    <script src="scripts/app.js"></script>
    <script src="scripts/services/dataService.js"></script>
<#list entityNames as entityName>
    <script src="${entityName}Controllers.js"></script>
</#list>
<#if hasSecurity>
    <script src="LoginController.js"></script>
</#if>
    <script src="scripts/filters.js"></script>
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="#">${project.projectName}</a>
        </div>
    </div>
</div>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <img src="images/forge-logo.png" alt="JBoss Forge"></img>
            <nav class="well sidebar-nav"  <#if hasSecurity>ng-controller="LoginController"</#if>>
            <#if hasSecurity>
                <form>
                    <div ng-show="isLoggedIn()">
                        <button id="logout" name="logout" class="btn" ng-click="logout()">Log out</button>
                    </div>
                    <div ng-show="!isLoggedIn()">
                        <div>
                            <input type="text" class="input-small" name="username" id="username"
                                   ng-model="user.username" placeholder="username"></input>
                            <input type="password" class="input-small" name="password" id="password"
                                   ng-model="user.password" placeholder="password"></input>
                        </div>
                        <div>
                            <button id="login" name="login" class="btn btn-small btn-primary" ng-click="login()">Sign
                                in
                            </button>
                            <button id="enroll" name="login" class="btn btn-small btn-inverse" ng-click="enroll()">Sign
                                up
                            </button>
                        </div>
                    </div>
                </form>
            </#if>
                <ul id="sidebar-entries" class="nav nav-list">
                <#list entityNames as entityName>
                    <li><a href="#/${entityName}s">${entityName}s</a></li>
                </#list>
                </ul>
            </nav>
        </div>
        <div class="span9">
        <#if hasSecurity>
            <div id="auth-error-box" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
                 aria-hidden="true">
                <div class="modal-header">
                    <h3 id="myModalLabel">Security Error</h3>
                </div>
                <div class="modal-body">
                    <p>Not Authorized !</p>

                    <div class="modal-footer">
                        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
                    </div>
                </div>
            </div>
        </#if>
            <div id="main" class="hero-unit" ng-view>

            </div>
        </div>
    </div>
</div>
</body>
</html>