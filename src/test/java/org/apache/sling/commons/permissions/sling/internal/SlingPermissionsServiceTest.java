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
package org.apache.sling.commons.permissions.sling.internal;

import java.security.Principal;

import javax.jcr.Session;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlingPermissionsServiceTest {

    @Test
    public void testHasPermissionFailGettingResourceResolver() throws Exception {
        final Principal principal = mock(Principal.class);
        final SlingPermissionsService service = new SlingPermissionsService();
        final ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        when(resourceResolverFactory.getAdministrativeResourceResolver(anyMap())).thenThrow(LoginException.class);
        FieldUtils.writeDeclaredField(service, "resourceResolverFactory", resourceResolverFactory, true);
        assertThat(service.hasPermission(principal, "p"), is(false));
    }

    @Test
    public void testHasPermissionFailAdaptingResourceResolverWithNullSession() throws Exception {
        final Principal principal = mock(Principal.class);
        final SlingPermissionsService service = new SlingPermissionsService();
        final ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        final ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(null);
        when(resourceResolverFactory.getAdministrativeResourceResolver(anyMap())).thenReturn(resourceResolver);
        FieldUtils.writeDeclaredField(service, "resourceResolverFactory", resourceResolverFactory, true);
        assertThat(service.hasPermission(principal, "p"), is(false));
    }

}