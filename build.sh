#!/bin/sh
echo "Building all procjects.."
mvn clean verify -DskipTests
