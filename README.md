# KURITSUKU

Japanese for *click*, Kuritsuku is a Web-Service-Framework inspired by Apache-Click, JBoss RestEasy and Spring MVC.

## Features

  * Routing is speficied via Annotations.
  * important Objects (Context, Parameter, Header, ...) can be Injected via Annotations.
  * Response handling can be done Servlet-Style (directly), or delegated to ResponseAction implementations.
  * includes abstraction/provider for plugable Template Engines.
    - Apache Velocity
    - FreeMarker
  * includes abstraction/adapter for plugable HTTP Server Engines.
    - Servlet API
    - JDK Http Server
    - JBoss Undertow
    - SimpleFramework Http Service
