This file is a list of known issues that need to be solved.

Problems with api tests:
 - The editor build in the test fixture of the BasePlatformTestCase class is null. Need to find a way to set it up.
 - Need to run the api tests that use BasePlatformTestCase class twice. On the first run, there is a file, which it cannot find. For example starting from project root For-Mainframe/build/idea-sandbox/system-test/index/.persistent/textContentHashes.len. Everything is fine on the second run.
