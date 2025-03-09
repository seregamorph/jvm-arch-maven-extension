
[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.seregamorph/jvm-arch-maven-extension?style=flat-square)](https://central.sonatype.com/artifact/com.github.seregamorph/jvm-arch-maven-extension/overview)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

# CPU architecture validation extension
Checks if the JVM is started for the real CPU architecture to avoid performance overhead of running Rosetta.

## How it works (macOS X)
If the JVM architecture is `"x86_64"` and the OS is `"Mac OS X"`, the extension executes the following command:
```shell
sysctl -n machdep.cpu.brand_string
``` 
to ensure if it's a Rosetta ~~emulation~~ translation or not.

## How it works (Windows)
If the JVM architecture is `"amd64"` and the OS is `"Windows 11"`, the extension checks the `PROCESSOR_IDENTIFIER`
environment variable to ensure if it's a CPU emulation or not.

## Usage
Add to the root pom.xml:
```xml
<build>
    <extensions>
        <extension>
            <groupId>com.github.seregamorph</groupId>
            <artifactId>jvm-arch-maven-extension</artifactId>
            <version>0.1-SNAPSHOT</version>
        </extension>
    </extensions>
</build>
```
or to `.mvn/extensions.xml`:
```xml
<extensions>
    <extension>
        <groupId>com.github.seregamorph</groupId>
        <artifactId>jvm-arch-maven-extension</artifactId>
        <version>0.1-SNAPSHOT</version>
    </extension>
</extensions>
```

Supported operating systems:
* Windows (both Intel- and ARM-based)
* macOS (both Intel- and ARM-based)

For other operating systems like Linux this extension is just no-op.

Sample failure:
```
[ERROR] The started JVM is macOS x64-based
[ERROR] /Users/user/Java/amazon-corretto-17-x64.jdk/Contents/Home but the real CPU is 'Apple M3 Pro'.
[ERROR] To avoid emulation performance overhead, please use the proper JVM for Apple Silicon (aarch64).
```

Hint: to skip this validation, use `-DskipJvmArch=true` option.

## Configuring
You can set up a configuration file in `.mvn/jvm-arch.properties` of the root of your project.
Supported parameters:

| Parameter | Description                                                              |
| --- |--------------------------------------------------------------------------|
| `policy` | Can be `FAIL` (default) or `WARN`                                        |
| `additionalFailureMessage` | Optional extended text failure message like instruction where to consult |

Sample warning at the end of the build with
```
policy=WARN
additionalFailureMessage=For additional details please consult with devplatform team
```
configuration if specified in `.mvn/jvm-arch.properties`:
```
[WARNING] The started JVM is macOS x64-based 
/user/morph/Java/amazon-corretto-17-x64.jdk/Contents/Home but the real CPU is 'Apple M3 Pro'.
To avoid emulation performance overhead, please use the proper JVM for Apple Silicon (aarch64).
For additional details please consult with devplatform team
```
