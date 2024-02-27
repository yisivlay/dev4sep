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
package com.base.infrastructure.core.debug;

import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * @author YISivlay
 */
@Component
@Path("/echo")
public class EchoHeadersApiResource {

    @GET
    @Consumes({MediaType.WILDCARD})
    @Produces({MediaType.TEXT_PLAIN})
    public String get(@Context HttpHeaders headers) {
        var sb = new StringBuilder("Request Headers:\n");
        headers.getRequestHeaders().forEach((k, v) -> sb.append(k).append(" : ").append(v.get(0)).append("\n"));
        return sb.toString();
    }

}
