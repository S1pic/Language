@echo off

::
:: Copyright 2015 the original author or authors.
::
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
::
::      https://www.apache.org/licenses/LICENSE-2.0
::
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.
::

::
:: @author: Andres Almiray
::

::
:: This script is a simple wrapper around the gradle executable.
::
:: It looks for the `gradle` executable in the following locations:
::
::   1. `%%GRADLE_HOME%%\bin`
::   2. `%%USERPROFILE%%\.sdkman\candidates\gradle\current\bin`
::   3. `%%USERPROFILE%%\.gvm\gradle\current\bin`
::   4. `%%USERPROFILE%%\.jenv\versions\gradle\bin`
::
:: You can just as easily invoke gradle directly.
::
:: You can also configure the following environment variables:
::
::   * `GRADLE_HOME` - location of a Gradle installation.
::   * `GRADLE_OPTS` - additional JVM options.
::   * `JAVA_HOME`   - location of a JDK installation.
::   * `JAVA_OPTS`   - additional JVM options.
::

@if "%%DEBUG%%" == "" @echo off

:: Stop on error
setlocal

::
:: Resolve the location of the gradle executable
::
if defined GRADLE_HOME ( if exist "%%GRADLE_HOME%%\bin\gradle.bat" ( set "GRADLE_CMD=%%GRADLE_HOME%%\bin\gradle.bat" ) )
if not defined GRADLE_CMD if exist "%%USERPROFILE%%\.sdkman\candidates\gradle\current\bin\gradle.bat" ( set "GRADLE_CMD=%%USERPROFILE%%\.sdkman\candidates\gradle\current\bin\gradle.bat" )
if not defined GRADLE_CMD if exist "%%USERPROFILE%%\.gvm\gradle\current\bin\gradle.bat" ( set "GRADLE_CMD=%%USERPROFILE%%\.gvm\gradle\current\bin\gradle.bat" )
if not defined GRADLE_CMD if exist "%%USERPROFILE%%\.jenv\versions\gradle\bin\gradle.bat" ( set "GRADLE_CMD=%%USERPROFILE%%\.jenv\versions\gradle\bin\gradle.bat" )
if not defined GRADLE_CMD ( for /f "delims=" %%%%a in ('where gradle.bat 2^>nul') do @set "GRADLE_CMD=%%%%a" & goto :found_gradle )

:found_gradle
if not defined GRADLE_CMD (
    echo Could not find gradle.bat executable.
    exit /b 1
)

::
:: Run gradle
::
"%%GRADLE_CMD%%" %%*
