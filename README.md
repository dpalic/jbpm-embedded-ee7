# Embed jBPM + Wildfly 10.0.0 Server (JEE7)

This repository contains a POC to show how to embed the jBPM 6.5.0 into Wildfly 10.0.0

This is a fork of the codefullstack sample to integrate into Glassfish. We have fixed the codebase to run properly on Wildfly 10.0.0. 

To get started you need a running database. 
Best is to use the standard provided ExampleDS from wildfly. This is the default configuration in the persistence.xml file
Or you create a datasource in wildfly and you modify "<jta-data-source>jboss/datasources/ExampleDS</jta-data-source>" in persistence.xml accordingly.

For full explanation, see here http://codefullstack.com/2016/05/14/embed-jbpm-6-2-glassfish-4-x-jee7/
