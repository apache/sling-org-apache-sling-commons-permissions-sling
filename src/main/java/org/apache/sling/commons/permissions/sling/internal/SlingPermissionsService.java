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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.permissions.PermissionsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service which checks for permissions using Sling Resource and JCR APIs.
 */
@Component(
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
    ocd = SlingPermissionsServiceConfiguration.class,
    factory = true
)
public final class SlingPermissionsService implements PermissionsService {

    @Reference(
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    @SuppressWarnings("java:S3077")
    private volatile ResourceResolverFactory resourceResolverFactory;

    private SlingPermissionsServiceConfiguration configuration;

    private final Logger logger = LoggerFactory.getLogger(SlingPermissionsService.class);

    public SlingPermissionsService() { //
    }

    @Activate
    @SuppressWarnings("unused")
    private void activate(final SlingPermissionsServiceConfiguration configuration) {
        logger.debug("activating");
        this.configuration = configuration;
    }

    @Deactivate
    @SuppressWarnings("unused")
    private void deactivate() {
        logger.debug("deactivating");
    }

    @Override
    @SuppressWarnings({"java:S1874", "checkstyle:IllegalCatch", "checkstyle:ReturnCount"})
    public boolean hasPermission(final @NotNull Principal principal, @NotNull final String permission) {
        logger.debug("checking permission '{}' for '{}'", permission, principal.getName());
        final Map<String, Object> authenticationInfo = new HashMap<>();
        authenticationInfo.put(ResourceResolverFactory.USER_IMPERSONATION, principal.getName());
        try (ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(authenticationInfo)) {
            final Session session = resourceResolver.adaptTo(Session.class);
            if (Objects.isNull(session)) {
                logger.error("adapting resource resolver to session failed");
                return false;
            }
            final String path = String.format("%s/%s", configuration.path(), permission);
            return session.itemExists(path);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
