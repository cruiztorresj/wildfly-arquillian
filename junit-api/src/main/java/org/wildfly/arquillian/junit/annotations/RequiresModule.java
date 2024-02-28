/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.arquillian.junit.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.condition.RequiresModuleExecutionCondition;

/**
 * Enables or disables a test based on whether the module exists. You can optionally check the version of the module
 * to determine if the modules version is greater than the {@linkplain #minVersion() minimum version}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Inherited
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RequiresModuleExecutionCondition.class)
public @interface RequiresModule {

    /**
     * The minimum version of the module resource.
     * <p>
     * Note that if more than one resource is defined, only the first resource is used to determine the version.
     * </p>
     *
     * @return the minimum version
     */
    String minVersion() default "";

    /**
     * A reference for the issue tracker to be reported in the response for a disabled test.
     *
     * @return the issue reference
     */
    String issueRef() default "";

    /**
     * The reason message for disabled test.
     *
     * @return the reason message
     */
    String reason() default "";

    /**
     * The module that is required for the test to run.
     *
     * @return the module name
     */
    String value();
}
