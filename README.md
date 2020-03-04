# Quality - QUantum hArdware seLectIon uTilitY

[![Build Status](https://api.travis-ci.com/wederbn/quality.svg?branch=master)](https://travis-ci.com/wederbn/quality)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Build

1. Run `mvn package -DskipTests` inside the root folder.
2. When completed, the built product can be found in `org.planqk.quality.war/target`.

## Running via Docker

The easiest way to get started is using Docker:

1. `docker build -t quality .`.
   In case, there are issues, you can also try `docker build --no-cache -t quality .`
2. `docker run -p 8080:8080 quality` to run Quality on <http://localhost:8080>

You can also use the pre-built image:

    docker run -p 8080:8080 planqk/quality
	
## Running on Tomcat

Build the project and deploy the WAR file located at `org.planqk.quality.war/target` to Tomcat.

Prerequisites:

- [SWI Prolog](https://www.swi-prolog.org/) is installed on the machine where the Tomcat runs and the Path is configured correspondingly
- [Qiskit](https://qiskit.org/)is installed locally and accessible


## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden, ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.
