# (Self-)Updated Smartspacer Plugins

## Versions

Each plugin's version follows MAJOR.MINOR.PATCH: MAJOR for big or breaking changes, MINOR for
new features or changes to how it works, PATCH for bug fixes and small adjustments. The Uber
plugin keeps its original versionCode scheme (1.1.0 → 110).

## Releases

A tag `uber-v<version>` publishes the Uber plugin (`.github/workflows/release-uber.yml`), with
`.github/release-notes-uber.md` as the text, signed with the permanent key from the
`SIGNING_KEYSTORE_BASE64` and `SIGNING_PASSWORD` secrets (alias `smartspacer-plugins`).

Never publish a release, push a version tag or change release notes without asking first. Show
the version number and the full release notes, let me edit them, and wait for my OK.
