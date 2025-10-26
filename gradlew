'''#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# @author: Andres Almiray
#

#
# This script is a simple wrapper around the gradle executable.
#
# It looks for the `gradle` executable in the following locations:
#
#   1. `$GRADLE_HOME/bin`
#   2. `$HOME/.sdkman/candidates/gradle/current/bin`
#   3. `$HOME/.gvm/gradle/current/bin`
#   4. `$HOME/.jenv/versions/gradle/bin`
#   5. `$(brew --prefix gradle)/bin` (if Homebrew is installed)
#   6. `$(whence -p gradle)` (if ZSH is the shell)
#   7. `$(command -v gradle)` (if BASH is the shell)
#
# You can just as easily invoke gradle directly.
#
# You can also configure the following environment variables:
#
#   * `GRADLE_HOME` - location of a Gradle installation.
#   * `GRADLE_OPTS` - additional JVM options.
#   * `JAVA_HOME`   - location of a JDK installation.
#   * `JAVA_OPTS`   - additional JVM options.
#

# Stop on error
set -e

#
# Resolve the location of the gradle executable
#
if [ -n "$GRADLE_HOME" ] && [ -x "$GRADLE_HOME/bin/gradle" ]; then
    GRADLE_CMD="$GRADLE_HOME/bin/gradle"
elif [ -n "$HOME" ] && [ -d "$HOME/.sdkman/candidates/gradle/current/bin" ] && [ -x "$HOME/.sdkman/candidates/gradle/current/bin/gradle" ]; then
    GRADLE_CMD="$HOME/.sdkman/candidates/gradle/current/bin/gradle"
elif [ -n "$HOME" ] && [ -d "$HOME/.gvm/gradle/current/bin" ] && [ -x "$HOME/.gvm/gradle/current/bin/gradle" ]; then
    GRADLE_CMD="$HOME/.gvm/gradle/current/bin/gradle"
elif [ -n "$HOME" ] && [ -d "$HOME/.jenv/versions/gradle/bin" ] && [ -x "$HOME/.jenv/versions/gradle/bin/gradle" ]; then
    GRADLE_CMD="$HOME/.jenv/versions/gradle/bin/gradle"
elif type brew &>/dev/null && [ -n "$(brew --prefix gradle)" ] && [ -x "$(brew --prefix gradle)/bin/gradle" ]; then
    GRADLE_CMD="$(brew --prefix gradle)/bin/gradle"
elif [ -n "$ZSH_VERSION" ] && type whence &>/dev/null && whence -p gradle &>/dev/null; then
    GRADLE_CMD="$(whence -p gradle)"
elif type command &>/dev/null && command -v gradle &>/dev/null; then
    GRADLE_CMD="$(command -v gradle)"
fi

if [ -z "$GRADLE_CMD" ]; then
    echo "Could not find gradle executable."
    exit 1
fi

#
# Run gradle
#
exec "$GRADLE_CMD" "$@"
''