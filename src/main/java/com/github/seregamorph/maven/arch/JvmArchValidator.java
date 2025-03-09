package com.github.seregamorph.maven.arch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logic in this class is designed to be used as utility not only in JvmArchMojo, but also without any Maven context.
 */
public final class JvmArchValidator {

    private static final Logger logger = LoggerFactory.getLogger(JvmArchValidator.class);

    /**
     * Checks JVM arch vs real CPU arch. Throws {@link JvmArchViolationException} in case if emulation mode is detected.
     *
     * @throws JvmArchViolationException
     */
    public static void validateJvmArch() throws JvmArchViolationException {
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        logger.debug("os.arch: {}", osArch);
        logger.debug("os.name: {}", osName);
        if ("Mac OS X".equals(osName)) {
            MacOsSupport.checkArch(logger, osArch);
        } else if ("Windows 11".equals(osName)) {
            Windows11Support.checkArch(logger, osArch);
        }
    }

    private JvmArchValidator() {
    }
}
