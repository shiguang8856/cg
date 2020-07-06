#!/bin/bash
mvn -Dmaven.test.skip=true -U clean compile install  >fulllog.txt