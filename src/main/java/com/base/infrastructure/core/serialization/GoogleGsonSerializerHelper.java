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
package com.base.infrastructure.core.serialization;

import com.base.infrastructure.core.api.*;
import com.base.infrastructure.core.exception.UnsupportedParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code @author:} YISivlay
 */
@Service
public class GoogleGsonSerializerHelper {

    public static Gson createSimpleGson() {
        return createGsonBuilder().create();
    }

    public static GsonBuilder createGsonBuilder() {
        return createGsonBuilder(false);
    }

    public static GsonBuilder createGsonBuilder(final boolean prettyPrint) {
        final var builder = new GsonBuilder();
        registerTypeAdapters(builder);
        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        return builder;
    }

    public static void registerTypeAdapters(final GsonBuilder builder) {
        builder.registerTypeAdapter(java.util.Date.class, new DateAdapter());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeAdapter());
        builder.registerTypeAdapter(ZonedDateTime.class, new JodaDateTimeAdapter());
        builder.registerTypeAdapter(MonthDay.class, new JodaMonthDayAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        builder.registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter());
    }

    public Gson createGsonBuilderForPartialResponseFiltering(final boolean prettyPrint, final Set<String> responseParameters) {
        final var strategy = new ParameterListInclusionStrategy(responseParameters);

        final var builder = new GsonBuilder().addSerializationExclusionStrategy(strategy);
        registerTypeAdapters(builder);
        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

    public Gson createGsonBuilderWithParameterExclusionSerializationStrategy(final Set<String> supportedParameters,
                                                                             final boolean prettyPrint,
                                                                             final Set<String> responseParameters) {

        final Set<String> parameterNamesToSkip = new HashSet<>();

        if (!responseParameters.isEmpty()) {
            // strip out all known support parameters from expected response to
            // see if unsupported parameters requested for response.
            final var differentParametersDetectedSet = new HashSet<>(responseParameters);
            differentParametersDetectedSet.removeAll(supportedParameters);

            if (!differentParametersDetectedSet.isEmpty()) {
                throw new UnsupportedParameterException(new ArrayList<>(differentParametersDetectedSet));
            }

            parameterNamesToSkip.addAll(supportedParameters);
            parameterNamesToSkip.removeAll(responseParameters);
        }

        final var strategy = new ParameterListExclusionStrategy(parameterNamesToSkip);

        final var builder = new GsonBuilder().addSerializationExclusionStrategy(strategy);
        registerTypeAdapters(builder);
        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

    public String serializedJsonFrom(final Gson serializer, final Object[] dataObjects) {
        return serializer.toJson(dataObjects);
    }

    public String serializedJsonFrom(final Gson serializer, final Object singleDataObject) {
        return serializer.toJson(singleDataObject);
    }

}
