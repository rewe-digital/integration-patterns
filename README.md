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
maven clean install
```
All projects can be build via `./build.sh` - this will build jars and docker images for all services.

All services together can be started via `docker-compose up`.

The services will run on the following ports:
* composer: `8000`
* product-detail-page: `8080`
* header-footer: `8081`
* product-information: `8082`

To test the setup, go to `http://localhost:8000/p/2670536` to see a product detail page including a header and a footer. To acess the product detail page directly, go to `http://localhost:8080/products/2670536`.
