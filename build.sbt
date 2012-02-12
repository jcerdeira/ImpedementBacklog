
organization := "org.realsoftwarematters.jira"

// Set the project name to the string 'My Project'
name := "impedementbacklog"

// The := method used in Name and Version is one of two fundamental methods.
// The other method is <<=
// All other initialization methods are implemented in terms of these.
version := "1.0-SNAPSHOT"

scalaVersion := "2.9.0" 

sbtPlugin := true

// Add a single dependency
libraryDependencies ++= Seq(
	"net.sourceforge.jexcelapi" % "jxl" % "2.6.12"
)

