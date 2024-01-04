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

In `Appium Java Unit section`, click Edit and replace the yaml file with the content below:

- iOS

```
version: 0.1

# Phases represent collections of commands that are executed during your test run on the test host.
phases:

  # The install phase contains commands for installing dependencies to run your tests.
  # For your convenience, certain dependencies are preinstalled on the test host. 

  # For iOS tests, Java 12 is preinstalled and preconfigured on the test host. For more information
  # about how to package your Java dependencies for your test, please see:
  # https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-appium.html
  # Additionally, you can use the Node.JS tools nvm, npm, and avm to setup your Appium server.
  install:
    commands:
      # The Appium server is written using Node.js. In order to run your desired version of Appium,
      # you first need to set up a Node.js environment that is compatible with your version of Appium.
      # For iOS, use "nvm use" to switch between the two preinstalled NodeJS versions 14 and 16, and 
      # use "nvm install" to download a new version of your choice.
      - export NVM_DIR=$HOME/.nvm
      - . $NVM_DIR/nvm.sh
      - nvm use 16
      - node --version

      # For iOS, Appium versions 1.22.2 and 2.2.1 are preinstalled and selectable through avm.
      # For all other versions, please use npm to install them. For example:
      # - npm install -g appium@2.1.3;
      # Note that, for iOS devices, Appium 2 is only supported on iOS version 14 and above using
      # NodeJS version 16 and above.
      - avm 2.2.1
      - appium --version

      # For Appium version 2, for iOS tests, the XCUITest driver is preinstalled using version 5.7.0
      # If you want to install a different version of the driver, you can use the Appium extension CLI
      # to uninstall the existing XCUITest driver and install your desired version:
      # - |-
      #   if [ $DEVICEFARM_DEVICE_PLATFORM_NAME = "iOS" ];
      #   then
      #     appium driver uninstall xcuitest;
      #     appium driver install xcuitest@5.8.1;
      #   fi;

      # We recommend setting the Appium server's base path explicitly for accepting commands.
      - export APPIUM_BASE_PATH=/wd/hub

      # On iOS, all tests run using Java version 12. This is not configurable at this time.
      - java -version

  # The pre-test phase contains commands for setting up your test environment.
  pre_test:
    commands:
      # Setup the CLASSPATH so that Java knows where to find your test classes.
      - export CLASSPATH=$CLASSPATH:$DEVICEFARM_TEST_PACKAGE_PATH/*
      - export CLASSPATH=$CLASSPATH:$DEVICEFARM_TEST_PACKAGE_PATH/dependency-jars/*

      # Device farm provides different pre-built versions of WebDriverAgent, an essential Appium
      # dependency for iOS devices, and each version is suggested for different versions of Appium:
      # DEVICEFARM_WDA_DERIVED_DATA_PATH_V8: this version is suggested for Appium 2
      # DEVICEFARM_WDA_DERIVED_DATA_PATH_V7: this version is suggested for Appium 1
      # Additionally, for iOS versions 16 and below, the device unique identifier (UDID) needs
      # to be slightly modified for Appium tests.
      - |-
        if [ $DEVICEFARM_DEVICE_PLATFORM_NAME = "iOS" ];
        then
          if [ $(appium --version | cut -d "." -f1) -ge 2 ];
          then
            DEVICEFARM_WDA_DERIVED_DATA_PATH=$DEVICEFARM_WDA_DERIVED_DATA_PATH_V8;
          else
            DEVICEFARM_WDA_DERIVED_DATA_PATH=$DEVICEFARM_WDA_DERIVED_DATA_PATH_V7;
          fi;
          
          if [ $(echo $DEVICEFARM_DEVICE_OS_VERSION | cut -d "." -f 1) -le 16 ];
          then
            DEVICEFARM_DEVICE_UDID_FOR_APPIUM=$(echo $DEVICEFARM_DEVICE_UDID | tr -d "-");
          else
            DEVICEFARM_DEVICE_UDID_FOR_APPIUM=$DEVICEFARM_DEVICE_UDID;
          fi;
        fi;

      # We recommend starting the Appium server process in the background using the command below.
      # The Appium server log will be written to the $DEVICEFARM_LOG_DIR directory.
      # The environment variables passed as capabilities to the server will be automatically assigned
      # during your test run based on your test's specific device.
      # For more information about which environment variables are set and how they're set, please see
      # https://docs.aws.amazon.com/devicefarm/latest/developerguide/custom-test-environment-variables.html
      - |-
        appium --base-path=$APPIUM_BASE_PATH --log-timestamp \
          --log-no-colors --relaxed-security --default-capabilities \
          "{\"appium:deviceName\": \"$DEVICEFARM_DEVICE_NAME\", \
          \"platformName\": \"$DEVICEFARM_DEVICE_PLATFORM_NAME\", \
          \"appium:app\": \"$DEVICEFARM_APP_PATH\", \
          \"appium:udid\":\"$DEVICEFARM_DEVICE_UDID_FOR_APPIUM\", \
          \"appium:platformVersion\": \"$DEVICEFARM_DEVICE_OS_VERSION\", \
          \"appium:derivedDataPath\": \"$DEVICEFARM_WDA_DERIVED_DATA_PATH\", \
          \"appium:usePrebuiltWDA\": true, \
          \"appium:automationName\": \"XCUITest\"}" \
          >> $DEVICEFARM_LOG_DIR/appium.log 2>&1 &

      # This code will wait until the Appium server starts.
      - |-
        appium_initialization_time=0;
        until curl --silent --fail "http://0.0.0.0:4723${APPIUM_BASE_PATH}/status"; do
          if [[ $appium_initialization_time -gt 30 ]]; then
            echo "Appium did not start within 30 seconds. Exiting...";
            exit 1;
          fi;
          appium_initialization_time=$((appium_initialization_time + 1));
          echo "Waiting for Appium to start on port 4723...";
          sleep 1;
        done;

  # The test phase contains commands for running your tests.
  test:
    commands:
      # Your test package is downloaded and unpackaged into the $DEVICEFARM_TEST_PACKAGE_PATH directory.
      - cd $DEVICEFARM_TEST_PACKAGE_PATH
      - echo "Starting the Appium Java JUnit test"

      # Below are examples of how to run Appium Java  tests on Device Farm using different JUnit versions:
      # 1. JUnit 5 (default configuration)
      # 2. TestNG with support for running JUnit 4 tests
      # 3. JUnit 4
      # Uncomment and adapt the section that corresponds to your chosen JUnit version and test setup.

      # JUnit 5 example:

      # TestNG with JUnit 4 support example:
      # Note: TestNG can directly run JUnit 4 tests. Ensure your TestNG suite XML is set up accordingly.
      # For details, see the TestNG documentation: http://testng.org/doc/documentation-main.html
      # - |-
      #   java -Dappium.screenshots.dir=$DEVICEFARM_SCREENSHOT_PATH org.testng.TestNG -junit \
      #     -testjar *-tests.jar -d $DEVICEFARM_LOG_DIR/test-output -verbose 10

      # JUnit 4 example:
      # Make sure to replace 'Replace.With Your.TestClasses Here' with your actual test classes.
      - export TEST_CLASSES="com.kms.example.aws_ios.test.TestIos"
      - java -Dappium.screenshots.dir=$DEVICEFARM_SCREENSHOT_PATH org.junit.runner.JUnitCore $TEST_CLASSES

      # Before starting tests, verify that all dependencies and configurations are in place.
      # For more information, refer to AWS Device Farm's documentation on Appium Java JUnit testing:
      # https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-appium.html

  # The post-test phase contains commands that are run after your tests have completed.
  # If you need to run any commands to generating logs and reports on how your test performed,
  # we recommend adding them to this section.
  post_test:
    commands:

# Artifacts are a list of paths on the filesystem where you can store test output and reports.
# All files in these paths will be collected by Device Farm.
# These files will be available through the ListArtifacts API as your "Customer Artifacts".
artifacts:
  # By default, Device Farm will collect your artifacts from the $DEVICEFARM_LOG_DIR directory.
  - $DEVICEFARM_LOG_DIR
```

