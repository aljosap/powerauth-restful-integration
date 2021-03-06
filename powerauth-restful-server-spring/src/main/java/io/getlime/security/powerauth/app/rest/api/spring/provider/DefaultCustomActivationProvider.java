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
package io.getlime.security.powerauth.app.rest.api.spring.provider;

import io.getlime.security.powerauth.rest.api.base.provider.CustomActivationProvider;
import io.getlime.security.powerauth.rest.api.model.entity.ActivationType;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Default implementation of CustomActivationProvider interface.
 *
 * @author Roman Strobl, roman.strobl@wultra.com
 */
@Component
public class DefaultCustomActivationProvider implements CustomActivationProvider {

    @Override
    public String lookupUserIdForAttributes(Map<String, String> identityAttributes) {
        return identityAttributes.get("username");
    }

    @Override
    public void processCustomActivationAttributes(Map<String, Object> customAttributes, String activationId, String userId, ActivationType activationType) {
    }

    @Override
    public boolean shouldAutoCommitActivation(Map<String, String> identityAttributes, Map<String, Object> customAttributes, String activationId, String userId) {
        return true;
    }
}
