## wcm.io Configuration (deprecated)

Context-specific configuration for AEM applications.

**PLEASE NOTE**: wcm.io Configuration is deprecated (although still fully maintained). For new projects please use the [Apache Sling Context-Aware Configuration][sling-caconfig], with support from [wcm.io Context-Aware Configuration][caconfig]. There is a wiki page how to migrate: [Migrate from wcm.io Configuration 0.x to Context-Aware Configuration][caconfig-migration].


### Context-specific configuration

wcm.io Configuration manages context-specific configuration, that means configuration that cannot be stored as OSGi configurations. OSGi configurations are always system-wide, so they are not well-suited for storing configurations per context e.g. site, region or tenant. Each context can be described as a path in the resource hierarchy including its subtree. Contexts can be nested.

This wcm.io subproject implements APIs and SPIs to provide a flexible configuration infrastructure for reading and managing context-specific configuration. The implementation is pluggable which ensures it can be adapted to the application needs. It's based on OSGi services.

The problems and usecases solved by this implementation are described in the wcm.io Wiki [Multi Tenancy and Configuration Requirements][wiki-config-requirements] page (although not everything described there is implemented currently). For detailed documentation and usage descriptions see below.


### Overview

* [API](api/): API and SPI for context-specific configuration.
* [Implementation](core/): Context-specific configuration management implementation.
* [Configuration Editor](editor/): Configuration Editor Template for AEM.
* [Sample Application](sample-app/): Sample application to demonstrate configuration API and configuration editor GUI.


### GitHub Repository

Sources: https://github.com/wcm-io/wcm-io-config



[caconfig]: http://wcm.io/caconfig/
[sling-caconfig]: http://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html
[caconfig-migration]: https://wcm-io.atlassian.net/wiki/x/BgCvAg
[wiki-config-requirements]: https://wcm-io.atlassian.net/wiki/x/HIAH
