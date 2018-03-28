#!/bin/sh
echo "Building all procjects.."
cd composer-service
mvn clean verify -DskipTests
cd ..
cd header-footer
mvn clean verify -DskipTests
cd ..
cd product-detail-page
mvn clean verify -DskipTests
cd ..
cd product-information
mvn clean verify -DskipTests
cd ..
cd simplekafka
mvn clean verify -DskipTests
cd ..
