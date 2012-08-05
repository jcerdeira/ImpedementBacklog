package org.realsoftwarematters.jira

import org.specs2.mutable._
import java.text.DecimalFormat;
import java.net.URL;
import com.typesafe.config._

  class HelloWorldSpec extends Specification {

    "The 'Hello world' string" should {
      "contain 11 characters" in {

        val conf = ConfigFactory.load()

        val baseUrl = conf.getString("jira.url")

        val soapSession = SOAPSession(new URL(baseUrl));

        val timing = Timing.startTiming("JIRA SOAP client sample");

        soapSession.connect(conf.getString("jira.authentication.user"),conf.getString("jira.authentication.password"))

        val jiraSoapService = soapSession.getJiraSoapService();
        println("JIRA Token:" + soapSession.authenticationToken)

        //val issue = jiraSoapService.getIssueById(soapSession.authenticationToken, "issueId");

        val jql = "project = EIDCV AND component = \"Pending Internal\" ORDER BY due ASC, priority DESC, created ASC"

        jiraSoapService.getIssuesFromJqlSearch(soapSession.authenticationToken,jql,50) foreach{ it => println(it.getSummary)}

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



   case class Timing(operationDesc:String,then:Long){

        def printTiming()
        {
            val howLong = System.currentTimeMillis() - this.then
            println("________________________________________________________________");
            val decFormat = new DecimalFormat("###,##0")
            println("\t" + this.operationDesc + " took " 
              + decFormat.format(howLong) + " ms to run")
        }

   }

   object Timing{

         def startTiming(operationDesc:String): Timing =
        {
            println("\nRunning : " + operationDesc)
            Timing(operationDesc,System.currentTimeMillis())
        }

    }

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceServiceLocator;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

/**
 * This represents a SOAP session with JIRA including that state of being logged in or not
 */
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

// Romete Issue Implicit :D

import com.atlassian.jira.rpc.soap.client.RemoteIssue


class DecorateRemoteIssue(ri:RemoteIssue)  {

    override def toString():String = {
      ri.getSummary
    }
}
