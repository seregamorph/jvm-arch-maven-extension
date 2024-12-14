
# CPU architecture validation plugin
Checks if the JVM is started for the real CPU architecture to avoid performance overhead of running Rosetta.

# How it works
If the JVM architecture is `"x86_64"` and the OS is `"Mac OS X"`, the plugin executes the following command:
```bash
sysctl -n machdep.cpu.brand_string
``` 
to ensure if it's a Rosetta emulation or not.

## Usage
Add under in your pom.xml:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.seregamorph</groupId>
            <artifactId>arch-maven-plugin</artifactId>
            <version>0.1</version>
            <inherited>false</inherited>
            <executions>
                <execution>
                    <id>arch</id>
                    <goals>
                        <goal>arch</goal>
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
[INFO] Total time:  8.029 s (Wall Clock)
[INFO] Finished at: 2024-12-14T12:25:24+01:00
[INFO] ------------------------------------------------------------------------
[INFO] 3 goals, 3 executed
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.github.seregamorph:arch-maven-plugin:0.2:arch (arch) on project pt: The Maven is started on macOS x64-based JVM, but the real CPU is 'Apple M3 Pro'. To avoid performance overhead, please use the proper JVM for Apple Silicon (aarch64).
[ERROR] To skip this validation, use '-DskipArch=true' option.
```
