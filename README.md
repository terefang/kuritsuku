# KURITSUKU

Japanese for *click*, Kuritsuku is a Web-Service-Framework inspired by Apache-Click, JBoss RestEasy and Spring MVC.

## Features

  * Class/Method Routing is speficied via Annotations.

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

  * includes handler/helper for ease of implementing standardized webservices
    - json-rpc (via jsonrpc4j -- 1.0, 1.1, 2.0)
    - xml-rpc2 aka Frontier RPC (currently not implemented)
    - rest/json (currently not implemented)
    - rest/xml (currently not implemented
    - soap (currently not implemented)
