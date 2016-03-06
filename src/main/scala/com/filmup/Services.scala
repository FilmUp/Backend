package com.filmup

import javax.inject.Singleton
import com.foursquare.rogue.LiftRogue._
import com.twitter.finagle.http.ParamMap
import com.twitter.logging.Logger


/**
  * Handles user-user interaction(messaging, following), assumes the user nodes already exists.
  * In its current state I will use a pooling mechicnism to fetch refresh messages.
  * In the near future that will be replaced with a redis fan out for connected clients
  * for the apperance of realtime updates.
  */
@Singleton
class UserGraphService {

  def followUser = None
  def isFollowing = None
  def unfollowUser = None
  def followerCount = None
  def followingCount = None
  def messageUser = None
  def getThreads = None
  def getMessages = None

  /**
    * Given a thread id and a message post this message to the thread
    * @return
    */
  def sendMessage = None
}

@Singleton
class UserAuthentication {
  private val logger = Logger.get(getClass)

  private object Email {
    private val logger = Logger.get(getClass)
    import com.sendgrid.SendGrid
    import com.sendgrid.SendGrid.Email

    val sendgrid = new SendGrid(Config.getSetting("sendGridApi").getOrElse(None).asInstanceOf[String])
    //load template
    val template = Config.template
    val signature = Config.getSetting("emailSignature").getOrElse("The FilmUp Team").asInstanceOf[String]
    val redirectUrl = Config.getSetting("websiteHost").getOrElse("localhost").asInstanceOf[String]


    /**
      * In its current implementation this is hyper local to just sending email verifiication emails.
      * Introduce a simple templating system to make this a generic to different types of emails. Also storing the template
      * in memory after the first load(template singleton or factory?). How expensive would that be? )
      * @param user
      * @return
      */
    def send(user: UserPending) = {
      val filledTemplate = template.replace("<%= code %>", user.code.toString).replace("<%= websiteUrl %>", redirectUrl).replace("<%= signature %>", "The FilmUp Team")
      val email = new Email()
      email.addTo(user.email.toString())
      email.setFrom("laurence@film-up.com")
      email.setSubject("Email Confirmation")
      email.setHtml(filledTemplate)
      println("sent verification email to: " + user.email.toString)
      val response = sendgrid.send(email)
      println(response.getMessage)
    }
  }

  /**
    * Create Pending User model and send email with verification token
    * @return
    */
  def signupPendingUser(params: ParamMap) : UserPending = {
    val up = UserPending.createRecord
    params.foreach { case (key: String, value: String) =>
      key match {
        case "fName" => up.fName(value)
        case "lName" => up.lName(value)
        case "university" => up.university(value)
        case "major" => up.major(value)
        case "password" => up.password(value)
        case "location" => up.location(value)
        case "email" => up.email(value); up.userType(UserType.business)
        case "type" =>
          if (value.equalsIgnoreCase("student"))
            up.userType(UserType.student)
          else
            up.userType(UserType.business)
      }
    }
    up.code(Misc.randomString(20))
    Email.send(up)
    up.save(true)
  }

  /**
    * Check the verification token and return true if the token is valid
    * @return
    */
  def verifyUser(token: String): Option[User] = {
    val pending  = fromEmailToken(token)
    val user = for ( p <- pending ) yield createUser(p)
    for ( p <- pending ) yield p.delete_!
    user
  }

  /**
    * Given the fields from create pending generate a user row in the User table and delete.
    * @return
    */
  def createUser(pending: UserPending): User = {
    User.createRecord.fName(pending.fName.toString)
      .lName(pending.lName.toString)
      .university(pending.university.toString)
      .major(pending.major.toString)
      .password(pending.password.toString)
      .location(pending.location.toString)
      .email(pending.email.toString)
      .userType(pending.userType.get)
      .save(true)
  }

  def login(email: String, pass: String): Option[String] = {
    val user = fromEmail(email)
    for {
      u <- user
    if (u.password.toString() == pass)
    } yield u.token(Misc.randomString(20)).update.token.toString()
  }

  /**
    * Look up a usr by authId
    * @return User if exists, None else
    */
  def fromAuth(token: String): Option[User] = {
    User.where(_.token eqs token).get()
  }

  /**
    * Look up a user by id
    * @return User if exists, None else
    */
  def fromID(id: Long) = None

  /**
    * Look up a user by email code
    *  @return User if exists, None else
    */
  def fromEmailToken(code: String): Option[UserPending] = {
    UserPending.where(_.code eqs code).get()
  }

  /**
    * Look up a user by Email
    * @return User if exists, None else
    */
  def fromEmail(email: String): Option[User] = {
    User.where(_.email eqs email).get()
  }
}

/**
  * Any thing that would be found in the account settings page is handled here
  */
@Singleton
class UserManagementService {

  def updateBio = None
  def updateProPic = None
  def addExperience = None
  def listExperiences = None
  def getExperience = None

  /**
    * This is a blanket call to get all the data that populates the profile page
    * @return
    */
  def getProfile = None

}