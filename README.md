
# CPU architecture validation plugin
Checks if the JVM is started for the real CPU architecture to avoid performance overhead of running Rosetta.

# How it works
If the JVM architecture is `"x86_64"` and the OS is `"Mac OS X"`, the plugin executes the following command:
```bash
sysctl -n machdep.cpu.brand_string
``` 
to ensure if it's a Rosetta emulation or not.

## Usage
Add to the root pom.xml:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.seregamorph</groupId>
            <artifactId>jvm-arch-maven-plugin</artifactId>
            <version>0.1-SNAPSHOT</version>
            <inherited>false</inherited>
            <executions>
                <execution>
                    <id>jvm-arch</id>
                    <goals>
                        <goal>jvm-arch</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Sample failure:
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.015 s (Wall Clock)
[INFO] Finished at: 2024-12-14T18:01:12+01:00
[INFO] ------------------------------------------------------------------------
[INFO] 3 goals, 3 executed
[ERROR] Failed to execute goal com.github.seregamorph:jvm-arch-maven-plugin:0.1:jvm-arch (jvm-arch) on project server: The Maven is started on macOS x64-based JVM
[ERROR] /Users/user/Java/amazon-corretto-17-x64.jdk/Contents/Home but the real CPU is 'Apple M3 Pro'. To avoid performance overhead, please use the proper JVM for Apple Silicon (aarch64).
[ERROR] To skip this validation, use '-DskipJvmArch=true' option.
```
