# KURITSUKU

Japanese for *click*, Kuritsuku is a Web-Service-Framework inspired by Apache-Click, JBoss RestEasy and Spring MVC.

## Features

  * Class/Method Routing is specified via Annotations.

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
    - xmlrpc2 aka Frontier RPC (via Redstone XMLRPC)
    - rest/json (currently not implemented -- JSON-B?)
    - rest/xml (currently not implemented -- JAX-B?)
    - soap (currently not implemented -- kSoap2? SAAJ/JAX-B?)
