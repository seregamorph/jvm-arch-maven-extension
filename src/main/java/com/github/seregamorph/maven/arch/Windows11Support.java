package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.logging.Log;

class Windows11Support {

    static void checkArch(Log log, String arch) throws JvmArchViolationException {
        if ("amd64".equals(arch)) {
            // The Maven is started on Windows intel-based JVM, but it can be ARM CPU with x64 emulation.
            // So, we need to check the real CPU architecture.
            // Special notes. The PROCESSOR_ARCHITECTURE env variable will not give the correct result,
            // because in case of CPU emulation, it will return "AMD64" instead of "ARM64".
            String processorIdentifier = System.getenv("PROCESSOR_IDENTIFIER");
            log.info("Processor identifier: " + processorIdentifier);
            // sample values
            // "Intel64 Family 6 Model 158 Stepping 9, GenuineIntel"
            // or for ARM CPU architecture
            // "ARMv8 (64-bit) Family 8 Model 0 Revision   0, Apple"
            if (processorIdentifier != null && processorIdentifier.contains("ARMv8")) {
                String javaHome = System.getProperty("java.home");
                throw new JvmArchViolationException("The Maven is started on Windows x64-based JVM\n"
                        + javaHome + " but the real CPU is '" + processorIdentifier
                        + "'. To avoid emulation performance overhead, "
                        + "please use the proper JVM for Windows (aarch64).");
            }
        }
    }

    private Windows11Support() {
    }
}
