import sbtcrossproject.{CrossType, crossProject}
import ReleaseTransformations._

inThisBuild(
  Seq(
    organization := "io.megl",
    scalaVersion := "2.12.10",
    parallelExecution := false,
    scalafmtOnCompile := true
  )
)

val paradiseVersion = "2.1.1"

val scalaTestVersion = "3.2.0-M2"
val scalaTestPlusVersion = "3.1.0.0-RC2"

lazy val root =
  project
    .in(file("."))
    .settings(Common.noPublishSettings)
    .aggregate(
      `zio-circe-jvm`,
      `zio-circe-js`,
      `zio-common-jvm`,
      `zio-common-js`,
      `zio-schema-jvm`,
      `zio-schema-js`,
      `elasticsearch-core-jvm`,
      `elasticsearch-core-js`,
      `elasticsearch-admin-jvm`,
      `elasticsearch-admin-js`,
      `elasticsearch-cat-jvm`,
      `elasticsearch-cat-js`,
      `elasticsearch-orm-jvm`,
      `elasticsearch-orm-js`,
      `elasticsearch-client-sttp`,
      `elasticsearch-client-http4s`
    )

lazy val `elasticsearch-core` = ProjectUtils
  .setupCrossModule("elasticsearch-core")
  .settings(
    moduleName := "zio-elasticsearch-core"
  )
  .settings(Dependencies.elasticsearchCore)
  .dependsOn(`zio-common`)

lazy val `elasticsearch-core-jvm` = `elasticsearch-core`.jvm
lazy val `elasticsearch-core-js` = `elasticsearch-core`.js

lazy val `elasticsearch-admin` = ProjectUtils
  .setupCrossModule("elasticsearch-admin")
  .settings(
    moduleName := "zio-elasticsearch-admin"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-admin-jvm` = `elasticsearch-admin`.jvm
lazy val `elasticsearch-admin-js` = `elasticsearch-admin`.js

lazy val `elasticsearch-cat` = ProjectUtils
  .setupCrossModule("elasticsearch-cat", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-cat"
  )
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

lazy val `zio-circe` = ProjectUtils
  .setupCrossModule("zio-circe", CrossType.Full)
  .settings(
    moduleName := "zio-circe"
  )
  .settings(Dependencies.zioCirce)
lazy val `zio-circe-jvm` = `zio-circe`.jvm
lazy val `zio-circe-js` = `zio-circe`.js


lazy val `zio-common` = ProjectUtils
  .setupCrossModule("zio-common", CrossType.Full)
  .settings(
    moduleName := "zio-common"
  )
  .settings(Dependencies.zioCommon)
  .settings(Common.zioTests)
  .dependsOn(`zio-circe`)

lazy val `zio-common-jvm` = `zio-common`.jvm
lazy val `zio-common-js` = `zio-common`.js

lazy val `zio-schema` = ProjectUtils
  .setupCrossModule("zio-schema", CrossType.Pure)
  .settings(
    moduleName := "zio-schema"
  )
  .settings(Dependencies.zioSchema)
  .dependsOn(`zio-common`)

lazy val `zio-schema-jvm` = `zio-schema`.jvm
lazy val `zio-schema-js` = `zio-schema`.js


  lazy val `elasticsearch-orm` = ProjectUtils
    .setupCrossModule("elasticsearch-orm", CrossType.Full)
    .settings(
      moduleName := "zio-elasticsearch-orm"
    )
    .dependsOn(`zio-schema`, `elasticsearch-admin` % "test->test;compile->compile")
  
  lazy val `elasticsearch-orm-jvm` = `elasticsearch-orm`.jvm
  lazy val `elasticsearch-orm-js` = `elasticsearch-orm`.js

lazy val `elasticsearch-client-http4s` = ProjectUtils
  .setupJVMProject("elasticsearch-client-http4s")
  .settings(
    moduleName := "zio-elasticsearch-client-http4s"
  )
  .settings(Dependencies.clientHttp4s)
  .settings(Common.zioTests)
  .dependsOn(
    `elasticsearch-orm-jvm` % "test->test;compile->compile",
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
