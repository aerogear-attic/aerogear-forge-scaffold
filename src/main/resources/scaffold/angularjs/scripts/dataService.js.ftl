"use strict";

${project.projectName}.factory( "dataService", function() {
  return {
<#if hasSecurity>
    restAuth : AeroGear.Auth({
      name: "auth",
      settings: {
        agAuth: true,
 <#if hasCordova>
        baseURL: "${baseURL}",
 </#if>
        endpoints: {
          enroll: "register",
          login: "login",
          logout: "logout"
        }
      }
    }).modules.auth,
    <#list securityMap.entities as entitySecurity>
    ${entitySecurity.name?uncap_first}Pipe: AeroGear.Pipeline({
      name: "${entitySecurity.name?lower_case}s",
      settings: {
        authenticator: this.restAuth
    <#if hasCordova>,
        baseURL: "${baseURL}"
    </#if>
      }
    }).pipes.${entitySecurity.name?lower_case}s,

    ${entitySecurity.name?uncap_first}Store: AeroGear.DataManager({
      name: "${entitySecurity.name}",
      type: "SessionLocal",
      settings: {
        storageType: "localStorage"
      }
    }).stores.${entitySecurity.name}<#if entitySecurity_has_next>,</#if>
    </#list>
<#else>
    <#list entityNames as entityName>
    ${entityName?uncap_first}Pipe: AeroGear.Pipeline({
      name: "${entityName?lower_case}s"
    }).pipes.${entityName?lower_case}s,

    ${entityName?uncap_first}Store: AeroGear.DataManager({
      name: "${entityName}",
      type: "SessionLocal",
      settings: {
        storageType: "localStorage"
      }
    }).stores.${entityName}<#if entityName_has_next>,</#if>
    </#list>
</#if>
  };
});