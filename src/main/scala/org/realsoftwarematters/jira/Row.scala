package org.realsoftwarematters.jira.domain

case class ImpedementRow(jiraID:String, description:String, owner:String, priority:String,
	status:String, resolution:String, created:String, updated:String, dueDate:String, extraInfo:String)

case class Project(name:String)

case class ListImpedements(project:Project, impedements:Seq[ImpedementRow])

