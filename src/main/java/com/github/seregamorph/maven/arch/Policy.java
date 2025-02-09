package com.github.seregamorph.maven.arch;

public enum Policy {

    /**
     * Fail the build if the JVM architecture does not match the real CPU architecture
     */
    FAIL,
    /**
     * Warn the user if the JVM architecture does not match the real CPU architecture
     */
    WARN
}
