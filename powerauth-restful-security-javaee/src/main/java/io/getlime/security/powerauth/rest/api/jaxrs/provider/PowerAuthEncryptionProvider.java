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
package io.getlime.security.powerauth.rest.api.jaxrs.provider;

import io.getlime.powerauth.soap.v3.PowerAuthPortV3ServiceStub;
import io.getlime.security.powerauth.rest.api.base.encryption.PowerAuthEciesDecryptorParameters;
import io.getlime.security.powerauth.rest.api.base.exception.PowerAuthEncryptionException;
import io.getlime.security.powerauth.rest.api.base.provider.PowerAuthEncryptionProviderBase;
import io.getlime.security.powerauth.soap.axis.client.PowerAuthServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.rmi.RemoteException;

/**
 * Implementation of PowerAuth encryption provider.
 *
 * @author Roman Strobl, roman.strobl@wultra.com
 *
 */
@Stateless
public class PowerAuthEncryptionProvider extends PowerAuthEncryptionProviderBase  {

    private static final Logger logger = LoggerFactory.getLogger(PowerAuthEncryptionProvider.class);

    @Inject
    private PowerAuthServiceClient powerAuthClient;

    @Override
    public PowerAuthEciesDecryptorParameters getEciesDecryptorParameters(String activationId, String applicationKey, String ephemeralPublicKey) throws PowerAuthEncryptionException {
        try {
            PowerAuthPortV3ServiceStub.GetEciesDecryptorRequest eciesDecryptorRequest = new PowerAuthPortV3ServiceStub.GetEciesDecryptorRequest();
            eciesDecryptorRequest.setActivationId(activationId);
            eciesDecryptorRequest.setApplicationKey(applicationKey);
            eciesDecryptorRequest.setEphemeralPublicKey(ephemeralPublicKey);
            PowerAuthPortV3ServiceStub.GetEciesDecryptorResponse eciesDecryptorResponse = powerAuthClient.getEciesDecryptor(eciesDecryptorRequest);
            return new PowerAuthEciesDecryptorParameters(eciesDecryptorResponse.getSecretKey(), eciesDecryptorResponse.getSharedInfo2());
        } catch (RemoteException e) {
            logger.warn("Get Ecies decryptor parameters call failed", e);
            throw new PowerAuthEncryptionException();
        }
    }

}
