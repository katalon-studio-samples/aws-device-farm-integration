# aws-device-farm-integration project

**aws-device-farm-integration** is a built for executing Katalon Studio tests on AWS Device Farm. [AWS Device Farm](https://aws.amazon.com/device-farm/) only supports running tests using supported frameworks such as [Working with Appium and AWS Device Farm](https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-appium.html); hence, Katalon users cannot execute their tests on AWS Device farm directly.

By using **aws-device-farm-integration**, you can execute your Katalon test scripts with devices provided on AWS Device Farm. This tutorial shows you how to configure Katalon projects, AWS device farm and **aws-device-farm-integration** to execute the usage sample, which is **KatalonDemoProject**. Also, at the end of this tutorial, we provide some configurations that you can try to execute your Katalon projects on AWS Device Farm.

## Prerequisites

You need

* An active [Katalon Runtime Engine](https://docs.katalon.com/katalon-studio/docs/license.html#katalon-runtime-engine) license
* KRE v7.8+

Your machine should

* Install [Apache Maven](https://maven.apache.org/download.cgi) version 3.3.9 or later
* Install Java JDK 8 

## Supported testing types and platforms
- [x] Mobile app on Android
- [x] Mobile app on iOS
- [x] Web app on Android
- [ ] Web app on iOS

## How to use

### Configure your Katalon project

1. Prepare Katalon tested test cases, test suites that can run successfully on local device
   > For Mobile test, a test should start with [Start Existing Application](https://docs.katalon.com/katalon-studio/docs/mobile-keyword-start-existing-apps.html) keyword because AWS Device Farm already installs the application on tested device before every run.
2. Open Project Settings/Desired Capabilities/Remote
  - In `Remote server URL`, enter the Appium server URL: http://127.0.0.1:4723/wd/hub and select Appium in Remote server type.
  - In `Appium driver`, select Android Driver for Android devices; or iOS Driver for iOS devices.
  - Add a desired capability `platformName` with value `Android` for Android devices; or `iOS` for iOS devices.
    - For Android app testing, we need to add extra desired capabilities `appPackage: [app ID]` and `appActivity: [main activity name]`. Main activity can retrieve after uploading app to AWS Device Farm
    <img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/1-android-main-activity.png" width=70% alt="android-main-activity">
  - Press `Apply and Close`.
<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/1-configure-desired-capabilities.png" width=70% alt="configure-desired-capabilities">

### Configure aws-device-farm-integration project

1. Clone this repository
2. Open this open in any IDE choice (Eclipse, VSCode,...)
3. Zip your Katalon project and put in src/test/resources folder
<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/2-zip-demo-project.png" width=40% alt="zip-demo-project">

4. Open config.properties file and change the following variables as your context:
  - `KATALON_VERSION`: Katalon Runtime Engine version
    - (e.g. `"8.1.0, 7.8.2"`)
  - `KATALON_PROJECT_PACKAGE_FILE`: Your package file
    - (e.g. `"KatalonDemoProject.zip"`)
  - `KATALON_EXECUTE_ARGS`: The arguments part of your Katalon run command<br>
    - (e.g. `"-retry=0 -testSuitePath="Test Suites/Regression Tests" -executionProfile="default" -browserType="Remote" -reportFolder=$DEVICEFARM_LOG_DIR -apiKey="xxxxxx""`)
    > Please note that the `-browserType` argument must be set to `"Remote"`.

    > The `-reportFolder=$DEVICEFARM_LOG_DIR` argument allows us can download the execution report in Files/Customer Artifact of the AWS Device Farm Job
5. Build aws-device-farm-integration by typing the below command in terminal
```
mvn clean package -DskipTests=true
```
If the build runs successfully, you will see a zip name `zip-with-dependencies.zip` in the target folder. We will use this file for the next step.

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/2-build-project-with-maven.png" width=40% alt="build-project-with-maven">

### Configure test project on AWS Device Farm

1. In AWS Console, create or click on an existing project

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-create-test-project.png" width=70% alt="create-test-project">

2. Click on `Create a new run`
3. In `Choose Application` step, 
  - For Mobile app testing, select `Mobile App` tab and upload your tested application (.apk for Anroid application and .ipa for iOS application). Wait for uploading app successfully then click `Next`.

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-upload-mobile-app.png" width=70% alt="upload-mobile-app">

  - For Web app testing, select `Web App` and type a run name then click `Next`.
3. In `Configure` Step, select `Appium Java JUnit` in test type drop-down. Upload the `zip-with-dependencies.zip` that was created from Maven in the previous step then click `Next`.

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-upload-zip-file.png" width=70% alt="upload-zip-file">

4. In `Select devices` Step, choose a suitable device pool then click `Next`.
5. In `Specify device state` Step, review other settings and change when needed then click `Next`
6. In `Review and start run` Step, review all of configurations one last time then click `Confirm and Start Run`.

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-finish-creating-run.png" width=70% alt="finish-creating-run">

7. A run already starts, we can click on test run name and a specificed device to view the test status

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-view-test-run-status.png" width=70% alt="view-test-run-status">
<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-test-runs-finish.png" width=70% alt="test-runs-finish">

After the run finishes, we can download the execution report at `Files/Customer Artifacts`

<img src="https://raw.githubusercontent.com/katalon-studio-samples/aws-device-farm-integration/main/docs/images/3-download-report.png" width=70% alt="download-report">

## Examples Katalon project and AUTs
- Sample Katalon project in `aut/KatalonDemoProject`
- Sample iOS application in `aut/Coffee Timer.ipa`

## CI/CD pipelines
- [Jenkins integration](https://github.com/katalon-studio-samples/aws-device-farm-integration/blob/main/docs/Jenkins-integration.MD)
