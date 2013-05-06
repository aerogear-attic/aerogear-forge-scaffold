<form id="new-${entityName}" name="new${entityName}" class="form-horizontal">
<#list properties as property>
    <div class="control-group">
        <#if (property.hidden!"false") == "false">
            <label for="${property.name}" class="control-label">${property.name?cap_first}</label>

            <div class="controls">
                <#if (property["many-to-one"]!"false") == "true" || (property["one-to-one"]!"false") == "true">
                    <select id="${property.name}" name="${property.name}"
                            ng-model="${entityName?uncap_first}.${property.name}"
                            ng-options="${property.name?substring(0, 1)} as ${property.name?substring(0, 1)}.id for ${property.name?substring(0, 1)} in ${property.name}List">
                        <option value="">Choose a ${property.name?cap_first}</option>
                    </select>
                <#else>
                    <input id="${property.name}" name="${property.name}" <#if property.type == "number">type="number"
                           <#elseif (property["datetime-type"]!"") == "date">type="date"
                           <#elseif (property["datetime-type"]!"") == "time">type="time"
                           <#elseif (property["datetime-type"]!"") == "both">type="datetime"<#else>type="text"</#if>
                           <#if property.required!"false" == "true">required</#if>
                           ng-model="${entityName?uncap_first}.${property.name}"
                           placeholder="Enter the ${entityName} ${property.name}"></input>
                </#if>
            </div>
        </#if>
    </div>
</#list>
    <div class="control-group">
        <div class="controls">
            <button id="save${entityName}" name="save${entityName}" class="btn btn-primary"
                    ng-disabled="isClean() || new${entityName}.$invalid" ng-click="save()"><i
                    class="icon-ok-sign icon-white"></i> Save
            </button>
            <button id="cancel" name="cancel" class="btn" ng-click="cancel()"><i class="icon-remove-sign"></i> Cancel
            </button>
            <button id="delete${entityName}" name="delete${entityName}" class="btn"
                    ng-show="${entityName?lower_case}.id" ng-click="remove()"><i class="icon-trash"></i> Delete
            </button>
        </div>
        <div>
</form>