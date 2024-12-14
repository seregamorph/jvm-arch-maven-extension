package com.github.seregamorph.maven.arch;

import java.io.IOException;
import java.io.InputStream;

class IOUtils {

    static String read(InputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, read));
        }
        return builder.toString();
    }

    private IOUtils() {
    }
}
