/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.commons.permissions.sling.it.tests;

import java.security.Principal;

import javax.inject.Inject;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.permissions.PermissionsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.apache.sling.testing.paxexam.SlingOptions.slingQuickstartOakTar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.factoryConfiguration;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.newConfiguration;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SlingPermissionsServiceIT extends PermissionsTestSupport {

    @Inject
    private ResourceResolverFactory resourceResolverFactory;

    @Inject
    private PermissionsService permissionsService;

    @Configuration
    public Option[] configuration() {
        return options(
            baseConfiguration(),
            slingQuickstart(),
            factoryConfiguration("org.apache.sling.commons.permissions.sling.internal.SlingPermissionsService")
                .put("path", "/libs/sling/permissions")
                .asOption(),
            factoryConfiguration("org.apache.sling.jcr.base.internal.LoginAdminWhitelist.fragment")
                .put("whitelist.bundles", new String[]{
                    "org.apache.sling.commons.permissions.sling"
                })
                .asOption(),
            factoryConfiguration("org.apache.sling.jcr.repoinit.RepositoryInitializer")
                .put("scripts", new String[]{
                    "create user george with password george\n" +
                    "create user william with password william\n" +
                    "create group pub\n" +
                    "add george to group pub\n" +
                    "create path (sling:Folder) /libs/sling/permissions/pub/beer/drink\n" +
                    "create path (sling:Folder) /libs/sling/permissions/pub/beer/serve\n" +
                    "set ACL for pub\nallow jcr:read on /libs/sling/permissions/pub/beer\nend\n" +
                    "set ACL for william\nallow jcr:read on /libs/sling/permissions/pub/beer\nend\n" +
                    "set ACL for william\ndeny jcr:read on /libs/sling/permissions/pub/beer/drink\nend\n"
                })
                .asOption(),
            newConfiguration("org.apache.sling.jcr.base.internal.LoginAdminWhitelist")
                .put("whitelist.bundles.regexp", "PAXEXAM-PROBE-.*")
                .asOption()
        );
    }

    protected Option slingQuickstart() {
        final int httpPort = findFreePort();
        final String workingDirectory = workingDirectory();
        return slingQuickstartOakTar(workingDirectory, httpPort);
    }

    protected Principal getPrincipal(final String name) throws Exception {
        final ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
        final JackrabbitSession session = (JackrabbitSession) resourceResolver.adaptTo(Session.class);
        final PrincipalManager principalManager = session.getPrincipalManager();
        return principalManager.getPrincipal(name);
    }

    @Test
    public void testGeorgesPermission() throws Exception {
        final Principal principal = getPrincipal("george");
        assertThat(permissionsService.hasPermission(principal, "pub/beer/serve"), is(true));
        assertThat(permissionsService.hasPermission(principal, "pub/beer/drink"), is(true));
    }

    @Test
    public void testWilliamsPermission() throws Exception {
        final Principal principal = getPrincipal("william");
        assertThat(permissionsService.hasPermission(principal, "pub/beer/serve"), is(true));
        assertThat(permissionsService.hasPermission(principal, "pub/beer/drink"), is(false));
    }

    @Test
    public void testNonExistingPermission() throws Exception {
        final Principal principal = getPrincipal("william");
        assertThat(permissionsService.hasPermission(principal, "pub/beer/foo"), is(false));
    }

}
