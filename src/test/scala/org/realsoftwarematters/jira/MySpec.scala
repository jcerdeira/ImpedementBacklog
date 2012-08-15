package org.realsoftwarematters.jira

import org.specs2.mutable._
import java.text.DecimalFormat;
import java.net.URL;
import com.typesafe.config._

import com.atlassian.jira.rpc.soap.client._


  class HelloWorldSpec extends Specification {

    "The 'Hello world' string" should {
      "contain 11 characters" in {

        
        
        val conf = ConfigFactory.load()

        val baseUrl = conf.getString("jira.url")
        implicit val soapSession = SOAPSession(new URL(baseUrl));

        soapSession.connect(conf.getString("jira.authentication.user"),conf.getString("jira.authentication.password"))

        println("JIRA Token:" + soapSession.authenticationToken)

        val jiraOps = new JiraOperations()
        
        val jql = "project = EIDCV AND component = \"Pending Internal\" ORDER BY due ASC, priority DESC, created ASC"
        jiraOps.getIssues(jql) foreach (it => println(it.serialize))

        "Hello world" must have size(11)
      }

      "start with 'Hello'" in {
        "Hello world" must startWith("Hello")
      }
      "end with 'world'" in {
        "Hello world" must endWith("world")
      }
    }
  }


import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceServiceLocator;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

class SOAPSession(jiraSoapServiceLocator:JiraSoapServiceService, 
                  jiraSoapService:JiraSoapService) {
  

    var authenticationToken:String=null

    def connect(userName:String, password:String) = {
        println("\tConnnecting via SOAP as : " + userName)
        authenticationToken = getJiraSoapService().login(userName, password)
        println("\tConnected")
    }

    def  getJiraSoapService():JiraSoapService= {
        return jiraSoapService;
    }

    def getJiraSoapServiceLocator() : JiraSoapServiceService = {
        return jiraSoapServiceLocator;
    }
    
}

class JiraOperations(implicit soapSession:SOAPSession){
  
  val statuses = JiraClientUtil.obtainStatus
  val resolutions = JiraClientUtil.obtainResolutions
  
  implicit def remoteIssueEnhanced(ri : RemoteIssue) = new EnhancedRemoteIssue(ri,statuses,resolutions)
  
  def getIssues(jql:String): Seq[EnhancedRemoteIssue] = {
      soapSession.getJiraSoapService.getIssuesFromJqlSearch(soapSession.authenticationToken,jql,50) map {it => remoteIssueEnhanced(it)}
    }
  
}


object SOAPSession{
  def apply(webServicePort:URL):SOAPSession ={
        val jiraSoapServiceLocator = new JiraSoapServiceServiceLocator()
        if (webServicePort == null) {
          new SOAPSession(jiraSoapServiceLocator,jiraSoapServiceLocator.getJirasoapserviceV2())
        }else{
          val jiraSoapService = jiraSoapServiceLocator.getJirasoapserviceV2(webServicePort)
          println("SOAP Session service endpoint at " + webServicePort.toExternalForm())
          new SOAPSession(jiraSoapServiceLocator,jiraSoapService)
        }   
  }
}

class EnhancedRemoteIssue(ri:RemoteIssue, statuses:Map[String,String], resolutions:Map[String,String])  {
  
    implicit def remoteComponentEnhanced(rc : RemoteComponent) = new EnhancedRemoteComponent(rc)

    def serialize()(implicit soapSession:SOAPSession):String = {
      
      def formatDate(date:java.util.Calendar): String = {
        val sdf = new java.text.SimpleDateFormat("yyy-MM-dd HH:mm")
        sdf.format(date.getTime)
      }

      "Issue : {" + 
        "\n\tKey : " + ri.getKey + 
        "\n\tSumary : " + ri.getSummary + 
        "\n\tRetorter : " + ri.getReporter + 
        "\n\tAssignee : " + ri.getAssignee +
        "\n\tCreated : " + formatDate(ri.getCreated) +
        "\n\tUpdated : " + formatDate(ri.getUpdated) +
        "\n\tStatus : " + statuses.get(ri.getStatus).getOrElse("None") +
        "\n\tResolution : " + resolutions.get(ri.getResolution).getOrElse("None") +
        //"\n\tDescription : " + ri.getDescription +
        "\n\tComponents: ["+ ri.getComponents.map(EnhancedRemoteComponent.serialize(_)).mkString(",") + "]" + 
        "\n}"
    }
    
}
object EnhancedRemoteIssue{
    
  def print(eri:EnhancedRemoteIssue)(implicit soapSession:SOAPSession) ={
    println(eri.serialize)
  }
}

class EnhancedRemoteComponent(rc:RemoteComponent){
  override def toString(): String = {
      "Component : { Name: " + rc.getName + "}"
    }
}
object EnhancedRemoteComponent{
  def serialize(erc: EnhancedRemoteComponent): String = {
    erc.toString
  }
}

object JiraClientUtil{
  
  def obtainResolutions(implicit soapSession:SOAPSession): Map[String,String] = {
    soapSession.getJiraSoapService.getResolutions(soapSession.authenticationToken).map(it => (it.getId->it.getName)).toMap;
  }
  
  def obtainStatus(implicit soapSession:SOAPSession): Map[String,String] = {
    soapSession.getJiraSoapService.getStatuses(soapSession.authenticationToken).map(it => (it.getId->it.getName)).toMap;
  }
  
}