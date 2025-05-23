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

### 🚨 Removed
- TBD

### 💥 Breaking
- TBD

### 📢 Deprecated
- TBD

### 🚀 New Features
- TBD

### 🐛 Fixes
- TBD

### ✨ Improvements
- TBD

### 🔧 Internal Changes
- TBD

### 🚦 Tests
- TBD

### 📦 Updates
- TBD

### 🔒 Security
- TBD

### 📝 Documentation Updates
- TBD
-->

## [Unreleased]

### Changes
- TBD


## [3.3.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.3.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.3.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.3.0)

### Summary
- Fix overriding POM artifact with a file built in a POM type project. Root caused by [artifact-spy-plugin #4](https://github.com/mavenplugins/artifact-spy-plugin/issues/4) - #23
  - Update `artifact-spy-plugin` version to `1.1.0`
  - Enhance to configure the `artifact-spy-plugin` version
- Allow configuration of alternate deploy repositories identical to `maven-deploy-plugin` - #26
- Add configuration for workflow step `deployArtifacts` to skip actual deployment to remote repositories - #27

### 🐛 Fixes
- Fix overriding POM artifact with a file built in a POM type project. Root caused by [artifact-spy-plugin #4](https://github.com/mavenplugins/artifact-spy-plugin/issues/4) - #23
- Fix authentication for `additionalDeployemntRepositories` if `settings.xml` contains Maven encrypted passwords - #27

### 🚀 New Features
- Allow configuration of `artifact-spy-plugin` - #23
  - [artifactSpyPluginGroupId](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#artifactspyplugingroupid)
  - [artifactSpyPluginArtifactId](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#artifactspypluginartifactid)
  - [artifactSpyPluginVersion](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#artifactspypluginversion)
- Allow configuration of alternate deploy repositories identical to `maven-deploy-plugin` - #26
  - [altDeploymentRepository](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#altdeploymentrepository)
  - [altSnapshotDeploymentRepository](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#altsnapshotdeploymentrepository)
  - [altReleaseDeploymentRepository](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#altreleasedeploymentrepository)
- Add configuration `isDeployDryRun`, property `unleash.isDeployDryRun` to skip actual deployment to remote repositories - #27
  Log the intended deployment details on warn level instead.
  - [isDeployDryRun](https://github.com/mavenplugins/unleash-maven-plugin/wiki/unleash-perform-goals#isdeploydryrun)

### ✨ Improvements
- AbstractUnleashMojo.java:
  - Add parameters:
    - `altDeploymentRepository`, property: `altDeploymentRepository`, default: empty - #26
    - `altSnapshotDeploymentRepository`, property: `altSnapshotDeploymentRepository`, default: empty - #26
    - `altReleaseDeploymentRepository`, property: `altReleaseDeploymentRepository`, default: empty - #26
    - `artifactSpyPluginGroupId`, property: `unleash.artifactSpyPluginGroupId`, default: `io.github.mavenplugins` - #23
    - `artifactSpyPluginArtifactId`, property: `unleash.artifactSpyPluginArtifactId`, default: `artifact-spy-plugin` - #23
    - `artifactSpyPluginVersion`, property: `unleash.artifactSpyPluginVersion`, default: `1.1.0` - #23
    - `isDeployDryRun`, property: `unleash.isDeployDryRun`, default: `false` - #27
  - Update CDI producer for `artifactSpyPlugin` to provide Maven coordinates using the before mentioned parameters - #23

### 📦 Updates
- pom.xml:
  - Update property `<version.artifact-spy-plugin>1.0.7</version.artifact-spy-plugin>` -> `<version.artifact-spy-plugin>1.1.0</version.artifact-spy-plugin>`
  - Add property `<version.plexus-utils>4.0.2</version.plexus-utils>` - #23
  - Add property `<version.plexus-xml>3.0.2</version.plexus-xml>` - #23, #25
  - Exclude transient Maven dependencies from dependency to `io.github.mavenplugins:artifact-spy-plugin` - #23

- unleash-maven-plugin/pom.xml:
  - Add unmanaged dependency `org.codehaus.plexus:plexus-utils:${version.plexus-utils}` - #23
  - Add unmanaged dependency `org.codehaus.plexus:plexus-xml:${version.plexus-xml}` - #23

### 🔧 Internal Changes
- AbstractVersionMojo.java:
  - Replace usage of deprecated `WriterFactory.newPlatformWriter(File)` by `Files.newBufferedWriter(File.toPath())` - #23


## [3.2.1]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.2.1 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.2.1)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.2.1)

### Summary
- Fix vulnerability warnings on dependencies - see **📦 Updates** for details.
- No further functional change

### 📦 Updates
- pom.xml:
  - Update dependency `io.github.mavenplugins:cdi-plugin-utils:4.0.0` -> `io.github.mavenplugins:cdi-plugin-utils:4.0.1`
  - Remove dependency management definition for `com.google.guava:guava`
  - Replace explicit dependency to `com.google.guava:guava` by transient dependency through `io.github.mavenplugins:cdi-plugin-utils`
  - Update dependency `junit:junit:4.12` -> `junit:junit:4.13.2`
  - Update property `<version.guava>23.0</version.guava>` -> `<version.guava.minimum_provided>33.0.0-jre</version.guava.minimum_provided>`
  - Update dependency `org.apache.maven.shared:maven-invoker:3.0.1` -> `org.apache.maven.shared:maven-invoker:3.1.0`
  - Update property `<version.commons-lang3>3.4</version.commons-lang3>` -> `<version.commons-lang3>3.12.0</version.commons-lang3>`
  - Update managed dependency `org.apache.commons:commons-lang3:3.4` -> unmanaged `org.apache.commons:commons-lang3:3.12.0`
  - Add property `<version.commons-text>1.10.0</version.commons-text>`
  - Add unmanaged dependency `org.apache.commons:commons-text:1.10.0`
  - Update provided dependency to Maven version `3.3.9` -> `3.8.1`
  - Remove `org.apache.maven:maven-core` from dependency management

- unleash-maven-plugin/pom.xml:
  - Update managed dependency `org.apache.commons:commons-lang3:3.4` -> unmanaged `org.apache.commons:commons-lang3:3.12.0`
  - Add unmanaged dependency `org.apache.commons:commons-text:1.10.0`

- utils/pom.xml:
  - Update dependency scope for `com.google.guava:guava` to `provided`

- scm-provider-api/pom.xml:
  - Remove explicit dependency to `com.google.guava:guava`
  - Update managed dependency `org.apache.commons:commons-lang3:3.4` -> unmanaged `org.apache.commons:commons-lang3:3.12.0`

### 🔧 Internal Changes
- AbstractVersionMojo.java:
  - Replace usage of deprecated `SystemUtils.LINE_SEPARATOR` by `System.lineSeparator()`

- ScmMessagePrefixUtil.java:
  - Replace usage of deprecated `org.apache.commons.lang3.StringEscapeUtils` by `org.apache.commons.text.StringEscapeUtils`

### 🚦 Tests
- PomUtilTest.java, ReleaseUtilTest.java:
  - Fix for deprecation warning on `ExpectedException.none()`


## [3.2.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.2.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.2.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.2.0)

### Summary
- Preserve leading `0` for increased version parts with the same number of digits if not exceeding the digits required for the new value. - #14<br>
  - Add optional system property `currentVersion` to goals `unleash:nextSnapshotVersion` and `unleash:releaseVersion`.
    Default is `${project.version}`. This property is being used as the base for any version calculation.
  - Add further `VersionUpgradeStrategy` options:
    - `BUILD` - 4th version part
    - `PART_5` - 5th version part
    - `PART_6` - 6th version part
    - `PART_7` - 7th version part
    - `PART_8` - 8th version part
- Refactor class `FileToRelativePath` as a pre-requisite to fix `unleash-scm-provider-git`
  for Maven projects located in a sub folder of the checkout folder. - #15

### :bug: Fixes
- Fix issue raised by workflow step `SetNextDevVersion` for Git SCM projects. - #15<br>
  If the Maven project base dir is within a sub folder of the git checkout directory,
  then the next dev modified POMs did not get recognized as changed files to be commited.<br>
  👉 Requires `unleash-scm-provider-git` version `3.1.0` or later!

### Updates
- pom.xml:
  - update parent pom version

- Plugin Mojo classes:
  - AbstractVersionMojo.java:
    - add property `currentVersion`
  - NextSnapshotVersionMojo.java:
    - fix class comment
  
- Version calculation classes:
  - Version.java:
    - preserve leading 0's of version part being increased
  - VersionUpgradeStrategy.java:
    - add version part options (s. Summary)
  - MavenVersionUtilTest.java:
    - Add according test cases

- Others:
  - Deprecate `com.itemis.maven.plugins.unleash.util.FileToRelativePath`
    - this is moved to `com.itemis.maven.plugins.unleash.scm.utils.FileToRelativePath`
  - FollowUp refactored classes:
    - DetectReleaseArtifacts.java
    - DevVersionUtil.java


## [3.1.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.1.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.1.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.1.0)

### Summary
- Add goals `unleash:nextSnapshotVersion` and `unleash:releaseVersion`
- Bump parent pom reference `org-parent` to version `7`

### Updates
- pom.xml:
  - update parent pom version

- Add Mojo classes:
  - AbstractVersionMojo.java
  - NextSnapshotVersionMojo.java
  - ReleaseVersionMojo.java


## [3.0.3]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.0.3 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.0.3)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.0.3)

### Summary
- 👉 **Reverted** changes from release `3.0.2`
- More generic replacement of escaped characters for `tagNamePattern` and `scmMessagePrefix`

### Updates
- ScmMessagePrefixUtil.java:
  - add to provide method `public static String unescapeScmMessagePrefix(final String scmMessagePrefixConfigured)`
  - add JUnit test `ScmMessagePrefixUtilTest`

- ReleaseUtil.java:
  - deprecate `public static String getTagName(...)`
  - replace by `public static String getScmPatternResolved((...)`

- ReleaseMetadata.java:
  - follow up previous API change

- AbstractUnleashMojo.java:
  - method getScmMessagePrefix():
    - follow up previous API change
    - make use of `ScmMessagePrefixUtil.unescapeScmMessagePrefix(...)`


## [3.0.2] - DEPRECATED -
<!-- !!! Align version in badge URLs as well !!! -->
[![3.0.2 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.0.2)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.0.2)

### Summary
- DEPRECATED - This release works but changes will be reverted within future releases
- Move `com.itemis.maven.plugins.unleash.util.VersionUpgradeStrategy` to new Maven module `unleash-shared`.
  Module `unleash-shared` is a dependency shared by this Maven plugin and the [JenkinsCI unleash-plugin](https://github.com/jenkinsci/unleash-plugin).
  This is to get JenkinsCI unleash-plugin rid of useless transient dependencies.

### Updates
- pom.xml:
  - add module `shared`
  - add dependencyManagement for `unleash-shared`

- unleash-utils/pom.xml:
  - add dependency to `unleash-shared`

- VersionUpgradeStrategy.java:
  - moved from `unleash-utils` to `unleash-shared`
  - package and class name pre-served for backward compatibility

- README.md:
  - add tag to reflect E2E test state


## [3.0.1]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.0.1 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.0.1)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.0.1)

### Summary
- Fix wrong URLs for **Project URL** and **Source Control** rendered on [Maven Central](https://central.sonatype.com) for this project.
- No functional, code or dependency change

### Updates
- pom.xml:
  - add appropriate attributes to nodes `<project>` and `<scm>` - #9


## [3.0.0]
<!-- !!! Align version in badge URLs as well !!! -->
[![3.0.0 Badge](https://img.shields.io/nexus/r/io.github.mavenplugins/unleash-maven-plugin?server=https://s01.oss.sonatype.org&label=Maven%20Central&queryOpt=:v=3.0.0)](https://central.sonatype.com/artifact/io.github.mavenplugins/unleash-maven-plugin/3.0.0)

### Summary
- Add Workflow action `exitWithRollbackNoError` to abort the workflow  with a rollback of previous actions but with Maven success.<br>
  This is supposed for test purpose only. See [maven-cdi-plugin-utils #4](https://github.com/mavenplugins/maven-cdi-plugin-utils/issues/4).
- Update dependency `io.github.mavenplugins:cdi-plugin-utils:3.4.1` -> `io.github.mavenplugins:cdi-plugin-utils:4.0.0`
  - => This plugin now works for **Java 8, 11, 17 and 21**

### Compatibility
- 👉 This release requires to be used with the following minimum versions of addons:
  - `unleash-scm-provider-git: >= 3.0.0`
  - `unleash-scm-provider-svn: >= 3.0.0`
  - `cdi-plugin-hooks:         >= 0.2.0`
  Reason: CDI dependencies changed from Javax to Jakarta EE

### Updates
- ExitWithRollbackNoError.java:
  - workflow action `exitWithRollbackNoError` added

- pom.xml:
  - update dependency `io.github.mavenplugins:cdi-plugin-utils:3.4.1` -> `io.github.mavenplugins:cdi-plugin-utils:4.0.0`
  - improved and fixed Maven dependencies to meet scope `provided` Maven plugin dependency requirements
  - other dependency version updates:
    - `plexus-interactivity-api:  1.0-alpha-6 -> 1.3`
    - `maven-plugin-plugin:       3.5.2 -> 3.13.1`
    - `plexus-component-metadata: 1.7.1 -> 2.2.0`
    - `versions-maven-plugin:     2.5 -> 2.15.0`


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

[Unreleased]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.3.0..HEAD
[3.3.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.2.1..v3.3.0
[3.2.1]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.2.0..v3.2.1
[3.2.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.1.0..v3.2.0
[3.1.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.0.3..v3.1.0
[3.0.3]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.0.2..v3.0.3
[3.0.2]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.0.1..v3.0.2
[3.0.1]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v3.0.0..v3.0.1
[3.0.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v2.11.0..v3.0.0
[2.11.0]: https://github.com/mavenplugins/unleash-maven-plugin/compare/v2.10.0..v2.11.0
[2.10.0]: https://github.com/mavenplugins/unleash-maven-plugin/releases/tag/v2.10.0
