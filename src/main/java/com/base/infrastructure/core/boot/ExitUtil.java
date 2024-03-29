/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.base.infrastructure.core.boot;

import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class ExitUtil {

    private ExitUtil() {
    }

    public static void waitForKeyPressToCleanlyExit(ConfigurableApplicationContext ctx) throws IOException {
        System.out.println("\nHit enter to quit...");
        BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
        d.readLine();
        ctx.stop();
        ctx.close();
    }
}
