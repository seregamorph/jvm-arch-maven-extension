package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

final class MacOsSupport {

    private static final String CMD_GET_CPU_BRAND = "sysctl -n machdep.cpu.brand_string";

    static void checkArch(Log log, String arch) throws JvmArchViolationException {
        if ("x86_64".equals(arch)) {
            // The Maven is started on macOS intel-based JVM, but it can be Apple Silicon CPU
            // So, we need to check the real architecture
            try {
                // Special notes. Both "uname -m" and "machine" commands will not give correct result,
                // because in case of Rosetta, they will return "x86_64" or "i486" instead of "arm64"/"arm64e".
                log.info("Executing '" + CMD_GET_CPU_BRAND + "'");
                Process process = Runtime.getRuntime().exec(CMD_GET_CPU_BRAND);
                if (process.waitFor(5, TimeUnit.SECONDS)) {
                    try (InputStream in = process.getInputStream()) {
                        String cpuBrand = read(in).trim();
                        log.info("CPU Brand: " + cpuBrand);
                        // Sample values:
                        // "Apple M1", "Apple M3 Pro", "Apple M4 Max" for Apple Silicon
                        // "Intel(R) Core(TM) i7-7700HQ CPU @ 2.80GHz" for Intel
                        String javaHome = System.getProperty("java.home");
                        if (cpuBrand.startsWith("Apple ")) {
                            throw new JvmArchViolationException("The Maven is started on macOS x64-based JVM\n"
                                    + javaHome + " but the real CPU is '" + cpuBrand + "'. To avoid emulation "
                                    + "performance overhead, please use the proper JVM for Apple Silicon (aarch64).");
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

    private static String read(InputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, read));
        }
        return builder.toString();
    }

    private MacOsSupport() {
    }
}
