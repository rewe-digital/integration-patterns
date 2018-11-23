# Example integration-patterns

[![Build Status](https://travis-ci.org/rewe-digital/integration-patterns.svg?branch=master)](https://travis-ci.org/rewe-digital/integration-patterns)

This repository contains examples for integrating microservices:
* Integration of frontend components via a micro-frontend approach
* Integration of backend data via Apache Kafka based events 

The example consists of the following services:
* composer - a basic frontend composition engine
* product-detail-page - a simple product detail page
* header-footer - a service serving a header and a footer
* product-information - a backend for managing product data

To run the example, each project can be build using the latest maven version:
```
mvn clean install
```
All projects can be build via `./build.sh` - this will build jars and docker images for all services.

All services together can be started via `docker-compose up -d`.

Locally, the services will run on the following ports:
* composer: `8000`
* product-detail-page: `8080`
* header-footer: `8081`
* product-information: `8082`

When started via docker-compose, the services run on
* composer: `9000`
* product-detail-page: `9080`
* header-footer: `9081`
* product-information: `9082`

To test the setup, go to `http://localhost:9000/p/2670536` to see a product detail page including a header and a footer. To acess the product detail page directly, go to `http://localhost:9080/products/2670536`.

### License

The MIT License (MIT) Copyright © [yyyy] REWE digital GmbH, https://www.rewe-digital.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.