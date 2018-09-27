/*
 * PowerAuth integration libraries for RESTful API applications, examples and
 * related software components
 *
 * Copyright (C) 2017 Lime - HighTech Solutions s.r.o.
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

package io.getlime.security.powerauth.rest.api.base.encryption;

/**
 * Interface for authentication objects used for ECIES encryption. This object mirrors
 * data that are transmitted in "X-PowerAuth-Encryption" header.
 *
 * @author Roman Strobl, roman.strobl@wultra.com
 */
public interface PowerAuthEciesEncryption {

    /**
     * Get application key.
     * @return Application key.
     */
    String getApplicationKey();

    /**
     * Get activation ID.
     * @return Activation ID.
     */
    String getActivationId();

    /**
     * Get PowerAuth protocol version.
     * @return PowerAuth protocol version.
     */
    String getVersion();
}
