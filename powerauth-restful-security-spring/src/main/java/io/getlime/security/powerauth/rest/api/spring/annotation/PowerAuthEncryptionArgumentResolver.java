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

package io.getlime.security.powerauth.rest.api.spring.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getlime.security.powerauth.rest.api.base.encryption.PowerAuthEciesEncryption;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Argument resolver for {@link PowerAuthEciesEncryption} objects. It enables automatic
 * parameter resolution for methods that are annotated via {@link PowerAuthEciesEncryption} annotation.
 *
 * @author Roman Strobl, roman.strobl@wultra.com
 */
public class PowerAuthEncryptionArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasMethodAnnotation(PowerAuthEncryption.class)
                && (parameter.hasParameterAnnotation(EncryptedRequestBody.class) || PowerAuthEciesEncryption.class.isAssignableFrom(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final PowerAuthEciesEncryption eciesObject = (PowerAuthEciesEncryption) request.getAttribute(PowerAuthEncryption.ENCRYPTION_OBJECT);
        // Decrypted object is inserted into parameter annotated by @EncryptedRequestBody annotation
        if (parameter.hasParameterAnnotation(EncryptedRequestBody.class) && eciesObject != null && eciesObject.getDecryptedRequest() != null) {
            final Class<?> parameterType = parameter.getParameterType();
            try {
                return objectMapper.readValue(eciesObject.getDecryptedRequest(), parameterType);
            } catch (IOException e) {
                return null;
            }
        }
        // Ecies encryption object is inserted into parameter which is of type PowerAuthEciesEncryption
        if (PowerAuthEciesEncryption.class.isAssignableFrom(parameter.getParameterType())) {
            return eciesObject;
        }
        return null;
    }

}
