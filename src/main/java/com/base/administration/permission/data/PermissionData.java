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
package com.base.administration.permission.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * {@code @author:} YISivlay
 */
@Getter
@Setter
@Builder
public class PermissionData {

    @SuppressWarnings("unused")
    private final String grouping;

    @SuppressWarnings("unused")
    private final String code;

    @SuppressWarnings("unused")
    private final String entityName;

    @SuppressWarnings("unused")
    private final String actionName;

    @SuppressWarnings("unused")
    private final Boolean selected;

}
