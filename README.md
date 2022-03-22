# Bedrock

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/158b774a4d4b4a7a9368af58f96d5dd9)](https://app.codacy.com/gh/DRE2N/Bedrock?utm_source=github.com&utm_medium=referral&utm_content=DRE2N/Bedrock&utm_campaign=Badge_Grade_Settings)

Core library for Bukkit plugins of DRE2N, containing commands, configurations, user caching and more.

## Features

- A simple command system with TabCompletion, Subcommands, command requirements and permission support.

- A FileConfiguration class loader for easy config access and management.

- An easy way to load multiple language files and its messages. Messages might contain placeholders,
  which will be replaced with provided arguments (if given) on access.

- The MessageUtil class offers adventure's [MiniMessage](https://github.com/KyoriPowered/adventure) support,
  message centering and other helpful message utilities.

- A JavaPlugin extension class to manage plugin configs, version control, language files, permission/economy
  provider and plugin updater.

- Simple and RAM efficient User caching with basic User interface class to override, providing onJoin and
  onQuit methods.

- A bunch of other utility classes and methods to simplify the development process.

## Compatibility

This library supports paper version 1.18.2 and higher. Non-paper versions do build but lack some features,
mostly related to adventure's MiniMessage.

## DRECommons

This library is an updated paper-only version of [DRECommons](https://github.com/DRE2N/DRECommons).

### Maven

```
<repositories>
    <repository>
        <id>dre-repo</id>
        <url>https://erethon.de/repo</url>
    </repository>
</repositories>

<dependency>
    <groupId>de.erethon</groupId>
    <artifactId>bedrock</artifactId>
    <version>1.2.1</version>
    <scope>compile</scope>
</dependency>
```

### Gradle (Kotlin)

```
repositories {
    maven {
        url = uri("https://erethon.de/repo")
    }
}

dependencies {
    implementation("de.erethon:bedrock:1.2.1")
}
```