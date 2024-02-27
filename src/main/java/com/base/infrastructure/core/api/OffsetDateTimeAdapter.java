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
package com.base.infrastructure.core.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author YISivlay
 */
public class OffsetDateTimeAdapter implements JsonSerializer<OffsetDateTime> {

    @Override
    @SuppressWarnings("unused")
    public JsonElement serialize(final OffsetDateTime dateTime, final Type typeOfSrc, final JsonSerializationContext context) {
        JsonElement object = null;
        if (dateTime != null) {
            object = new JsonPrimitive(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime));
        }
        return object;
    }

}
