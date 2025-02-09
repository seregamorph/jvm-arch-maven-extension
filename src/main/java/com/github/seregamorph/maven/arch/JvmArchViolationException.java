package com.github.seregamorph.maven.arch;

import org.apache.maven.plugin.MojoExecutionException;

public class JvmArchViolationException extends MojoExecutionException {

    public JvmArchViolationException(String message) {
        super(message);
    }
}
