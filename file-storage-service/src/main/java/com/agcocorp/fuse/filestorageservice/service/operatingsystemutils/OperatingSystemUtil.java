package com.agcocorp.fuse.filestorageservice.service.operatingsystemutils;

import static com.agcocorp.fuse.filestorageservice.service.operatingsystemutils.OperatingSystemType.UNIX;
import static com.agcocorp.fuse.filestorageservice.service.operatingsystemutils.OperatingSystemType.WINDOWS;
import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

public class OperatingSystemUtil {

    public static OperatingSystemType getCurrentOperatingSystem() {
        if (IS_OS_LINUX || IS_OS_UNIX || IS_OS_MAC || IS_OS_MAC_OSX) {
            return UNIX;
        } else if (IS_OS_WINDOWS) {
            return WINDOWS;
        }
        return null;
    }
}
