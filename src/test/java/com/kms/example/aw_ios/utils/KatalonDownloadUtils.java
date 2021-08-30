package com.kms.example.aw_ios.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katalon.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class KatalonDownloadUtils {
    private static final String LATEST_VERSION_NAME = "latest";
    private static final String RELEASES_LIST =
            "https://raw.githubusercontent.com/katalon-studio/katalon-studio/master/releases.json";

    /**
     * Using this to add a custom version of Katalon to the release list
     */
    private static final KatalonVersion katalonVersion;

    static {
        katalonVersion = new KatalonVersion();
        katalonVersion.setVersion("8.1.0");
        katalonVersion.setOs(OsUtils.getOSVersion(null));
        katalonVersion.setContainingFolder("Katalon_Studio_Engine_MacOS-8.1.0");
        katalonVersion.setFilename("Katalon_Studio_Engine_MacOS-8.1.0.tar.gz");
        katalonVersion.setUrl("https://katalon.s3.amazonaws.com/STUDIO-70/Katalon_Studio_Engine_MacOS-8.1.0.tar.gz");
    }

    static File getKatalonPackage(String versionNumber, String rootDir)
            throws IOException, InterruptedException {
        KatalonVersion version = getVersionInfo(versionNumber);

        File katalonDir = getKatalonFolder(version.getVersion(), rootDir);
        Path fileLog = Paths.get(katalonDir.toString(), ".katalon.done");

        if (fileLog.toFile().exists()) {
            ConsoleLogger.logInfo("Katalon Studio package has been downloaded already.");
        } else {
            org.apache.commons.io.FileUtils.deleteDirectory(katalonDir);

            boolean katalonDirCreated = katalonDir.mkdirs();
            if (!katalonDirCreated) {
                throw new IllegalStateException("Cannot create directory to store Katalon Studio package.");
            }

            String versionUrl = version.getUrl();
            FileUtils.downloadAndExtract(ConsoleLogger.getInstance(), versionUrl, katalonDir);

            boolean fileLogCreated = fileLog.toFile().createNewFile();
            if (fileLogCreated) {
                ConsoleLogger.logInfo("Katalon Studio has been installed successfully.");
            }
        }

        String[] childrenNames = katalonDir.list((dir, name) -> {
            File file = new File(dir, name);
            return file.isDirectory() && name.contains("Katalon");
        });

        String katalonContainingDirName = Arrays.stream(childrenNames).findFirst().get();
        File katalonContainingDir = new File(katalonDir, katalonContainingDirName);
        return katalonContainingDir;
    }

    private static File getKatalonFolder(String version, String rootDir) {
        String path = rootDir != null ? rootDir : System.getProperty("user.home");

        Path p = Paths.get(path, ".katalon", version);
        return p.toFile();
    }

    private static KatalonVersion getVersionInfo(String versionNumber) throws IOException {
        String os = OsUtils.getOSVersion(ConsoleLogger.getInstance());

        List<KatalonVersion> versions = getVersionList();
        versions.add(katalonVersion);

        String versionNumberToDisplay = StringUtils.isNotBlank(versionNumber) ? versionNumber : LATEST_VERSION_NAME;
        ConsoleLogger.logInfo("Retrieve Katalon Studio version: " + versionNumberToDisplay + ", OS: " + os);
        ConsoleLogger.logInfo("Number of releases: " + versions.size());

        KatalonVersion matchedVersion = findVersionInfo(versions, versionNumber);
        if (matchedVersion != null) {
            ConsoleLogger.logInfo("Katalon Studio is hosted at " + matchedVersion.getUrl() + ".");
            return matchedVersion;
        }
        ConsoleLogger.logInfo("Cannot find the specified version (" + versionNumberToDisplay + ")");
        return null;
    }

    private static KatalonVersion findVersionInfo(List<KatalonVersion> versions, String versionNumber) {
        String os = OsUtils.getOSVersion(ConsoleLogger.getInstance());
        for (KatalonVersion version : versions) {
            if ((version.getVersion().equals(versionNumber)
                    || StringUtils.isBlank(versionNumber)
                    || StringUtils.equals(versionNumber, LATEST_VERSION_NAME))
                    && (version.getOs().equalsIgnoreCase(os))) {
                return version;
            }
        }
        return null;
    }

    private static List<KatalonVersion> getVersionList() throws IOException {
        URL releaseListUrl = new URL(RELEASES_LIST);
        ObjectMapper objectMapper = new ObjectMapper();
        List<KatalonVersion> versions = objectMapper.readValue(
                releaseListUrl,
                new TypeReference<List<KatalonVersion>>() {
                }
        );
        versions.stream().forEach(versionI -> {
            String containingFolder = getContainingFolder(versionI);
            versionI.setContainingFolder(containingFolder);
        });
        return versions;
    }

    private static String getContainingFolder(KatalonVersion version) {
        String containingFolder = version.getContainingFolder();
        if (containingFolder == null) {
            String fileName = version.getFilename();
            String fileExtension = "";
            if (fileName.endsWith(".zip")) {
                fileExtension = ".zip";
            } else if (fileName.endsWith(".tar.gz")) {
                fileExtension = ".tar.gz";
            }
            containingFolder = fileName.replace(fileExtension, "");
        }
        return containingFolder;
    }
}
