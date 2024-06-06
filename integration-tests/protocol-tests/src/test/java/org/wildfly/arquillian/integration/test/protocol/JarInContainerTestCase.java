/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wildfly.arquillian.integration.test.protocol;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
public class JarInContainerTestCase extends AbstractInContainerTestCase {
    private static final String DEPLOYMENT_NAME = JarInContainerTestCase.class.getSimpleName() + ".jar";

    @Deployment
    public static JavaArchive create() {
        return ShrinkWrap.create(JavaArchive.class, DEPLOYMENT_NAME)
                .addClasses(Protocol.class,
                        // Not fully used, but needed for the deployment to not log errors
                        AbstractInContainerTestCase.class,
                        ServerSetupTask.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
}
