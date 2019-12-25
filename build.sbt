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

val paradiseVersion = "2.1.1"

val scalaTestVersion = "3.2.0-SNAP10"
val scalaTestPlusVersion = "3.1.0.0-RC2"

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
