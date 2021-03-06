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
package io.getlime.security.powerauth.rest.api.jaxrs.encryption;

import io.getlime.core.rest.model.base.request.ObjectRequest;
import io.getlime.powerauth.soap.v2.PowerAuthPortV2ServiceStub;
import io.getlime.security.powerauth.rest.api.base.encryption.PowerAuthNonPersonalizedEncryptor;
import io.getlime.security.powerauth.rest.api.model.entity.NonPersonalizedEncryptedPayloadModel;
import io.getlime.security.powerauth.soap.axis.client.PowerAuthServiceClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.rmi.RemoteException;

/**
 * Class responsible for building encryptors.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Stateless
public class EncryptorFactory {

    @Inject
    private PowerAuthServiceClient powerAuthClient;

    public EncryptorFactory() {
    }

    /**
     * Return a new instance of a non-personalized encryptor.
     * @param object Request object to be used to initialize a new encryptor.
     * @return New instance of a non-personalized encryptor.
     * @throws RemoteException In case a SOAP exception occurs.
     */
    public PowerAuthNonPersonalizedEncryptor buildNonPersonalizedEncryptor(ObjectRequest<NonPersonalizedEncryptedPayloadModel> object) throws RemoteException {
        return this.buildNonPersonalizedEncryptor(
                object.getRequestObject().getApplicationKey(),
                object.getRequestObject().getSessionIndex(),
                object.getRequestObject().getEphemeralPublicKey()
        );
    }

    /**
     * Return a new instance of a non-personalized encryptor.
     * @param applicationKeyBase64 Application key associated with an application master key used for encryption.
     * @param sessionIndexBase64 Session index.
     * @param ephemeralPublicKeyBase64 Ephemeral public key.
     * @return New instance of a non-personalized encryptor.
     * @throws RemoteException In case a SOAP exception occurs.
     */
    public PowerAuthNonPersonalizedEncryptor buildNonPersonalizedEncryptor(String applicationKeyBase64, String sessionIndexBase64, String ephemeralPublicKeyBase64) throws RemoteException {
        final PowerAuthPortV2ServiceStub.GetNonPersonalizedEncryptionKeyResponse encryptionKeyResponse = powerAuthClient.v2().generateNonPersonalizedE2EEncryptionKey(
                applicationKeyBase64,
                ephemeralPublicKeyBase64,
                sessionIndexBase64
        );
        return new PowerAuthNonPersonalizedEncryptor(
                encryptionKeyResponse.getApplicationKey(),
                encryptionKeyResponse.getEncryptionKey(), encryptionKeyResponse.getEncryptionKeyIndex(),
                encryptionKeyResponse.getEphemeralPublicKey()
        );
    }

}
