/**
* JBoss, Home of Professional Open Source
* Copyright Red Hat, Inc., and individual contributors
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

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class PicketLinkDefaultUsers {


@Inject
private IdentityManager identityManager;

/**
* <p>Loads some users during the first construction.</p>
*/
@PostConstruct
public void create() {

<#list securityMap.users as user>
User ${user} = new SimpleUser("${user}");

/*
* Note: Password will be encoded in SHA-512 with SecureRandom-1024 salt
* See http://lists.jboss.org/pipermail/security-dev/2013-January/000650.html for more information
*/
this.identityManager.add(${user});
this.identityManager.updateCredential(${user}, new Password("123"));
</#list>
<#list securityMap.roles as role>

Role ${role} = new SimpleRole("${role}");
this.identityManager.add(${role});
</#list>
<#list securityMap.roleMap as roleMapEntry>
    <#list roleMapEntry.roles as singleRole>
    identityManager.grantRole(${roleMapEntry.user}, ${singleRole});
    </#list>
</#list>


}

}