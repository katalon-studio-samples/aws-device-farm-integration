package com.kms.example.aw_ios.utils;

import com.katalon.utils.LogUtils;
import com.katalon.utils.Logger;
import com.katalon.utils.OsUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KatalonUtils {
    public static final String ARG_CONSOLE_LOG = "-consoleLog";

    public static final String ARG_NO_EXIT = "-noExit";

    private static final List<String> REMOVABLE_ARGS = Arrays.asList(
            ARG_CONSOLE_LOG,
            ARG_NO_EXIT
    );

    /**
     * Execute Katalon Studio test projects. Katalon Studio can be downloaded and installed automatically.
     *
     * @param version                 Version of Katalon Studio to be installed. Ignored if {@code location} is provided.
     * @param location                Local location where Katalon Studio has been pre-installed. If this argument is null or empty the package will be downloaded and installed automatically.
     * @param projectPath             Path to the Katalon Studio project to be executed. Ignored if provided by (@code executeArgs}.
     * @param executeArgs             Arguments for Katalon Studio CLI, without {@code -runMode}. If {@code -projectPath} is missing, the argument {@code projectPath} will be used.
     * @param x11Display              Linux only. This value will be used as the {@code DISPLAY} environment variable.
     * @param xvfbConfiguration       Linux only. This value will be used as the arguments for {@code xvfb-run}.
     * @param environmentVariablesMap Environment variables available when executing Katalon
     * @return true if the exit code is 0, false otherwise.
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean executeKatalon(
            String version,
            String location,
            String projectPath,
            String executeArgs,
            String x11Display,
            String xvfbConfiguration,
            Map<String, String> environmentVariablesMap,
            String rootDir)
            throws IOException, InterruptedException {

        String katalonDirPath;

        if (StringUtils.isBlank(location)) {
            File katalonDir = KatalonDownloadUtils.getKatalonPackage(version, rootDir);
            katalonDirPath = katalonDir.getAbsolutePath();
        } else {
            katalonDirPath = location;
        }

        ConsoleLogger.logInfo("Using Katalon Studio at " + katalonDirPath);

        if (executeArgs.equals("")) {
            ConsoleLogger.logInfo("Arguments are empty, no tests will be executed");
            return false;
        }

        String katalonExecutableFile;
        Path katalonPath, kataloncPath;
        String os = OsUtils.getOSVersion(ConsoleLogger.getInstance());
        if (os.contains("macos")) {
            kataloncPath = Paths.get(katalonDirPath, "Katalon Studio Engine.app", "Contents", "MacOS", "katalonc");
            katalonPath = Paths.get(katalonDirPath, "Contents", "MacOS", "katalon");
        } else if (os.contains("linux")) {
            kataloncPath = Paths.get(katalonDirPath, "katalonc");
            katalonPath = Paths.get(katalonDirPath, "katalon");
        } else {
            kataloncPath = Paths.get(katalonDirPath, "katalonc.exe");
            katalonPath = Paths.get(katalonDirPath, "katalon.exe");
        }

        if (Files.exists(kataloncPath)) {
            katalonExecutableFile = kataloncPath.toAbsolutePath().toString();
            makeDriversExecutable(katalonDirPath, true);
        } else {
            katalonExecutableFile = katalonPath.toAbsolutePath().toString();
            makeDriversExecutable(katalonDirPath, false);
        }

        return executeKatalon(
                katalonExecutableFile,
                projectPath,
                executeArgs,
                x11Display,
                xvfbConfiguration,
                environmentVariablesMap
        );
    }

    private static boolean executeKatalon(
            String katalonExecutableFile,
            String projectPath,
            String executeArgs,
            String x11Display,
            String xvfbConfiguration,
            Map<String, String> environmentVariablesMap)
            throws IOException, InterruptedException {
        File file = new File(katalonExecutableFile);
        if (!file.exists()) {
            file = new File(katalonExecutableFile + ".exe");
        }
        if (file.exists()) {
            file.setExecutable(true);
        }
        if (katalonExecutableFile.contains(" ")) {
            katalonExecutableFile = "\"" + katalonExecutableFile + "\"";
        }
        String command = katalonExecutableFile;

        if (!executeArgs.contains("-noSplash")) {
            command += " -noSplash ";
        }

        if (!executeArgs.contains("-runMode=console")) {
            command += " -runMode=console ";
        }

        if (!executeArgs.contains("-projectPath")) {
            command += " -projectPath=\"" + projectPath + "\" ";
        }
        command += " " + executeArgs + " ";

        // Remove removable arguments
        command = REMOVABLE_ARGS.stream()
                .reduce(command, (newCommand, arg) -> newCommand.replace(" " + arg, ""));

        Path workingDirectory = Files.createTempDirectory("katalon-");

        return OsUtils.runCommand(
                ConsoleLogger.getInstance(),
                command,
                workingDirectory,
                x11Display,
                xvfbConfiguration,
                environmentVariablesMap);
    }

    private static void makeDriversExecutable(String katalonDir, boolean isKatalonc)
            throws IOException {

        Path driverDirectoryPath = null;
        String os = OsUtils.getOSVersion(ConsoleLogger.getInstance());
        if (os.contains("macos")) {
            if (isKatalonc) {
                driverDirectoryPath = Paths.get(katalonDir, "Katalon Studio Engine.app", "Contents", "Eclipse",
                        "configuration", "resources", "drivers")
                        .toAbsolutePath();
            } else {
                driverDirectoryPath = Paths.get(katalonDir, "Contents", "Eclipse",
                        "configuration", "resources", "drivers")
                        .toAbsolutePath();
            }
        } else {
            driverDirectoryPath = Paths.get(katalonDir, "configuration", "resources", "drivers")
                    .toAbsolutePath();
        }

        ConsoleLogger.logInfo("Making driver executables...");
        if (driverDirectoryPath != null) {
            ConsoleLogger.logInfo("Drivers folder at: " + driverDirectoryPath.toAbsolutePath().toString());
            Files.walk(driverDirectoryPath).filter(Files::isRegularFile).forEach(a -> {
                ConsoleLogger.logInfo("Set " + a.getFileName().toString() + " as executable !");
                a.toFile().setExecutable(true);
            });
        } else {
            ConsoleLogger.logInfo(" Could not find Drivers folder !");
        }

    }
}
