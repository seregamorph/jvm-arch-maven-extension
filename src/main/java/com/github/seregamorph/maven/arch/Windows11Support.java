package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static com.github.seregamorph.maven.arch.JvmArchMojo.PROP_SKIP_JVM_ARCH;

class Windows11Support {

    private static final String CMD_SYSTEMINFO = "systeminfo";

    static void checkArch(Log log, String arch) throws MojoExecutionException {
        if ("amd64".equals(arch)) {
            // The Maven is started on Windows intel-based JVM, but it can be ARM CPU
            // So, we need to check the real architecture
            try {
                // Special notes. The PROCESSOR_ARCHITECTURE env variable will not give the correct result,
                // because in case of CPU emulation, it will return "AMD64" instead of "ARM64".
                log.info("Executing '" + CMD_SYSTEMINFO + "'");
                Process process = Runtime.getRuntime().exec(CMD_SYSTEMINFO);
                if (process.waitFor(5, TimeUnit.SECONDS)) {
                    try (InputStream in = process.getInputStream()) {
                        // note: it's multiline and localized (not only English)
                        String systemInfo = IOUtils.read(in).trim();
                        log.debug("System info:\n" + systemInfo);
                        /*
                        Sample value (substring of multi-line)
                        ...
                        System Type:    ARM64-based PC
                        Processor(s):   1 Processor(s) Installed.
                                        [01]: ARMv8 (64-bit) Family 8 Model 0 Revision   0 Apple ~2000 МГц
                        ...
                        or for x64
                        ...
                        System Type:    x64-based PC
                        ...
                        */
                        String javaHome = System.getProperty("java.home");
                        if (systemInfo.contains("ARM64-based PC")) {
                            throw new MojoExecutionException("The Maven is started on Windows x64-based JVM\n"
                                    + javaHome + " but the real CPU is 'ARM64'. To avoid performance overhead, "
                                    + "please use the proper JVM for Windows (aarch64).\n"
                                    + "To skip this validation, use '-D" + PROP_SKIP_JVM_ARCH + "=true' option.");
                        }
                    }
                } else {
                    log.warn("Timeout waiting for CPU brand");
                }
            } catch (IOException | InterruptedException e) {
                log.error("Failed to get CPU architecture", e);
            }
        }
    }

    private Windows11Support() {
    }
}
