## About Configuration Sample Application

Sample application to demonstrate configuration API and configuration editor GUI.

See [wcm.io Configuration](http://wcm.io/config/) for more details about the context-specific configuration support.


### Build and deploy samples from source

- Clone the github repository https://github.com/wcm-io/wcm-io-config
- Go to /sample-app directory
- Build with `mvn clean install`
- Deploy OSGi bundle target/io.wcm.config.sample-app-* to AEM instance (e.g. via Felix Console)


### Try out the sample

- In you AEM instance, create a page using the template "wcm.io Sample Configuration Editor"
- Try out the editor capabilities
- Inspect the source code


### System requirements

- AEM 6.0 SP1 or AEM 6.1
