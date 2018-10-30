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
package io.getlime.security.powerauth.app.rest.api.spring.controller;

import io.getlime.core.rest.model.base.request.ObjectRequest;
import io.getlime.core.rest.model.base.response.ObjectResponse;
import io.getlime.powerauth.soap.v2.CreateActivationResponse;
import io.getlime.security.powerauth.app.rest.api.spring.provider.PowerAuthUserProvider;
import io.getlime.security.powerauth.rest.api.base.encryption.PowerAuthNonPersonalizedEncryptor;
import io.getlime.security.powerauth.rest.api.base.exception.PowerAuthActivationException;
import io.getlime.security.powerauth.rest.api.model.entity.NonPersonalizedEncryptedPayloadModel;
import io.getlime.security.powerauth.rest.api.model.request.v2.ActivationCreateCustomRequest;
import io.getlime.security.powerauth.rest.api.model.request.v2.ActivationCreateRequest;
import io.getlime.security.powerauth.rest.api.model.response.v2.ActivationCreateResponse;
import io.getlime.security.powerauth.rest.api.spring.encryption.EncryptorFactory;
import io.getlime.security.powerauth.soap.spring.client.PowerAuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * Example controller for a custom activation implementation.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Controller
@RequestMapping(value = "/pa/activation/direct")
public class CustomActivationController {

    private PowerAuthServiceClient powerAuthClient;

    private EncryptorFactory encryptorFactory;

    private PowerAuthUserProvider userProvider;

    @Autowired
    public void setPowerAuthClient(PowerAuthServiceClient powerAuthClient) {
        this.powerAuthClient = powerAuthClient;
    }

    @Autowired
    public void setEncryptorFactory(EncryptorFactory encryptorFactory) {
        this.encryptorFactory = encryptorFactory;
    }

    @Autowired(required = false)
    public void setUserProvider(PowerAuthUserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public @ResponseBody ObjectResponse<NonPersonalizedEncryptedPayloadModel> createNewActivation(
            @RequestBody ObjectRequest<NonPersonalizedEncryptedPayloadModel> object
    ) throws PowerAuthActivationException {
        try {

            // Check if there is any user provider to be autowired
            if (userProvider == null) {
                throw new PowerAuthActivationException();
            }

            // Prepare an encryptor
            final PowerAuthNonPersonalizedEncryptor encryptor = encryptorFactory.buildNonPersonalizedEncryptor(object);
            if (encryptor == null) {
                throw new PowerAuthActivationException();
            }

            // Decrypt the request object
            ActivationCreateCustomRequest request = encryptor.decrypt(object, ActivationCreateCustomRequest.class);

            if (request == null) {
                throw new PowerAuthActivationException();
            }

            // Lookup user ID using a provided identity
            final Map<String, String> identity = request.getIdentity();
            String userId = userProvider.lookupUserIdForAttributes(identity);

            // If no user was found, return error
            if (userId == null) {
                throw new PowerAuthActivationException();
            }

            // Create activation for a looked up user and application related to the given application key
            ActivationCreateRequest acr = request.getPowerauth();
            CreateActivationResponse response = powerAuthClient.v2().createActivation(
                    acr.getApplicationKey(),
                    userId,
                    acr.getActivationIdShort(),
                    acr.getActivationName(),
                    acr.getActivationNonce(),
                    acr.getEphemeralPublicKey(),
                    acr.getEncryptedDevicePublicKey(),
                    acr.getExtras(),
                    acr.getApplicationSignature()
            );

            // Process custom attributes using a custom logic
            final Map<String, Object> customAttributes = request.getCustomAttributes();
            userProvider.processCustomActivationAttributes(customAttributes);

            // Prepare the created activation response data
            ActivationCreateResponse createResponse = new ActivationCreateResponse();
            createResponse.setActivationId(response.getActivationId());
            createResponse.setEphemeralPublicKey(response.getEphemeralPublicKey());
            createResponse.setActivationNonce(response.getActivationNonce());
            createResponse.setEncryptedServerPublicKey(response.getEncryptedServerPublicKey());
            createResponse.setEncryptedServerPublicKeySignature(response.getEncryptedServerPublicKeySignature());

            // Encrypt response object
            final ObjectResponse<NonPersonalizedEncryptedPayloadModel> powerAuthApiResponse = encryptor.encrypt(createResponse);

            // Check if activation should be committed instantly and if yes, perform commit
            if (userProvider.shouldAutoCommitActivation(identity, customAttributes)) {
                powerAuthClient.commitActivation(response.getActivationId());
            }

            // Return response
            return powerAuthApiResponse;

        } catch (IOException e) {
            throw new PowerAuthActivationException();
        }

    }

}
