# Data Pipeline Sample Application
This application is a follow-up to a design discussion, illustrating high-level functionality of syncing an external API to a local database in a distributed and fault-tolerant manner. It's written in Kotlin and uses Gradle as a build tool.

## Goals
- Pluggable integration for different Ticket based external systems using generic interfaces.
- Saving API data to disk so that we can perform data transformation separately
- Using kafka to design the data pipelin
 
## Prerequisites

Before you begin, ensure you have met the following requirements:

- You'll need JDK to run this
- You have installed [Homebrew](https://brew.sh/), a package manager for Mac.

## Installing JDK using Homebrew

Java Development Kit (JDK) is a software development environment used for developing Java applications. To install JDK using Homebrew, follow these steps:

Open Terminal and enter the following command:

```bash
brew install openjdk@11
```
After the installation is complete, you can verify it by running:
```bash
java -version
```

Then navigate to the project directory:
```bash
cd data-pipeline
```

Use gradle to run the application
```bash
./gradlew run
```

