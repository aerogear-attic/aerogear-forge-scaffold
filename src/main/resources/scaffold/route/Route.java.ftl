/*
* JBoss, Home of Professional Open Source
* Copyright 2012, Red Hat, Inc., and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ${packageName};

<#list entityNames as entityName>
import ${packageName}.model.${entityName};
import ${packageName}.rest.${entityName}Endpoint;
</#list>

import org.jboss.aerogear.controller.router.AbstractRoutingModule;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.RequestMethod;
<#if hasSecurity>
import org.jboss.aerogear.security.exception.AeroGearSecurityException;
import ${packageName}.exceptions.HttpSecurityException;
import ${packageName}.config.CustomMediaTypeResponder;
import ${packageName}.rest.Error;
import ${packageName}.rest.Login;
import ${packageName}.rest.Register;
import org.jboss.aerogear.security.model.AeroGearUser;
</#if>
public class Routes extends AbstractRoutingModule {

@Override
public void configuration() throws Exception {
<#if hasSecurity>
route()
.on(AeroGearSecurityException.class)
.produces(JSON)
.to(Error.class).index(param(HttpSecurityException.class));
route()
.from("/login")
.on(RequestMethod.POST)
.consumes(JSON)
.produces(CustomMediaTypeResponder.CUSTOM_MEDIA_TYPE)
.to(Login.class).login(param(AeroGearUser.class));

route()
.from("/logout")
.on(RequestMethod.POST)
.consumes(JSON)
.produces(CustomMediaTypeResponder.CUSTOM_MEDIA_TYPE)
.to(Login.class).logout();

route()
.from("/register")
.on(RequestMethod.POST)
.consumes(JSON)
.produces(CustomMediaTypeResponder.CUSTOM_MEDIA_TYPE)
.to(Register.class).register(param(AeroGearUser.class));

    <#list securityMap.entities as entitySecurity>

    route()
    .from("/${entitySecurity.name?lower_case}s")
        <#if entitySecurity.GET.authorization?has_content>
        .roles(<#list entitySecurity.GET.authorization as role>"${role}"<#if role_has_next>,</#if></#list>)
        </#if>
    .on(RequestMethod.GET)
    .consumes(JSON)
    .produces(JSON)
    .to(${entitySecurity.name}Endpoint.class).listAll();

    route()
    .from("/${entitySecurity.name?lower_case}s")
        <#if entitySecurity.POST.authorization?has_content>
        .roles(<#list entitySecurity.POST.authorization as role>"${role}"<#if role_has_next>,</#if></#list>)
        </#if>
    .on(RequestMethod.POST)
    .consumes(JSON)
    .produces(JSON)
    .to(${entitySecurity.name}Endpoint.class).create(param(${entitySecurity.name}.class));

    route()
    .from("/${entitySecurity.name?lower_case}s/{id}")
        <#if entitySecurity.DELETE.authorization?has_content>
        .roles(<#list entitySecurity.DELETE.authorization as role>"${role}"<#if role_has_next>,</#if></#list>)
        </#if>
    .on(RequestMethod.DELETE)
    .consumes(JSON)
    .produces(JSON)
    .to(${entitySecurity.name}Endpoint.class).deleteById(param("id",Long.class));

    route()
    .from("/${entitySecurity.name?lower_case}s/{id}")
        <#if entitySecurity.GETById.authorization?has_content>
        .roles(<#list entitySecurity.GETById.authorization as role>"${role}"<#if role_has_next>,</#if></#list>)
        </#if>
    .on(RequestMethod.GET)
    .consumes(JSON)
    .produces(JSON)
    .to(${entitySecurity.name}Endpoint.class).findById(param("id",Long.class));

    route()
    .from("/${entitySecurity.name?lower_case}s/{id}")
        <#if entitySecurity.PUT.authorization?has_content>
        .roles(<#list entitySecurity.PUT.authorization as role>"${role}"<#if role_has_next>,</#if></#list>)
        </#if>
    .on(RequestMethod.PUT)
    .consumes(JSON)
    .produces(JSON)
    .to(${entitySecurity.name}Endpoint.class).update(param("id",Long.class), param(${entitySecurity.name}.class));

    </#list>
<#else>
    <#list entityNames as entityName>
    route()
    .from("/${entityName?lower_case}s")
    .on(RequestMethod.GET)
    .consumes(JSON)
    .produces(JSON)
    .to(${entityName}Endpoint.class).listAll();
    route()
    .from("/${entityName?lower_case}s")
    .on(RequestMethod.POST)
    .consumes(JSON)
    .produces(JSON)
    .to(${entityName}Endpoint.class).create(param(${entityName}.class));
    route()
    .from("/${entityName?lower_case}s/{id}")
    .on(RequestMethod.DELETE)
    .consumes(JSON)
    .produces(JSON)
    .to(${entityName}Endpoint.class).deleteById(param("id",Long.class));
    route()
    .from("/${entityName?lower_case}s/{id}")
    .on(RequestMethod.GET)
    .consumes(JSON)
    .produces(JSON)
    .to(${entityName}Endpoint.class).findById(param("id",Long.class));
    route()
    .from("/${entityName?lower_case}s/{id}")
    .on(RequestMethod.PUT)
    .consumes(JSON)
    .produces(JSON)
    .to(${entityName}Endpoint.class).update(param("id",Long.class), param(${entityName}.class));
    </#list>
</#if>

}

}

