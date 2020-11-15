package com.agcocorp.fuse.filestorageservice.service.directoryutils;

import static com.agcocorp.fuse.filestorageservice.service.operatingsystemutils.OperatingSystemUtil.getCurrentOperatingSystem;
import static java.io.File.separator;
import static java.lang.System.getProperty;

import com.agcocorp.fuse.filestorageservice.service.operatingsystemutils.OperatingSystemType;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class TemporaryDirectoryDetector {

    public static String getTemporaryDirectory() {
        String systemTemporaryDirectory = getProperty("java.io.tmpdir");

        if (systemTemporaryDirectory == null) {
            String temporaryDirectoryByOperatingSystemType =
                    getTemporaryDirectoryByOperatingSystemType(getCurrentOperatingSystem());

            systemTemporaryDirectory = (temporaryDirectoryByOperatingSystemType != null ?
                    temporaryDirectoryByOperatingSystemType : getCurrentWorkingDirectory());
        }

        return addEndingFileSeparatorToTmpDirIfMissing(systemTemporaryDirectory);
    }

    private static String getCurrentWorkingDirectory() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + FileSystems.getDefault().getSeparator();
    }

    private static String getTemporaryDirectoryByOperatingSystemType(OperatingSystemType currentOperatingSystemType) {
        String temporaryDirectoryPath = null;
        if (currentOperatingSystemType == null) {
            return null;
        }

        switch (currentOperatingSystemType) {
            case UNIX:
                temporaryDirectoryPath = "/tmp/";
                break;
            case WINDOWS:
                temporaryDirectoryPath = "c:\\temp\\";
            default:
                break;
        }
        return temporaryDirectoryPath;
    }

    private static String addEndingFileSeparatorToTmpDirIfMissing(String systemTemporaryDirectory) {
        String systemTemporaryDirectoryWithEndingSeparator = null;
        if (systemTemporaryDirectory != null) {
            if (!systemTemporaryDirectory.endsWith(separator)) {
                systemTemporaryDirectoryWithEndingSeparator = systemTemporaryDirectory + separator;
            }else{
                systemTemporaryDirectoryWithEndingSeparator = systemTemporaryDirectory;
            }
        }
        return systemTemporaryDirectoryWithEndingSeparator;
    }
}
