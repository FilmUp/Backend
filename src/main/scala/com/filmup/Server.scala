package com.filmup


import javax.inject.Inject

import com.twitter.finagle.{Filter, SimpleFilter, Service}
import com.twitter.finagle.http.{Response, Request}
import com.twitter.finatra.http.{HttpHeaders, HttpServer}
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.logging.modules.Slf4jBridgeModule
import com.twitter.util.Future

import javax.inject.{Inject, Singleton}

@Singleton
class CorsFilter extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request).map {
      response =>
        response
          .headerMap
          .add("Access-Control-Allow-Origin", "*")
          .add("Access-Control-Allow-Headers", "accept, content-type")
          .add("Access-Control-Allow-Methods", "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH")
        response
    }
  }
}


//TODO generate flag for website signature.
class UserManagementServer extends HttpServer{
  //optional arguments
  val port = flag("port", 3000, "Port to run the server on")
  val mongoPort = flag("mongoPort", 27017, "Port Mongo is running on")
  val mongoHost = flag("mongoHost", "localhost", "Host Mongo is running on")
  val mongoDB = flag("mongoDB", "filmup_test", "RemoteDB name")
  //required arguments
  val sendGridApi = flag[String]("sendGridApi","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", "Api needed to send emails")
  val websiteHost = flag[String]("websiteHost","http://localhost:63342", "What is the public host of the website")

  override val name = "UserManagementServer"
  override def modules = Seq(
    Slf4jBridgeModule)

  override def configureHttp(router: HttpRouter) {

    import com.foursquare.rogue.LiftRogue._
    //Define constants to be used globally
    Config.setSetting("mongoHost", mongoHost())
    Config.setSetting("mongoPort", mongoPort())
    Config.setSetting("mongoDB", mongoDB())
    Config.setSetting("websiteHost", websiteHost())
    Config.setSetting("sendGridApi", sendGridApi())

    //Connect to the mongo server.
    RogueMongo.connectToMongo

    //Insert test record and delete to test connection to the database.
    //Connection will fail other wise.
    println(User.createRecord.email("test@test.test").save(true))
    println(User.where(_.email eqs "test@test.test").findAndDeleteOne())

    //Server stuff goes here
      router
        .filter[CommonFilters]
        .filter[CorsFilter]
        .add[AuthenticationController]

    //Clear connection to the database
    //RogueMongo.disconnectFromMongo
  }
}
//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
object Server extends UserManagementServer
