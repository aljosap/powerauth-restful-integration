/*
 * PowerAuth integration libraries for RESTful API applications, examples and
 * related software components
 *
 * Copyright (C) 2018 Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.getlime.security.powerauth.rest.api.jaxrs.application;

import io.getlime.security.powerauth.rest.api.base.application.PowerAuthApplicationConfiguration;

import java.util.Map;

/**
 * Default (empty) implementation of application configuration.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
public class DefaultApplicationConfiguration implements PowerAuthApplicationConfiguration {

    public DefaultApplicationConfiguration() {
    }

    @Override
    public boolean isAllowedApplicationKey(String applicationKey) {
        return true;
    }

    @Override
    public Map<String, Object> statusServiceCustomObject() {
        return null;
    }
}
