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

import ${packageName}.rest.ResponseHeaders;
import org.jboss.aerogear.security.auth.AuthenticationManager;
import org.jboss.aerogear.security.auth.Token;
import org.jboss.aerogear.security.model.AeroGearUser;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
public class Login {

private static final Logger LOGGER = Logger.getLogger(Login.class.getSimpleName());

private static final String AUTH_TOKEN = "Auth-Token";

@Inject
private AuthenticationManager authenticationManager;

@Inject
@Token
private Instance
<String> token;

    @Inject
    Event
    <ResponseHeaders> headers;

        public void index() {
        LOGGER.info("Login page!");
        }

        /**
        * {@link org.jboss.aerogear.security.model.AeroGearUser} registration
        *
        * @param user represents a simple implementation that holds user's credentials.
        * @return HTTP response and the session ID
        */
        public AeroGearUser login(final AeroGearUser user) {
        performLogin(user);
        fireResponseHeaderEvent();
        return user;
        }

        public void logout() {
        LOGGER.info("User logout!");
        authenticationManager.logout();
        }

        private void performLogin(AeroGearUser aeroGearUser) {
        authenticationManager.login(aeroGearUser);
        }

        private void fireResponseHeaderEvent() {
        headers.fire(new ResponseHeaders(AUTH_TOKEN, token.get().toString()));
        }
        }
