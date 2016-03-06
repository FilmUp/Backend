name := "UserGraphService"

version := "1.0"

scalaVersion := "2.11.7"
parallelExecution in ThisBuild := false
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)
mainClass in assembly := Some("com.filmup.Server")

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.discard
    }
  case other => MergeStrategy.first
}


//foursquare - rogue
val rogueField      = "com.foursquare" %% "rogue-field"         % "2.5.0" intransitive()
val rogueCore       = "com.foursquare" %% "rogue-core"          % "2.5.1" intransitive()
val rogueLift       = "com.foursquare" %% "rogue-lift"          % "2.5.1" intransitive()
val rogueIndex      = "com.foursquare" %% "rogue-index"         % "2.5.1" intransitive()
val liftMongoRecord = "net.liftweb"    %% "lift-mongodb-record" % "2.6"

//twitter
val finatraHttp = "com.twitter.finatra" % "finatra-http_2.11" % "2.1.2"
val finatraCore = "com.twitter.finatra" % "finatra-root_2.11" % "2.1.2"
val finatraHttpClient = "com.twitter.finatra" % "finatra-httpclient_2.11" % "2.1.2"
val finatraJackson = "com.twitter.finatra" % "finatra-jackson_2.11" % "2.1.2"
val finatraUtil = "com.twitter.finatra" % "finatra-utils_2.11" % "2.1.2"
val finatraSl4j = "com.twitter.finatra" % "finatra-slf4j_2.11" % "2.1.2"

//sendgrid
val sendGrid = "com.sendgrid" % "sendgrid-java" % "2.2.1"

//logback
val logBack = "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies ++= Seq(
  rogueField,
  rogueCore,
  rogueLift,
  rogueIndex,
  liftMongoRecord,
  sendGrid,
  logBack,
  finatraCore,
  finatraHttp,
  finatraHttpClient,
  finatraUtil,
  finatraJackson,
  finatraSl4j)
