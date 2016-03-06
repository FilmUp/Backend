package com.filmup

import com.foursquare.index.{Asc, Desc, IndexedRecord, IndexModifier, TwoD}
import com.foursquare.rogue.LiftRogue._
import com.foursquare.rogue.ObjectIdKey
import com.mongodb.{Mongo, ServerAddress}
import net.liftweb.mongodb.{MongoDB, MongoIdentifier}
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.record._
import org.bson.types.ObjectId


/**
  * Handles the connection to a mongoInstance.
  * The underlying mongo instance details can be abstracted to a
  */
object RogueMongo extends MongoIdentifier {

  override def jndiName = "rogue_mongo"

  private var mongo: Option[Mongo] = None

  def connectToMongo = {
    val MongoPort = Option(Config.getSetting("mongoPort").getOrElse(37648).asInstanceOf[Int])

    mongo = Some(new Mongo(new ServerAddress(Config.getSetting("mongoHost").getOrElse("localhost").asInstanceOf[String], MongoPort.get)))
    MongoDB.defineDb(RogueMongo, mongo.get, Config.getSetting("mongoDB").getOrElse("test").asInstanceOf[String])
  }

  def disconnectFromMongo = {
    mongo.foreach(_.close)
    MongoDB.close
    mongo = None
  }
}

object UserType extends Enumeration {
  val student = Value("Student")
  val business = Value("Business")
}

class User extends MongoRecord[User] with ObjectIdKey[User] with IndexedRecord[User] {
  def meta = User
  object fName extends StringField(this, 100)
  object lName extends StringField(this, 100)
  object university extends StringField(this, 100)
  object password extends StringField(this, 50)
  object major extends StringField(this, 100)
  object location extends StringField(this, 100)
  object email extends EmailField(this, 50)
  object token extends StringField(this, 50)
  object last_updated extends DateField(this)
  object userType extends EnumNameField(this, UserType) { override def name = "type" }
}

object User extends User with MongoMetaRecord[User] {
  override def collectionName = "users"
  override def mongoIdentifier = RogueMongo
}

class UserPending extends MongoRecord[UserPending] with ObjectIdKey[UserPending] with IndexedRecord[User] {
  def meta = UserPending
  object fName extends StringField(this, 100)
  object lName extends StringField(this, 100)
  object university extends StringField(this, 100)
  object major extends StringField(this, 100)
  object password extends StringField(this, 5)
  object location extends StringField(this, 100)
  object email extends EmailField(this, 50)
  object last_updated extends DateField(this)
  object code extends StringField(this, 200)
  object userType extends EnumNameField(this, UserType) { override def name = "type" }
}

object UserPending extends UserPending with MongoMetaRecord[UserPending] {
  override def collectionName = "pendingUsers"
  override def mongoIdentifier = RogueMongo
}