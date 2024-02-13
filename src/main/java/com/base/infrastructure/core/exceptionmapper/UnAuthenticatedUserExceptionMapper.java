/**
 * Copyright 2024 DEV4Sep
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.infrastructure.core.exceptionmapper;

import com.base.infrastructure.core.data.ApiGlobalErrorResponse;
import com.base.infrastructure.core.exception.UnAuthenticatedUserException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * {@code @author:} YISivlay
 */
@Provider
@Component
@Scope("singleton")
public class UnAuthenticatedUserExceptionMapper implements ExceptionMapper<UnAuthenticatedUserException> {
    @Override
    public Response toResponse(UnAuthenticatedUserException exception) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(ApiGlobalErrorResponse.unAuthenticated())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
