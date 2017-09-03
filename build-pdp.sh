#!/bin/sh
echo "Building pdp procject.."
cd product-detail-page
mvn clean verify -DskipTests
cd ..
