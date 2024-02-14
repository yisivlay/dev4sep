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
package com.base.organisation.office.api;

import com.base.infrastructure.core.security.service.PlatformSecurityContext;
import com.base.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import com.base.organisation.office.data.OfficeData;
import com.base.organisation.office.service.OfficeReadPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * {@code @author:} YISivlay
 */
@Component
@Scope("singleton")
@Path("/offices")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OfficeApiResource {

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<OfficeData> toApiJsonSerializer;
    private final OfficeReadPlatformService officeReadPlatformService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll(@Context final UriInfo uriInfo,
                         @DefaultValue("false") @QueryParam("includeAllOffices") final boolean includeAllOffices) {
        this.context.authenticatedUser().validateHasReadPermission("OFFICE");

        final List<OfficeData> offices = this.officeReadPlatformService.getAll(includeAllOffices);

        return this.toApiJsonSerializer.serialize(offices);
    }

}
