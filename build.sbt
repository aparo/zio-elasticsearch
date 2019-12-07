import sbtcrossproject.{CrossType, crossProject}
import ReleaseTransformations._

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

val scalaTestVersion = "3.2.0-SNAP10"
val scalaTestPlusVersion = "3.1.0.0-RC2"

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _ => false
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
        compilerPlugin(
          ("org.scalamacros" % "paradise" % paradiseVersion)
            .cross(CrossVersion.patch))
      )
    } else Nil
  ),
  startYear := Some(2019),
  licenses += ("Apache-2.0", new URL(
    "https://www.apache.org/licenses/LICENSE-2.0.txt")),
  headerLicense := Some(
    HeaderLicense.ALv2("2019", "Alberto Paro", HeaderLicenseStyle.SpdxSyntax))
)

val allSettings = baseSettings ++ publishSettings

val docMappingsApiDir =
  settingKey[String]("Subdirectory in site target directory for API docs")

lazy val root =
  project
    .in(file("."))
    .settings(allSettings)
    .settings(noPublishSettings)
    .aggregate(`elasticsearch-core`,`elasticsearch-admin`, `elasticsearch-cat`,  `elasticsearch-client-sttp`)

lazy val http4sVersion = "0.21.0-M5"
lazy val elasticsearchClusterRunnerVersion = "7.4.2.0"
lazy val testContainerScalaVersion = "0.33.0"

lazy val `elasticsearch-core` = project
  .in(file("elasticsearch-core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(allSettings)
  .settings(
    moduleName := "zio-elasticsearch-core",
    libraryDependencies ++= Seq(
      "io.7mind.izumi" %% "logstage-core" % "0.9.16",
      "io.circe" %% "circe-derivation-annotations" % "0.12.0-M7",
      "io.circe" %% "circe-parser" % "0.12.3",
      "com.beachape" %% "enumeratum-circe" % "1.5.22",
      "dev.zio" %% "zio-streams" % "1.0.0-RC16",
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.scalatestplus" %% "scalatestplus-scalacheck" % scalaTestPlusVersion % Test,
      "org.codelibs" % "elasticsearch-cluster-runner" % elasticsearchClusterRunnerVersion % Test,
      "com.dimafeng" %% "testcontainers-scala" % testContainerScalaVersion % Test
    )
  )

lazy val `elasticsearch-admin` = project
  .in(file("elasticsearch-admin"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(allSettings)
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-cat` = project
  .in(file("elasticsearch-cat"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(allSettings)
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-client-sttp` = project
  .in(file("elasticsearch-client-sttp"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(allSettings)
  .settings(
    moduleName := "zio-elasticsearch-sttp",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp" %% "async-http-client-backend-zio" % "1.7.2",
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.scalatestplus" %% "scalatestplus-scalacheck" % scalaTestPlusVersion % Test,
      "org.codelibs" % "elasticsearch-cluster-runner" % elasticsearchClusterRunnerVersion % Test,
      "com.dimafeng" %% "testcontainers-scala" % testContainerScalaVersion % Test
    )
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile",
    `elasticsearch-admin` % "test->test;compile->compile",
    `elasticsearch-cat` % "test->test;compile->compile"
  )

lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/aparo/zio-elasticsearch")),
  licenses := Seq(
    "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
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
  apiURL := Some(
    url("https://zio-elasticsearch.github.io/zio-elasticsearch/api/")),
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

// Releasing
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
