
organization := "org.realsoftwarematters.jira"

// Set the project name to the string 'My Project'
name := "impedementbacklog"

// The := method used in Name and Version is one of two fundamental methods.
// The other method is <<=
// All other initialization methods are implemented in terms of these.
version := "1.0-SNAPSHOT"

scalaVersion := "2.9.2" 

sbtPlugin := true

resolvers += "JIRA Repo" at "https://maven.atlassian.com/content/groups/public/"

// Add a single dependency
libraryDependencies ++= Seq(
	"com.atlassian.jira.plugins" % "jira-soapclient" % "4.4",
	"junit" % "junit" % "4.10" % "test",
	"com.novocode" % "junit-interface" % "0.8" % "test",
	"org.specs2" % "specs2_2.9.2" % "1.11",
	"org.apache.geronimo.configs" % "javamail" % "3.0.0",
	"com.typesafe" % "config" % "0.5.0"
)

