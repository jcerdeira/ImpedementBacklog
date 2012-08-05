package org.realsoftwarematters.jira

case class ImpedementRow(id:String, status:String, description:String, owner:String)

case class Project(name:String)

case class ListImpedements(project:Project, impedements:List[ImpedementRow])

case class Configs(jiraProject:String,jiraComponent:String)

