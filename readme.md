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
* The default setting in Android Studio to run locally are messed up for some point. Modify the App Engine DevAppServer in Android Studio's War path to point to "/backend/build/exploded-app".
	* Note: You can also run from the command line using the task appengineStart / appengineStop, but I recommend using the Android Studio so that you can see the logs.
* Install mysql server locally
	* The test database is cloned from production regularly, you can import a dump from it.
	* The code assumes the local database will be at 127.0.0.1:3306 and that the login will be root with no password.
* You need to have two local environment variables that must match the values in appengine-web.xml.
	* ENDPOINTS_SERVICE_NAME e.g. "playerapi.endpoints.lthoi-test.cloud.goog"
	* ENDPOINTS_SERVICE_VERSION e.g. "2017-08-06r0"

Building and Deploying for testing.
* Build from the command line: *./gradlew build*
* <If you've changed the API Definition> Generate the API doc: 
	* *endpoints-framework-tools get-openapi-doc --hostname=playerapi.endpoints.lthoi-test.cloud.goog --war=backend/build/exploded-backend com.cavellandcavell.leavethehouseoutofit.backend.PlayerAPI*
* <If you've changed the API Definition> Deploy the API config
	* *gcloud service-management deploy openapi.json*
* <If you've changed the API Definition> Check for the new version of the API.
	* *gcloud.cmd service-management configs list --service=playerapi.endpoints.lthoi-test.cloud.goog*
* <If you've changed the API Definition> Use the latest version (e.g. "2017-06-17r0") in backend/appengine-web.xml
* Redeploy: *./gradlew appengineDeploy*

Building and Deploying for Production
* TBD

## API Reference

https://github.com/BurgherJon/LTHOIv3/tree/master/backend/build/endpointsDiscoveryDocs

## Tests

None Yet

## Contributors

Jonathan Cavell
Bojan Soldan



