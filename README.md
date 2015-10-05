# HYBRIDBPM #
## What is HYBRIDBPM? ##
**HYBRIDBPM** is an easy-to-use Business Process Management System (BPMS) targeted at business people and developers. HYBRIDBPM has a distributed core based on [Hazelcast](http://www.hazelcast.org) and [OrientDB](http://www.orientdb.com), modern user interface created with [Vaadin Framework](http://www.vaadin.com). It is written in Java and runs in any servlet container or as a standalone application. It's open-source and distributed under the Apache license. 

## Features ##
### Full cycle of process management ###
1. [Modelling and development](https://github.com/hybridbpm/hybridbpm/wiki/Screenshots#modelling) (Process, Data, Forms, Connectors)
2. [Execution](https://www.liferay.com/community/forums/-/message_boards/message/5235047) (Process Engine, User Case/Task Lists)
3. [Monitoring](https://github.com/hybridbpm/hybridbpm/wiki/Screenshots#monitoring) (Dashboard and configurable charts)

### Easy-to-use modern user interface ###
All [screenshots](https://github.com/hybridbpm/hybridbpm/wiki/Screenshots)

### Lightweight ###
1. 70Mb all-in-one jar
2. Embedded database (OrientDB)

### Scalability and high-availability ###
1. Distributed data-grid-based (Hazelcast) process engine 
2. Distributed multi-model (Document, Graph) database (OrientDB)

## Requirements ##
1. Mandatory: **Java 8**
2. Optional: Servlet container

## Installation ##
### Standalone ###
Standalone all-in-one HYBRIDBPM server is based on [Undertow](http://www.undertow.io)

1. Download all-in-one [hybridbpm-server-1.0.0-alpha1.jar](https://github.com/hybridbpm/hybridbpm/releases/download/1.0.0-alpha1/hybridbpm-server-1.0.0-alpha1.jar)
2. Run `java -jar hybridbpm-server-1.0.0-alpha1.jar`
3. Point your browser to http://hostname:8080 
4. Default login: administrator, password:administrator

### Servlet ###
1. Download [hybridbpm-1.0.0-alpha1.war](https://github.com/hybridbpm/hybridbpm/releases/download/1.0.0-alpha1/hybridbpm-1.0.0-alpha1.war)
2. Deploy hybridbpm-1.0.0-alpha1.war into servlet container
3. Point your browser to http://hostname:port/hybridbpm-1.0.0-alpha1
4. Default login: administrator, password:administrator

## Getting started ##
### Create new process ###
1. Create data model
2. Create process model
3. Create form
4. Create connector

### Working with cases and tasks ###
1. Start new case
2. Execute tasks
3. Add comments
4. Manage cases

### Administration ###
1. Manage users
2. Manage roles
3. Manage groups

## Professional services ##
Support Service, Professional Consulting and Trainings are comming soon.

## Licenses ##
The source code is released under Apache 2.0.

The application uses the Vaadin Charts 3 add-on, which is released under the Commercial Vaadin Addon License: https://vaadin.com/license/cval-3