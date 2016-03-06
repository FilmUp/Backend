package com.filmup

import javax.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Cookie, Request}
import com.twitter.finatra.http.{HttpHeaders, Controller}
/**
  * Created by lwelch on 1/31/16.
  */


@Singleton
class AuthenticationController @Inject()(
  authService: UserAuthentication)
  extends Controller {

  def ALL_READY_REDISTERED = "That Email is Already Registered"
  def INCORRECT_CREDENTIALS = "Incorrect Email/Password"
  def VERIFY_ERROR = "Error While Attempting to Verify Email"
  def AUTH_ERROR = "Not authorized"
  def version = "V1"
  def path = "auth"
  def prefix = "/api/V1/auth"

  /**
    * check to see if email has been used in other login
    * pass param map to @signupPendingUser and return ok
    *
    */
  post(prefix + "/register") { request: Request =>
    val params = request.params
    if (authService.fromEmail(params.get("email").get).isDefined) {
      response.unauthorized(ALL_READY_REDISTERED)
    }
    response.ok(authService.signupPendingUser(params))
  }

  options(prefix + "/register") { request: Request =>
    response.ok
  }

  /**
    * check to see if email is tied to a user, if not return error
    * check to see if the pass for the returned user is valid, if not return error
    * set auth cookie
    * return user object
    */
  get(prefix + "/login") { request: Request =>
    val params = request.params
    val email = params.get("email")
    val password = params.get("password")
    val res = authService.login(email.getOrElse(""), password.getOrElse(""))
      response.ok.cookie("jwt", res.getOrElse(""))
  }
  options(prefix + "/login") { request: Request =>
    response.ok
  }

  /**
    * check to see if token exists
    * move usr from pending to User table
    * return ok
    */
  post(prefix + "/verify") { request: Request =>
    val id = request.params.get("code")
    println("verifying " + id.getOrElse(""))
    val check = authService.verifyUser(id.getOrElse(""))
    response.ok
  }
  options(prefix + "/verify") { request: Request =>
    response.ok
  }

  /**
    * check to see if token exists
    * move usr from pending to User table
    * return ok
    */
  get(prefix + "/me") { request: Request =>
    val token = request.cookies.get("jwt").map(_.value)
    if (token.isEmpty)
      response.unauthorized(AUTH_ERROR)
    response.ok(authService.fromAuth(token.getOrElse("")))
  }
}
