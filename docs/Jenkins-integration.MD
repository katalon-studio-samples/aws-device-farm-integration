# Jenkins Integration

In this tutorial, we will demostrate how to setup and run **aws-device-farm-integration** or a similar project from Git in Jenkins pipeline. That will help to deploy and run your tests automatically.

## Prerequisites

Your machine should

* Jenkins
* Install [Apache Maven](https://maven.apache.org/download.cgi) version 3.3.9 or later

## How to configure

1. Follow step #1 of this [intruction](https://docs.aws.amazon.com/devicefarm/latest/developerguide/jenkins-ci-installing-the-plugin.html) to install [aws-device-farm](https://plugins.jenkins.io/aws-device-farm/) in your Jenkins

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-aws-df-plugin.png" width=70% alt="aws-df-plugin">

2. Follow steps #2, #3 of this [intruction](https://docs.aws.amazon.com/devicefarm/latest/developerguide/jenkins-ci-first-time-configuration-instructions.html) to configure Jenkins system and validate your AWS credentials

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-aws-credentials.png" width=70% alt="aws-credentials">

3. Back to Jenkins Dashboard, create New item -> Free style project

4. In **Source Code Management**, add your project URL and credentials

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-source-code-management.png" width=70% alt="source-code-management">

5. In **Build**, click `Add build step` -> `Execute shell`

6. In the command text field, add this script that helps to build **aws-device-farm-integration** Maven project

```
export MAVEN_HOME=[Your Maven installed folder]
export PATH=$PATH:$MAVEN_HOME/bin
mvn clean package -DskipTests=true
```
<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-build-steps.png" width=70% alt="build-steps">

7. In **Post Build Action**, click `Add post-build action` -> `Run Tests on AWS Device Farm`

8. Select your AWS DF `Project` and `Device Pool` that you created in AWS DF Console

9. In `Application` field, enter the relative path of the AUT from your root project. Leave it blank if it is a web application

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-application.png" width=70% alt="application">

10. In **Choose test to run**, select `Appium Java JUnit`. In `Tests` field, enter the path to maven build zip file. In this case, it should be `target/zip-with-dependencies.zip`

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-zip-file.png" width=70% alt="zip-file">

11. In **Choose your execution environment**, select `Custom enviroment` and `Default TestSpec for Android Appium Java Junit v2.0 (adds support for Appium 1.14+)` option to let AWS DF run with the latest Appium version they can support

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-custom-environment.png" width=70% alt="custom-environment">

12. Review other AWS DF configurations then click `Save`

13. Trigger the build and wait the pipeline completes

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-pipeline-console-log.png" width=70% alt="pipeline-console-log">

    When the pipeline completes, we are able to see the AWS Device Farm Results on Jenkins dashboard

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/jenkins-integration/1-dashboard.png" width=70% alt="dashboard">
