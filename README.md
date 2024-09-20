
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
