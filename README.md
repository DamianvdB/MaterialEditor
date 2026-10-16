# Material Note Editor

Watch this space :)

## License

    Copyright 2020 Damian van den Berg

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
## Maintenance release 1.0.5

This patch branches from tag `v1.0.4`. It fixes IME Next/Done/Enter dispatch and
uses an idempotent keyboard-show request. It includes no changes from newer
editor development.

Use JDK 17 and this repository's Gradle wrapper. The existing library toolchain
uses Android SDK 33 and Build Tools 33.0.0. To run tests and create a local Maven
repository without a PGP key:

```sh
./gradlew :materialchecklist:testDebugUnitTest \
  :materialchecklist:publishReleasePublicationToMaintenanceRepository \
  -x :materialchecklist:signReleasePublication
```

Output: `build/maintenance-repository/io/github/damianvdb/material-editor/1.0.5/`.
D Notes can consume it with `-PmaterialEditorRepository=/absolute/path/to/build/maintenance-repository`.
This is local verification; it does not publish to Maven Central.

For public publication, configure a Central Portal user token and the existing
PGP signing credentials outside Git. The `OSSRH_USERNAME`/`OSSRH_PASSWORD`
variables now mean the Portal token values, not the retired OSSRH credentials.
PGP uses `SIGNING_KEY_ID`, `SIGNING_PASSWORD` and `SIGNING_SECRET_KEY_RING_FILE`.
The Android upload keystore is unrelated to Maven PGP signing.

The remote repository now uses Sonatype's OSSRH compatibility service. After
an approved `publishReleasePublicationToSonatypeRepository` run, transfer the
staging repository to the Portal and review/release it there, following
[Sonatype's Gradle publishing instructions](https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/).
Do not use automatic publication. Confirm 1.0.5 resolves from Maven Central
before releasing D Notes without the local repository property.
