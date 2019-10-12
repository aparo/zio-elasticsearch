import sbtcrossproject.{ CrossType, crossProject }

inThisBuild(
  Seq(
    organization := "io.megl",
    scalaVersion := "2.12.10",
    parallelExecution := false,
    scalafmtOnCompile := true,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in packageDoc := false,
    sources in (Compile, doc) := Seq.empty
  )
)

val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen"
)

val paradiseVersion = "2.1.1"

val scalaTestVersion = "3.1.0-SNAP13"
val scalaTestPlusVersion = "3.1.0.0-RC2"

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }

val baseSettings = Seq(
  scalaVersion := "2.12.10",
  scalacOptions ++= compilerOptions,
  scalacOptions ++= (
    if (priorTo2_13(scalaVersion.value))
      Seq(
        "-Xfuture",
        "-Yno-adapted-args",
        "-Ywarn-unused-import",
        "-Ypartial-unification"
      )
    else
      Seq(
        "-Ymacro-annotations",
        "-Ywarn-unused:imports"
      )
  ),
  scalacOptions in (Compile, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
  },
  scalacOptions in (Test, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
  },
  coverageHighlighting := true,
  (scalastyleSources in Compile) ++= (unmanagedSourceDirectories in Compile).value,
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
    scalaOrganization.value % "scala-reflect" % scalaVersion.value % Provided
  ) ++ (
    if (priorTo2_13(scalaVersion.value)) {
      Seq(
        compilerPlugin(("org.scalamacros" % "paradise" % paradiseVersion).cross(CrossVersion.patch))
      )
    } else Nil
  ),
  startYear := Some(2019),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  headerLicense := Some(HeaderLicense.ALv2("2019", "Alberto Paro", HeaderLicenseStyle.SpdxSyntax))
)

val allSettings = baseSettings ++ publishSettings

val docMappingsApiDir = settingKey[String]("Subdirectory in site target directory for API docs")

lazy val root =
  project.in(file(".")).settings(allSettings).settings(noPublishSettings).aggregate(elasticsearch)

lazy val http4sVersion = "0.21.0-M5"

lazy val elasticsearch = project
  .in(file("elasticsearch"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(allSettings)
  .settings(
    moduleName := "zio-elasticsearch",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp" %% "async-http-client-backend-zio" % "1.6.7",
      "io.circe" %% "circe-derivation-annotations" % "0.12.0-M7",
      "io.circe" %% "circe-parser" % "0.12.1",
      "com.beachape" %% "enumeratum-circe" % "1.5.21",
      "dev.zio" %% "zio" % "1.0.0-RC12-1",
      "dev.zio" %% "zio-streams" % "1.0.0-RC12-1",
      "com.github.mlangc" %% "slf4zio" % "0.2.1",
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.scalatestplus" %% "scalatestplus-scalacheck" % scalaTestPlusVersion % Test,
      "org.codelibs" % "elasticsearch-cluster-runner" % "7.4.0.0" % Test,
      "com.dimafeng" %% "testcontainers-scala" % "0.33.0" % Test
    ),
    ghpagesNoJekyll := true,
    docMappingsApiDir := "api",
    addMappingsToSiteDir(mappings in (Compile, packageDoc), docMappingsApiDir)
  )

lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/aparo/zio-elasticsearch")),
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots".at(nexus + "content/repositories/snapshots"))
    else
      Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
  },
  autoAPIMappings := true,
  apiURL := Some(url("https://zio-elasticsearch.github.io/zio-elasticsearch/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/aparo/zio-elasticsearch"),
      "scm:git:git@github.com:aparo/zio-elasticsearch.git"
    )
  ),
  developers := List(
    Developer(
      "aparo",
      "Alberto Paro",
      "alberto.paro@gmail.com",
      url("https://twitter.com/aparo77")
    )
  )
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

credentials ++= (
  for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield
    Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      username,
      password
    )
).toSeq