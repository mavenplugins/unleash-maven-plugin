# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

<!-- Format restrictions - see https://common-changelog.org and https://keepachangelog.com/ for details -->
<!-- Each Release must start with a line for the release version of exactly this format: ## [version] -->
<!-- The subsequent comment lines start with a space - not to irritate the release scripts parser!
 ## [major.minor.micro]
 <empty line> - optional sub sections may follow like:
 ### Added:
 - This feature was added
 <empty line>
 ### Changed:
 - This feature was changed
 <empty line>
 ### Removed:
 - This feature was removed
 <empty line>
 ### Fixed:
 - This issue was fixed
 <empty line>
 <empty line> - next line is the starting of the previous release
 ## [major.minor.micro]
 <empty line>
 <...>
 !!! In addition the compare URL links are to be maintained at the end of this CHANGELOG.md as follows.
     These links provide direct access to the GitHub compare vs. the previous release.
     The particular link of a released version will be copied to the release notes of a release accordingly.
     At the end of this file appropriate compare links have to be maintained for each release version in format:
 
  +-current release version
  |
  |                   +-URL to this repo              previous release version tag-+       +-current release version tag
  |                   |                                                            |       |
 [major.minor.micro]: https://github.com/mavenplugins/unleash-maven-plugin/compare/vM.N.u..vM.N.u
-->
<!--
## [Unreleased]

### Additions
- TBD

### Changes
- TBD

### Deprecated
- TBD

###	Removals
- TBD

### Fixes
- TBD

###	Security
- TBD
-->

## [Unreleased]

### Changes
- TBD


## [2.12.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![2.12.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=2.12.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/2.12.0)

### Summary
- Add Workflow action `exitWithRollbackNoError` to abort the workflow  with a rollback of previous actions but with Maven success.<br>
  This is supposed for test purpose only. See [maven-cdi-plugin-utils #4](https://github.com/mavenplugins/maven-cdi-plugin-utils/issues/4).

### Updates
- ThrowEnforceRollbackWithoutErrorException.java:
  - added

- pom.xml:
  - update dependency `io.github.mavenplugins:cdi-plugin-utils:3.4.0` -> `io.github.mavenplugins:cdi-plugin-utils:3.5.0`


## [2.11.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![2.11.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=2.11.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/2.11.0)

### Summary
- Add enhancements provided by RockwellAutomation and hmdebenque
- Update requirements:
  - Apache Maven `3.3.9` or higher
- Update dependency `io.github.mavenplugins:cdi-plugin-utils:3.4.0` -> `io.github.mavenplugins:cdi-plugin-utils:3.4.1`
- Fix JavaDoc warnings

### Updates
- Replace POM property variables in scmMessagePrefix - #2
- Fix TagScm for empty scm properties - #3
- Add action `logScmProviderName` - #3
- Fix checks to tolerate the reactor projects own snapshot dependency artifacts and/or own snapshot plugins - #4
- Replace newline symbol '\n' by actual new line character in message prefix - #5
- Prefix tag comments with scmMessagePrefix - #6
- Refactor pom.xml deps + update versions - co-authored by hmdebenque
- Fix file URI relativizing on Windows OS - #8
- Update dependency `io.github.mavenplugins:cdi-plugin-utils:3.4.0` -> `io.github.mavenplugins:cdi-plugin-utils:3.4.1`

- AbstractUnleashMojo.java:
  - update coordinates for artifact-spy-plugin to `io.github.mavenplugins:artifact-spy-plugin:1.0.7`

- FileToRelativePath.java:
  - convert File URIs to lower case on Windows OS runtimes

- DetectReleaseArtifacts.java:
  - make use of FileToRelativePath for file URI relativizing

- ProjectToCoordinates.java,
  ProjectToString.java,
  IsSnapshotProject.java,
  CheckoutRequest.java,
  CommitRequest.java,
  HistoryRequest.java,
  TagRequest.java,
  UpdateRequest.java:
  - Fix JavaDoc warnings - no functional change

- README.md:
  - Update requirement: Apache Maven `3.3.9` or higher


## [2.10.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![2.10.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=2.10.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/2.10.0)

### Summary
- Initial release of this artifact with new groupId `io.github.mavenplugins`
- Codewise identical with `com.itemis.maven.plugins:unleash-maven-plugin:2.10.0`<br>No more features nor changes
- Released to Maven Central

### Updates
- pom.xml:
  - update parent pom reference
  - update groupId to io.github.mavenplugins
  - update dependency `com.itemis.maven.plugins:cdi-plugin-utils:3.4.0` -> `io.github.mavenplugins:cdi-plugin-utils:3.4.0`
  - update dependency `com.itemis.maven.plugins:artifact-spy-plugin:1.0.6` -> `io.github.mavenplugins:artifact-spy-plugin:1.0.7`
  - update URLs to fit with new repo location
  - remove obsolete profile disable-java8-doclint

- README.md:
  - update URLs for build tags
  - update URLs of lookup references


<!--
## []

### NeverReleased
- This is just a dummy placeholder to make the parser of GHCICD/release-notes-from-changelog@v1 happy!
-->

[Unreleased]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v2.12.0..HEAD
[2.12.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v2.11.0..v2.12.0
[2.11.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v2.10.0..v2.11.0
[2.10.0]: https://github.com/mavenplugins/unleash-maven-plugin/releases/tag/v2.10.0
