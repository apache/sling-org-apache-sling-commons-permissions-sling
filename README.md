[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-permissions-sling/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-permissions-sling/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-permissions-sling/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-permissions-sling/job/master/test/?width=800&height=600)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-commons-permissions-sling&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-commons-permissions-sling)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-commons-permissions-sling&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-commons-permissions-sling)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.commons.permissions.sling.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.commons.permissions.sing)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.commons.permissions.sling/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.commons.permissions.sling%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Permissions Sling

This module is part of the [Apache Sling](https://sling.apache.org) project.

This module provides an implementation of the Sling Commons Permissions API based on Sling Resource and JCR APIs.

## Sling Permissions Service

The Sling Permissions Service maps permissions to JCR items and checks if given `Principal` has read access via `Session#itemExists($permission)`.

The root path of the permissions (hierarchy) is configurable.
