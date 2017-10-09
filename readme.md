## Synopsis

This is the source code for an application meant to let a group of friends place play wagers on Football games against one another.

## Code Example



## Motivation

This seems pretty obvious.

## Installation

Steps for setting up a dev environment:
* Clone this repository
* Install Gradle
* Install Android Studio
* Install Endpoint Framework Tools
	* http://search.maven.org/remotecontent?filepath=com/google/endpoints/endpoints-framework-tools/2.0.0-beta.11/endpoints-framework-tools-2.0.0-beta.11.zip
	* Create a PATH for endpoints-framework-tools


Desktop Testing
* Install mysql server locally
	* The test database is cloned from production regularly, you can import a dump from it.
	    * The URL for the test DB server is 104.197.120.142 and the password is lthoi-test.
	    * You will need Jonathan to add your desktop to the white list if you want to do the clone.
	* The code assumes the local database will be at 127.0.0.1:3306 and that the login will be lthoidb with "pass" as password.  If you wish to use a different setup, you can change the configuration in Environments.java, but please do not check it in.
* You need to have two local environment variables that must match the values in /backend/src/webapp/WEB-INF/appengine-web.xml.  Note that when you change these on a Windows box, you will need to restart AndroidStudio.
	* ENDPOINTS_SERVICE_NAME e.g. "playerapi.endpoints.lthoi-test.cloud.goog"... but whatever is current in appengine-web.xml
	* ENDPOINTS_SERVICE_VERSION e.g. "2017-08-06r0"... but whatever is current in appengine-web.xml
* In the repository where you have the source code, start a dev server by using the command *./gradlew appengineRun*
* Now in your browser you will be able to load the shitty UI at the url: localhost:8080/shitty/

Managing the Codebase
* When you have a working version of your code and are ready for me to implement, please merge to the latest codebase in master and create a pull request.
* All modifications should either be done through a pull request or a branch (if you're a core developer).

Test Pipeline:
* ONLY APPLICABLE IF YOU HAVE CHANGED THE API (e.g. added/removed attributes or changed parameter).  In this case you will need to update the version of the API:
    * Modify appengine-web.xml (both in /backend/src/webapp/WEB-INF/ and in /prodfiles/ to reflect a new version number.
    * Change the jenkinsconfig.yaml file property "CreateNewApi" to true
* Once your code has been merged with master then deploy to test using this job: http://13.59.49.208/jenkins/job/Test%20Pipeline/
    * This will also copy the production database back to test.
    * Once the deployment is finished you can find the application here: https://lthoi-test.appspot.com/shitty/
        * Note: Because the database is pulled back from prod, if you had logged in to test before, the application will have forgotten.  You will need to pair to an existing user.  Jonathan is user 1 and is in two leagues, might be a helpful test id.
    * Please test the following things:
        * Do the standings look the same as they do in production?
        * When you place a bet does it increase the bet amount by an appropriate amount?
        * Do the results of the previous week look right?  (Please check a win, a loss, and a failed to cover)

Prod Pipeline:
* IF YOU ARE CREATING A NEW API... BE SURE THE jenkinsconfig.yaml file is setup correctly.
* Run this Jenkins Job: http://13.59.49.208/jenkins/job/ProdDeploy/
* IF YOU WERE CREATING A NEW API... PLEASE REVERT the jenkinsconfig.yaml file and create a new version.

## API Reference

https://lthoi-test.appspot.com/_ah/api/discovery/v1/apis/playerapi/v1/rest?fields=kind%2Cname%2Cversion%2CrootUrl%2CservicePath%2Cresources%2Cparameters%2Cmethods%2CbatchPath%2Cid&pp=0&key=AIzaSyDT-hXFhfRcHvmiFqEzTxaLQh2u0FY62Gc
Also useful is the test API Console: https://apis-explorer.appspot.com/apis-explorer/?base=https://lthoi-test.appspot.com/_ah/api#p/playerapi/v1/ (you can get the firebase UID from the database to execute as any user).

## Tests

None Yet

## Contributors

Jonathan Cavell
Bojan Soldan



