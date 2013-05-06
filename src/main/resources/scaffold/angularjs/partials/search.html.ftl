<h2>${entityName}s List</h2>
<form id="${entityName}Search" class="form-horizontal">
    <div class="control-group">
        <div class="controls">
            <a id="Create" name="Create" class="btn" href="#/${entityName}s/new"><i class="icon-plus-sign"></i> Create
                New ${entityName}</a>
        </div>
    </div>
</form>
<div id="search-results">
    <table class="table table-bordered">
        <thead>
        <tr>
        <#list properties as property>
            <#if (property.hidden!"false") != "true">
                <th>${property.name?cap_first}</th>
            </#if>
        </#list>
        </tr>
        </thead>
        <tbody id="search-results-body">
        <tr ng-repeat="result in searchResults | filter:filterSearchResults | startFrom:currentPage*pageSize | limitTo:pageSize">
        <#list properties as property>
            <#if (property.hidden!"false") != "true">
                <#if (property["many-to-one"]!"false") == "true" || (property["one-to-one"]!"false") == "true">
                    <td><a href="#/${entityName}s/edit/{{result.id}}">{{result.${property.name}.id}}</a></td>
                <#else>
                    <td><a href="#/${entityName}s/edit/{{result.id}}">{{result.${property.name}}}</a></td>
                </#if>
            </#if>
        </#list>
        </tr>
        </tbody>
    </table>
    <div class="pagination pagination-centered">
        <ul>
            <li ng-class="{disabled:currentPage == 0}">
                <a id="prev" href ng-click="previous()">«</a>
            </li>
            <li ng-repeat="n in pageRange" ng-class="{active:currentPage == n}" ng-click="setPage(n)">
                <a href ng-bind="n + 1">1</a>
            </li>
            <li ng-class="{disabled: currentPage == (numberOfPages() - 1)}">
                <a id="next" href ng-click="next()">»</a>
            </li>
        </ul>
    </div>
</div>