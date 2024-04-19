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

package org.wildfly.arquillian.integration.test.junit5.server.setup;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.setup.ReloadServerSetupTask;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.WildFlyArquillian;
import org.wildfly.plugin.tools.server.ServerManager;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@WildFlyArquillian
@RunAsClient
@ServerSetup(ReloadServerSetupTaskTestCase.TestSetupTask.class)
public class ReloadServerSetupTaskTestCase {

    public static class TestSetupTask extends ReloadServerSetupTask {
        private final ModelNode address = Operations.createAddress("subsystem", "remoting");
        private final String attributeName = "max-inbound-channels";
        private volatile int currentValue;

        @ContainerResource
        private ServerManager serverManager;

        @Override
        protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
            currentValue = executeOperation(client, Operations.createReadAttributeOperation(address, attributeName)).asInt();
            // Increase the current value which should put the server in a state of reload-required
            executeOperation(client, Operations.createWriteAttributeOperation(address, attributeName, currentValue + 10));
            // Check the server state is in a reload required state
            Assertions.assertEquals(ClientConstants.CONTROLLER_PROCESS_STATE_RELOAD_REQUIRED, serverManager.serverState());
        }

        @Override
        protected void doTearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            // Reset the old value
            executeOperation(managementClient, Operations.createWriteAttributeOperation(address, attributeName, currentValue));
            // Check the server state is in a reload required state
            Assertions.assertEquals(ClientConstants.CONTROLLER_PROCESS_STATE_RELOAD_REQUIRED, serverManager.serverState());
        }
    }

    @ContainerResource
    private static ManagementClient client;

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @AfterAll
    public static void checkControllerState() throws Exception {
        // Check the server state is in a reload required state
        checkServerStateIsRunning();
    }

    @Test
    public void checkServerReloaded() throws Exception {
        // Check the server state is not in a reload required state
        checkServerStateIsRunning();
    }

    private static void checkServerStateIsRunning() throws IOException {
        final ModelNode op = Operations.createReadAttributeOperation(new ModelNode().setEmptyList(), "server-state");
        final ModelNode result = client.getControllerClient().execute(op);
        if (Operations.isSuccessfulOutcome(result)) {
            Assertions.assertEquals(ClientConstants.CONTROLLER_PROCESS_STATE_RUNNING, Operations.readResult(result)
                    .asString());
        } else {
            Assertions.fail("Checking the server state failed: " + Operations.getFailureDescription(result).asString());
        }
    }
}