- Android
```
version: 0.1

# This flag enables your test to run using Device Farm's Amazon Linux 2 test host. For more information,
# please see https://docs.aws.amazon.com/devicefarm/latest/developerguide/amazon-linux-2.html
android_test_host: legacy

# Phases represent collections of commands that are executed during your test run on the test host.
phases:

  # The install phase contains commands for installing dependencies to run your tests.
  # For your convenience, certain dependencies are preinstalled on the test host. To lean about which
  # software is included with the host, and how to install additional software, please see:
  # https://docs.aws.amazon.com/devicefarm/latest/developerguide/amazon-linux-2-supported-software.html

  # Many software libraries you may need are available from the test host using the devicefarm-cli tool.
  # To learn more about what software is available from it and how to use it, please see:
  # https://docs.aws.amazon.com/devicefarm/latest/developerguide/amazon-linux-2-devicefarm-cli.html
  install:
    commands:
      # The Appium server is written using Node.js. In order to run your desired version of Appium,
      # you first need to set up a Node.js environment that is compatible with your version of Appium.
      - devicefarm-cli use node 18
      - node --version

      # Use the devicefarm-cli to select a preinstalled major version of Appium.
      - devicefarm-cli use appium 1
      - appium --version

      # The Device Farm service automatically updates the preinstalled Appium versions over time to
      # incorporate the latest minor and patch versions for each major version. If you wish to
      # select a specific version of Appium, you can use NPM to install it.
      # - npm install -g appium@2.1.3

      # For Appium version 2, Device Farm automatically updates the preinstalled UIAutomator2 driver
      # over time to incorporate the latest minor and patch versions for its major version 2. If you
      # want to install a specific version of the driver, you can use the Appium extension CLI to
      # uninstall the existing UIAutomator2 driver and install your desired version:
      # - appium driver uninstall uiautomator2
      # - appium driver install uiautomator2@2.34.0

      # We recommend setting the Appium server's base path explicitly for accepting commands.
      - export APPIUM_BASE_PATH=/wd/hub

      # Use the devicefarm-cli to setup a Java environment, with which you can run your test suite.
      - devicefarm-cli use java 11
      - java -version

  # The pre-test phase contains commands for setting up your test environment.
  pre_test:
    commands:
      # Setup the CLASSPATH so that Java knows where to find your test classes.
      - export CLASSPATH=$CLASSPATH:$DEVICEFARM_TEST_PACKAGE_PATH/*
      - export CLASSPATH=$CLASSPATH:$DEVICEFARM_TEST_PACKAGE_PATH/dependency-jars/*

      # Appium downloads Chromedriver using a feature that is considered insecure for multitenant
      # environments. This is not a problem for Device Farm because each test host is allocated
      # exclusively for one customer, then terminated entirely. For more information, please see
      # https://github.com/appium/appium/blob/master/packages/appium/docs/en/guides/security.md

      # We recommend starting the Appium server process in the background using the command below.
      # The Appium server log will be written to the $DEVICEFARM_LOG_DIR directory.
      # The environment variables passed as capabilities to the server will be automatically assigned
      # during your test run based on your test's specific device.
      # For more information about which environment variables are set and how they're set, please see
      # https://docs.aws.amazon.com/devicefarm/latest/developerguide/custom-test-environment-variables.html
      - |-
        appium  --default-capabilities \
          "{\"appium:deviceName\": \"$DEVICEFARM_DEVICE_NAME\", \
          \"platformName\": \"$DEVICEFARM_DEVICE_PLATFORM_NAME\", \
          \"appium:app\": \"$DEVICEFARM_APP_PATH\", \
          \"appium:udid\":\"$DEVICEFARM_DEVICE_UDID\", \
          \"appium:platformVersion\": \"$DEVICEFARM_DEVICE_OS_VERSION\", \
          \"appium:automationName\": \"UiAutomator2\"}" \
          >> $DEVICEFARM_LOG_DIR/appium.log 2>&1 &

      # This code will wait until the Appium server starts.
      - |-
        appium_initialization_time=0;
        until curl --silent --fail "http://0.0.0.0:4723/wd/hub/status"; do
          if [[ $appium_initialization_time -gt 30 ]]; then
            echo "Appium did not start within 30 seconds. Exiting...";
            exit 1;
          fi;
          appium_initialization_time=$((appium_initialization_time + 1));
          echo "Waiting for Appium to start on port 4723...";
          sleep 1;
        done;

  # The test phase contains commands for running your tests.
  test:
    commands:
      # Your test package is downloaded and unpackaged into the $DEVICEFARM_TEST_PACKAGE_PATH directory.
      - cd $DEVICEFARM_TEST_PACKAGE_PATH
      - echo "Starting the Appium Java JUnit test"

      # Below are examples of how to run Appium Java tests on Device Farm using different JUnit versions:
      # 1. JUnit 5 (default configuration)
      # 2. TestNG with support for running JUnit 4 tests
      # 3. JUnit 4
      # Uncomment and adapt the section that corresponds to your chosen JUnit version and test setup.

      # JUnit 5 example:

      # TestNG with JUnit 4 support example:
      # Note: TestNG can directly run JUnit 4 tests. Ensure your TestNG suite XML is set up accordingly.
      # For details, see the TestNG documentation: http://testng.org/doc/documentation-main.html
      # - |-
      #   java -Dappium.screenshots.dir=$DEVICEFARM_SCREENSHOT_PATH org.testng.TestNG -junit \
      #     -testjar *-tests.jar -d $DEVICEFARM_LOG_DIR/test-output -verbose 10

      # JUnit 4 example:
      # Make sure to replace 'Replace.With Your.TestClasses Here' with your actual test classes.
      - export TEST_CLASSES="com.kms.example.aws_ios.test.TestIos"
      - java -Dappium.screenshots.dir=$DEVICEFARM_SCREENSHOT_PATH org.junit.runner.JUnitCore $TEST_CLASSES

      # Before starting tests, verify that all dependencies and configurations are in place.
      # For more information, refer to AWS Device Farm's documentation on Appium Java JUnit testing:
      # https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-appium.html

  # The post-test phase contains commands that are run after your tests have completed.
  # If you need to run any commands to generating logs and reports on how your test performed,
  # we recommend adding them to this section.
  post_test:
    commands:

# Artifacts are a list of paths on the filesystem where you can store test output and reports.
# All files in these paths will be collected by Device Farm.
# These files will be available through the ListArtifacts API as your "Customer Artifacts".
artifacts:
  # By default, Device Farm will collect your artifacts from the $DEVICEFARM_LOG_DIR directory.
  - $DEVICEFARM_LOG_DIR
```

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
