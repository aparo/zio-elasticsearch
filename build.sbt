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

//val compilerOptions = Seq(
//  "-deprecation",
//  "-encoding",
//  "UTF-8",
//  "-feature",
//  "-language:existentials",
//  "-language:higherKinds",
//  "-language:implicitConversions",
//  "-unchecked",
//  "-Ywarn-dead-code",
//  "-Ywarn-numeric-widen"
//)

val paradiseVersion = "2.1.1"

val scalaTestVersion = "3.2.0-SNAP10"
val scalaTestPlusVersion = "3.1.0.0-RC2"

//val baseSettings = Seq(
//  scalaVersion := "2.12.10",
//  scalacOptions ++= compilerOptions,
//  scalacOptions ++= (
//    if (priorTo2_13(scalaVersion.value))
//      Seq(
//        "-Xfuture",
//        "-Yno-adapted-args",
//        "-Ywarn-unused-import",
//        "-Ypartial-unification"
//      )
//    else
//      Seq(
//        "-Ymacro-annotations",
//        "-Ywarn-unused:imports"
//      )
//  ),
//  scalacOptions in (Compile, console) ~= {
//    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
//  },
//  scalacOptions in (Test, console) ~= {
//    _.filterNot(Set("-Ywarn-unused-import", "-Ywarn-unused:imports"))
//  },
//  coverageHighlighting := true,
//  (scalastyleSources in Compile) ++= (unmanagedSourceDirectories in Compile).value,
//  libraryDependencies ++= Seq(
//    scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
//    scalaOrganization.value % "scala-reflect" % scalaVersion.value % Provided
//  ) ++ (
//    if (priorTo2_13(scalaVersion.value)) {
//      Seq(
//        compilerPlugin(
//          ("org.scalamacros" % "paradise" % paradiseVersion)
//            .cross(CrossVersion.patch))
//      )
//    } else Nil
//  ),
//  startYear := Some(2019),
//  licenses += ("Apache-2.0", new URL(
//    "https://www.apache.org/licenses/LICENSE-2.0.txt")),
//  headerLicense := Some(
//    HeaderLicense.ALv2("2019", "Alberto Paro", HeaderLicenseStyle.SpdxSyntax))
//)

//val allSettings = baseSettings ++ publishSettings

lazy val root =
  project
    .in(file("."))
    .settings(Common.noPublishSettings)
    .aggregate(
      `elasticsearch-core-jvm`,
      `elasticsearch-core-js`,
      `elasticsearch-admin-jvm`,
      `elasticsearch-admin-js`,
      `elasticsearch-cat-jvm`,
      `elasticsearch-cat-js`,
      `elasticsearch-client-sttp`
    )

lazy val `elasticsearch-core` = ProjectUtils
  .setupCrossModule("elasticsearch-core")
  .settings(
    moduleName := "zio-elasticsearch-core"
  )
  .settings(Dependencies.elasticsearchCore)

lazy val `elasticsearch-core-jvm` = `elasticsearch-core`.jvm
lazy val `elasticsearch-core-js` = `elasticsearch-core`.js

lazy val `elasticsearch-admin` = ProjectUtils
  .setupCrossModule("elasticsearch-admin")
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-admin-jvm` = `elasticsearch-admin`.jvm
lazy val `elasticsearch-admin-js` = `elasticsearch-admin`.js

lazy val `elasticsearch-cat` = ProjectUtils
  .setupCrossModule("elasticsearch-cat", CrossType.Pure)
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-cat-jvm` = `elasticsearch-cat`.jvm
lazy val `elasticsearch-cat-js` = `elasticsearch-cat`.js

lazy val `elasticsearch-client-sttp` = ProjectUtils
  .setupJVMProject("elasticsearch-client-sttp")
  .settings(
    moduleName := "zio-elasticsearch-client-sttp"
  )
  .settings(Dependencies.clientSTTP)
  .dependsOn(
    `elasticsearch-core-jvm` % "test->test;compile->compile",
    `elasticsearch-admin-jvm` % "test->test;compile->compile",
    `elasticsearch-cat-jvm` % "test->test;compile->compile"
  )

//lazy val publishSettings = Seq(
//  releaseCrossBuild := true,
//  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
//  homepage := Some(url("https://github.com/aparo/zio-elasticsearch")),
//  licenses := Seq(
//    "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
//  publishMavenStyle := true,
//  publishArtifact in Test := false,
//  pomIncludeRepository := { _ =>
//    false
//  },
//  publishTo := {
//    val nexus = "https://oss.sonatype.org/"
//    if (isSnapshot.value)
//      Some("snapshots".at(nexus + "content/repositories/snapshots"))
//    else
//      Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
//  },
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

// Releasing
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
//  runClean,
//  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publish"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
