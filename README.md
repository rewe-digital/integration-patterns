# Example integration-patterns

This repository contains an example for integrating microservices. Currently, it focuses on the often overlooked frontend composition integration. Services are integrated by a dynamic frontend composition allowing several services to contribute their parts of a frontend page.

The example consists of the follwoing services:
* composer - a basic frontend composition engine
* product-detail-page - a simple product detail page
* header-footer - a service serving a header and a footer

Each project can be build using the latest maven version:
```
maven clean install
```
Result is a fat jar that can be executed via
```
java -jar <path-to-jar>
```
