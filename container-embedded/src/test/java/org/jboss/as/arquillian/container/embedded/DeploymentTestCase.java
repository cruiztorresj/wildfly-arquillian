/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.as.arquillian.container.embedded;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests basic deployment
 */
@RunWith(Arquillian.class)
public class DeploymentTestCase {

    @Deployment
    public static JavaArchive create() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addClass(SystemPropertyServiceActivator.class)
                .addAsServiceProvider(ServiceActivator.class, SystemPropertyServiceActivator.class);
        return archive;
    }

    @Test
    public void testSystemPropSet() throws Exception {
        Assert.assertEquals(SystemPropertyServiceActivator.VALUE,
                System.getProperty(SystemPropertyServiceActivator.TEST_PROPERTY));
    }

    @Test
    public void testSystemPropertyArgument() throws Exception {
        testSystemProperty("deployment.arq.test.property");
        testSystemProperty("deployment.arq.other.test.property");
    }

    private static void testSystemProperty(final String key) {
        Assert.assertNotNull(String.format("Expected a value for property \"%s\"", key), System.getProperty(key));
    }
}