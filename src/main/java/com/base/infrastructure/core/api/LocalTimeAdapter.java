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

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalTime;

/**
 * @author YISivlay
 */
public class LocalTimeAdapter implements JsonSerializer<LocalTime> {

    @Override
    @SuppressWarnings("unused")
    public JsonElement serialize(final LocalTime src, final Type typeOfSrc, final JsonSerializationContext context) {
        JsonArray array = null;
        if (src != null) {
            array = new JsonArray();
            array.add(new JsonPrimitive(src.getHour()));
            array.add(new JsonPrimitive(src.getMinute()));
            array.add(new JsonPrimitive(src.getSecond()));
        }
        return array;
    }

}
