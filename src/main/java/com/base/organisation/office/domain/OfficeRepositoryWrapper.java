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
package com.base.organisation.office.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OfficeRepositoryWrapper {

    private final OfficeRepository repository;

    public Office save(final Office office) {
        return this.repository.save(office);
    }
}
