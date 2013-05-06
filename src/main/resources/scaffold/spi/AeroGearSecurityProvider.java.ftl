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

import ${packageName}.exceptions.HttpSecurityException;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.spi.SecurityProvider;
import org.jboss.aerogear.security.exception.AeroGearSecurityException;
import org.jboss.aerogear.security.exception.HttpStatus;
import org.jboss.aerogear.security.idm.AeroGearCredential;

import javax.inject.Inject;
import javax.servlet.ServletException;

/**
* Security SPI for AeroGear Controller
*/
public class AeroGearSecurityProvider implements SecurityProvider {

@Inject
private AeroGearCredential credential;

/**
* Route validation support on AeroGear Controller
* @param route the {@link Route} for which this provider to determine access.
* @throws ServletException
*/
@Override
public void isRouteAllowed(Route route) throws ServletException {

if (!credential.hasRoles(route.getRoles())) {
throw new HttpSecurityException(HttpStatus.AUTHENTICATION_FAILED);
}

}
}
