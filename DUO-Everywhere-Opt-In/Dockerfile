FROM tomcat:9-jdk11
LABEL Email=haibei.zhang@yale.edu
 
#Deco Args
ARG DECO_VERSION=0.3.1
ARG DECO_OS=linux
ARG DECO_ARCH=amd64

# Version of the WAR Artifact to install.TYPE is "snapshots" or "releases"
ARG ARTIFACT_TYPE=snapshots
ARG ARTIFACT_VERSION=1.0.0-SNAPSHOT

 
#Add Deco to the Image (Must)
ADD https://github.com/YaleUniversity/deco/releases/download/v${DECO_VERSION}/deco-v${DECO_VERSION}-${DECO_OS}-${DECO_ARCH} /usr/local/bin/deco
RUN chmod 555 /usr/local/bin/deco && deco version

# Download JDBC driver
#ADD https://repository.its.yale.edu/maven2/libs-releases/net/sourceforge/jtds/jtds/1.3.1/jtds-1.3.1.jar /usr/local/tomcat/lib
# WAR Artifact
ADD https://repository.its.yale.edu/maven2/libs-${ARTIFACT_TYPE}-local/edu/yale/its/iam/MFAOptIn/${ARTIFACT_VERSION}/MFAOptIn-${ARTIFACT_VERSION}.war /usr/local/tomcat/webapps
RUN mv /usr/local/tomcat/webapps/MFAOptIn-${ARTIFACT_VERSION}.war /usr/local/tomcat/webapps/mfa.war

RUN mkdir /usr/local/tomcat/webapps/mfa
RUN unzip -d /usr/local/tomcat/webapps/mfa /usr/local/tomcat/webapps/mfa.war
RUN rm /usr/local/tomcat/webapps/mfa.war

#For local docker only:
#COPY deco-DEV.json /run/secrets/deco.json
COPY entrypoint.sh /
RUN chmod 700 /entrypoint.sh
 
EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]