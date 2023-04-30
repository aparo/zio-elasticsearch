import sbtcrossproject.{ CrossType, crossProject }
import ReleaseTransformations._
import xerial.sbt.Sonatype._

inThisBuild(
  Seq(
    organization := "io.megl",
    parallelExecution := false,
    scalafmtOnCompile := false,
    Compile / packageDoc / publishArtifact := false,
    packageDoc / publishArtifact := false,
    Compile / doc / sources := Seq.empty,
    homepage := Some(url("https://github.com/aparo/zio-elasticsearch.git")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "aparo",
        "Alberto Paro",
        "albeto.paro@gmail.com",
        url("https://github.com/aparo")
      )
    ),
    versionScheme := Some("early-semver"),
    sonatypeProfileName := "io.megl",
    publishMavenStyle := true,
    licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    sonatypeProjectHosting := Some(GitHubHosting("aparo", "zio-elasticsearch", "alberto.paro@gmail.com")),
  )
)

val disableDocs = Seq[Setting[_]](
  Compile / doc / sources                := Seq.empty,
  Compile / packageDoc / publishArtifact := false
)

val disablePublishing = Seq[Setting[_]](
  publishArtifact := false,
  publish / skip  := true
)

val paradiseVersion = "2.1.1"

val scalaTestVersion = "3.2.0-M2"
val scalaTestPlusVersion = "3.1.0.0-RC2"

lazy val root =
  project
    .in(file("."))
    .settings((publish / skip) := true)
    .aggregate(
      `zio-common-jvm`,
      `zio-common-js`,
      `zio-schema-elasticsearch-jvm`,
      `zio-schema-elasticsearch-js`,
      `elasticsearch-core-jvm`,
      `elasticsearch-core-js`,
      `elasticsearch-async-search-jvm`,
      `elasticsearch-async-search-js`,
      `elasticsearch-autoscaling-jvm`,
      `elasticsearch-autoscaling-js`,
      `elasticsearch-cluster-jvm`,
      `elasticsearch-cluster-js`,
      `elasticsearch-ccr-jvm`,
      `elasticsearch-ccr-js`,
      `elasticsearch-dangling-indices-jvm`,
      `elasticsearch-dangling-indices-js`,
      `elasticsearch-enrich-jvm`,
      `elasticsearch-enrich-js`,
      `elasticsearch-eql-jvm`,
      `elasticsearch-eql-js`,
      `elasticsearch-features-jvm`,
      `elasticsearch-features-js`,
      `elasticsearch-fleet-jvm`,
      `elasticsearch-fleet-js`,
      `elasticsearch-graph-jvm`,
      `elasticsearch-graph-js`,
      `elasticsearch-indices-jvm`,
      `elasticsearch-indices-js`,
      `elasticsearch-ilm-jvm`,
      `elasticsearch-ilm-js`,
      `elasticsearch-ingest-jvm`,
      `elasticsearch-ingest-js`,
      `elasticsearch-license-jvm`,
      `elasticsearch-license-js`,
      `elasticsearch-logstash-jvm`,
      `elasticsearch-logstash-js`,
      `elasticsearch-migration-jvm`,
      `elasticsearch-migration-js`,
      `elasticsearch-ml-jvm`,
      `elasticsearch-ml-js`,
      `elasticsearch-monitoring-jvm`,
      `elasticsearch-monitoring-js`,
      `elasticsearch-nodes-jvm`,
      `elasticsearch-nodes-js`,
      `elasticsearch-rollup-jvm`,
      `elasticsearch-rollup-js`,
      `elasticsearch-searchable-snapshots-jvm`,
      `elasticsearch-searchable-snapshots-js`,
      `elasticsearch-security-jvm`,
      `elasticsearch-security-js`,
      `elasticsearch-shutdown-jvm`,
      `elasticsearch-shutdown-js`,
      `elasticsearch-slm-jvm`,
      `elasticsearch-slm-js`,
      `elasticsearch-snapshot-jvm`,
      `elasticsearch-snapshot-js`,
      `elasticsearch-sql-jvm`,
      `elasticsearch-sql-js`,
      `elasticsearch-ssl-jvm`,
      `elasticsearch-ssl-js`,
      `elasticsearch-tasks-jvm`,
      `elasticsearch-tasks-js`,
      `elasticsearch-text-structure-jvm`,
      `elasticsearch-text-structure-js`,
      `elasticsearch-transform-jvm`,
      `elasticsearch-transform-js`,
      `elasticsearch-watcher-jvm`,
      `elasticsearch-watcher-js`,
      `elasticsearch-xpack-jvm`,
      `elasticsearch-xpack-js`,
//      `elasticsearch-admin-jvm`,
//      `elasticsearch-admin-js`,
//      `elasticsearch-cat-jvm`,
//      `elasticsearch-cat-js`,
// custom managers
      `elasticsearch-orm-jvm`,
      `elasticsearch-orm-js`,
      // Clients
      `elasticsearch-client-sttp`,
//      `elasticsearch-client-zio-http` //,
//        `elasticsearch-tests`
    ).settings(disableDocs).settings(disablePublishing)

lazy val `zio-common` = ProjectUtils
  .setupCrossModule("zio-common", CrossType.Full)
  .settings(
    moduleName := "zio-common"
  )
  .settings(Dependencies.zioCommon)
//  .dependsOn(`zio-json-extra`)

lazy val `zio-common-jvm` = `zio-common`.jvm
lazy val `zio-common-js` = `zio-common`.js

lazy val `zio-schema-elasticsearch` = ProjectUtils
  .setupCrossModule("zio-schema-elasticsearch", CrossType.Pure)
  .settings(
    moduleName := "zio-schema-elasticsearch"
  )
  .settings(Dependencies.zioSchemaElasticsearch)
  .settings(Dependencies.testSupport)
  .dependsOn(`zio-common`)

lazy val `zio-schema-elasticsearch-jvm` = `zio-schema-elasticsearch`.jvm
lazy val `zio-schema-elasticsearch-js` = `zio-schema-elasticsearch`.js

lazy val `elasticsearch-core` = ProjectUtils
  .setupCrossModule("elasticsearch-core", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-core"
  )
  .dependsOn(`zio-schema-elasticsearch`)

lazy val `elasticsearch-core-jvm` = `elasticsearch-core`.jvm
lazy val `elasticsearch-core-js` = `elasticsearch-core`.js

lazy val `elasticsearch-async-search` = ProjectUtils
  .setupCrossModule("elasticsearch-async-search", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-async-search"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-async-search-jvm` = `elasticsearch-async-search`.jvm
lazy val `elasticsearch-async-search-js` = `elasticsearch-async-search`.js

lazy val `elasticsearch-autoscaling` = ProjectUtils
  .setupCrossModule("elasticsearch-autoscaling", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-autoscaling"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-autoscaling-jvm` = `elasticsearch-autoscaling`.jvm
lazy val `elasticsearch-autoscaling-js` = `elasticsearch-autoscaling`.js

lazy val `elasticsearch-cluster` = ProjectUtils
  .setupCrossModule("elasticsearch-cluster", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-cluster"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-ilm` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-ingest` % "test->test;compile->compile")

lazy val `elasticsearch-cluster-jvm` = `elasticsearch-cluster`.jvm
lazy val `elasticsearch-cluster-js` = `elasticsearch-cluster`.js

lazy val `elasticsearch-ccr` = ProjectUtils
  .setupCrossModule("elasticsearch-ccr", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-ccr"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-ccr-jvm` = `elasticsearch-ccr`.jvm
lazy val `elasticsearch-ccr-js` = `elasticsearch-ccr`.js

lazy val `elasticsearch-dangling-indices` = ProjectUtils
  .setupCrossModule("elasticsearch-dangling-indices", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-dangling-indices"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-dangling-indices-jvm` = `elasticsearch-dangling-indices`.jvm
lazy val `elasticsearch-dangling-indices-js` = `elasticsearch-dangling-indices`.js

lazy val `elasticsearch-enrich` = ProjectUtils
  .setupCrossModule("elasticsearch-enrich", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-enrich"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-enrich-jvm` = `elasticsearch-enrich`.jvm
lazy val `elasticsearch-enrich-js` = `elasticsearch-enrich`.js

lazy val `elasticsearch-eql` = ProjectUtils
  .setupCrossModule("elasticsearch-eql", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-eql"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-eql-jvm` = `elasticsearch-eql`.jvm
lazy val `elasticsearch-eql-js` = `elasticsearch-eql`.js

lazy val `elasticsearch-features` = ProjectUtils
  .setupCrossModule("elasticsearch-features", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-features"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-features-jvm` = `elasticsearch-features`.jvm
lazy val `elasticsearch-features-js` = `elasticsearch-features`.js

lazy val `elasticsearch-fleet` = ProjectUtils
  .setupCrossModule("elasticsearch-fleet", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-fleet"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-fleet-jvm` = `elasticsearch-fleet`.jvm
lazy val `elasticsearch-fleet-js` = `elasticsearch-fleet`.js

lazy val `elasticsearch-graph` = ProjectUtils
  .setupCrossModule("elasticsearch-graph", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-graph"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-graph-jvm` = `elasticsearch-graph`.jvm
lazy val `elasticsearch-graph-js` = `elasticsearch-graph`.js

lazy val `elasticsearch-ilm` = ProjectUtils
  .setupCrossModule("elasticsearch-ilm", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-ilm"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-indices` % "test->test;compile->compile")

lazy val `elasticsearch-ilm-jvm` = `elasticsearch-ilm`.jvm
lazy val `elasticsearch-ilm-js` = `elasticsearch-ilm`.js

lazy val `elasticsearch-indices` = ProjectUtils
  .setupCrossModule("elasticsearch-indices", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-indices"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-indices-jvm` = `elasticsearch-indices`.jvm
lazy val `elasticsearch-indices-js` = `elasticsearch-indices`.js

lazy val `elasticsearch-ingest` = ProjectUtils
  .setupCrossModule("elasticsearch-ingest", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-ingest"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-ingest-jvm` = `elasticsearch-ingest`.jvm
lazy val `elasticsearch-ingest-js` = `elasticsearch-ingest`.js

lazy val `elasticsearch-license` = ProjectUtils
  .setupCrossModule("elasticsearch-license", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-license"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-license-jvm` = `elasticsearch-license`.jvm
lazy val `elasticsearch-license-js` = `elasticsearch-license`.js

lazy val `elasticsearch-logstash` = ProjectUtils
  .setupCrossModule("elasticsearch-logstash", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-logstash"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-logstash-jvm` = `elasticsearch-logstash`.jvm
lazy val `elasticsearch-logstash-js` = `elasticsearch-logstash`.js

lazy val `elasticsearch-migration` = ProjectUtils
  .setupCrossModule("elasticsearch-migration", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-migration"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-migration-jvm` = `elasticsearch-migration`.jvm
lazy val `elasticsearch-migration-js` = `elasticsearch-migration`.js

lazy val `elasticsearch-ml` = ProjectUtils
  .setupCrossModule("elasticsearch-ml", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-ml"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-indices` % "test->test;compile->compile")

lazy val `elasticsearch-ml-jvm` = `elasticsearch-ml`.jvm
lazy val `elasticsearch-ml-js` = `elasticsearch-ml`.js

lazy val `elasticsearch-monitoring` = ProjectUtils
  .setupCrossModule("elasticsearch-monitoring", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-monitoring"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-monitoring-jvm` = `elasticsearch-monitoring`.jvm
lazy val `elasticsearch-monitoring-js` = `elasticsearch-monitoring`.js

lazy val `elasticsearch-nodes` = ProjectUtils
  .setupCrossModule("elasticsearch-nodes", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-nodes"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-indices` % "test->test;compile->compile")

lazy val `elasticsearch-nodes-jvm` = `elasticsearch-nodes`.jvm
lazy val `elasticsearch-nodes-js` = `elasticsearch-nodes`.js

lazy val `elasticsearch-rollup` = ProjectUtils
  .setupCrossModule("elasticsearch-rollup", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-rollup"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-rollup-jvm` = `elasticsearch-rollup`.jvm
lazy val `elasticsearch-rollup-js` = `elasticsearch-rollup`.js

lazy val `elasticsearch-searchable-snapshots` = ProjectUtils
  .setupCrossModule("elasticsearch-searchable-snapshots", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-searchable-snapshots"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-searchable-snapshots-jvm` = `elasticsearch-searchable-snapshots`.jvm
lazy val `elasticsearch-searchable-snapshots-js` = `elasticsearch-searchable-snapshots`.js

lazy val `elasticsearch-security` = ProjectUtils
  .setupCrossModule("elasticsearch-security", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-security"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-security-jvm` = `elasticsearch-security`.jvm
lazy val `elasticsearch-security-js` = `elasticsearch-security`.js

lazy val `elasticsearch-shutdown` = ProjectUtils
  .setupCrossModule("elasticsearch-shutdown", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-shutdown"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-shutdown-jvm` = `elasticsearch-shutdown`.jvm
lazy val `elasticsearch-shutdown-js` = `elasticsearch-shutdown`.js

lazy val `elasticsearch-slm` = ProjectUtils
  .setupCrossModule("elasticsearch-slm", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-slm"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-slm-jvm` = `elasticsearch-slm`.jvm
lazy val `elasticsearch-slm-js` = `elasticsearch-slm`.js

lazy val `elasticsearch-snapshot` = ProjectUtils
  .setupCrossModule("elasticsearch-snapshot", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-snapshot"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-indices` % "test->test;compile->compile")

lazy val `elasticsearch-snapshot-jvm` = `elasticsearch-snapshot`.jvm
lazy val `elasticsearch-snapshot-js` = `elasticsearch-snapshot`.js

lazy val `elasticsearch-sql` = ProjectUtils
  .setupCrossModule("elasticsearch-sql", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-sql"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-sql-jvm` = `elasticsearch-sql`.jvm
lazy val `elasticsearch-sql-js` = `elasticsearch-sql`.js

lazy val `elasticsearch-ssl` = ProjectUtils
  .setupCrossModule("elasticsearch-ssl", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-ssl"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-ssl-jvm` = `elasticsearch-ssl`.jvm
lazy val `elasticsearch-ssl-js` = `elasticsearch-ssl`.js

lazy val `elasticsearch-tasks` = ProjectUtils
  .setupCrossModule("elasticsearch-tasks", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-tasks"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-tasks-jvm` = `elasticsearch-tasks`.jvm
lazy val `elasticsearch-tasks-js` = `elasticsearch-tasks`.js

lazy val `elasticsearch-text-structure` = ProjectUtils
  .setupCrossModule("elasticsearch-text-structure", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-text-structure"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-text-structure-jvm` = `elasticsearch-text-structure`.jvm
lazy val `elasticsearch-text-structure-js` = `elasticsearch-text-structure`.js

lazy val `elasticsearch-transform` = ProjectUtils
  .setupCrossModule("elasticsearch-transform", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-transform"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-ml` % "test->test;compile->compile")

lazy val `elasticsearch-transform-jvm` = `elasticsearch-transform`.jvm
lazy val `elasticsearch-transform-js` = `elasticsearch-transform`.js

lazy val `elasticsearch-watcher` = ProjectUtils
  .setupCrossModule("elasticsearch-watcher", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-watcher"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")

lazy val `elasticsearch-watcher-jvm` = `elasticsearch-watcher`.jvm
lazy val `elasticsearch-watcher-js` = `elasticsearch-watcher`.js

lazy val `elasticsearch-xpack` = ProjectUtils
  .setupCrossModule("elasticsearch-xpack", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-xpack"
  )
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-ml` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-license` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-ilm` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-slm` % "test->test;compile->compile")

lazy val `elasticsearch-xpack-jvm` = `elasticsearch-xpack`.jvm
lazy val `elasticsearch-xpack-js` = `elasticsearch-xpack`.js

/*----------*/

lazy val `elasticsearch-orm` = ProjectUtils
  .setupCrossModule("elasticsearch-orm", CrossType.Pure)
  .settings(
    moduleName := "zio-elasticsearch-orm"
  )
  .dependsOn(`elasticsearch-cluster` % "test->test;compile->compile")
  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
  .settings(Dependencies.elasticsearchORM)

lazy val `elasticsearch-orm-jvm` = `elasticsearch-orm`.jvm
lazy val `elasticsearch-orm-js` = `elasticsearch-orm`.js

//lazy val `elasticsearch-admin` = ProjectUtils
//  .setupCrossModule("elasticsearch-admin")
//  .settings(
//    moduleName := "zio-elasticsearch-admin"
//  )
//  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
//
//lazy val `elasticsearch-admin-jvm` = `elasticsearch-admin`.jvm.settings(Dependencies.elasticsearchAdmin)
//lazy val `elasticsearch-admin-js` = `elasticsearch-admin`.js.settings(Dependencies.elasticsearchAdminJS)
//
//lazy val `elasticsearch-cat` = ProjectUtils
//  .setupCrossModule("elasticsearch-cat", CrossType.Pure)
//  .settings(
//    moduleName := "zio-elasticsearch-cat"
//  )
//  .dependsOn(`elasticsearch-core` % "test->test;compile->compile")
//
//lazy val `elasticsearch-cat-jvm` = `elasticsearch-cat`.jvm
//lazy val `elasticsearch-cat-js` = `elasticsearch-cat`.js

lazy val `elasticsearch-client-sttp` = ProjectUtils
  .setupJVMProject("elasticsearch-client-sttp")
  .settings(
    moduleName := "zio-elasticsearch-client-sttp"
  )
  .settings(Dependencies.clientSTTP)
  .settings(Dependencies.testSupport)
  .dependsOn(
    `elasticsearch-core-jvm` % "test->test;compile->compile"
  )

lazy val `elasticsearch-client-zio-http` = ProjectUtils
  .setupJVMProject("elasticsearch-zio-http")
  .settings(
    moduleName := "zio-elasticsearch-zio-http"
  )
  .settings(Dependencies.clientZioHTTP)
  .settings(Dependencies.testSupport)
  .dependsOn(
    `elasticsearch-core-jvm` % "test->test;compile->compile"
  )

lazy val `elasticsearch-tests` = ProjectUtils
  .setupJVMProject("elasticsearch-tests")
  .settings(
    moduleName := "zio-elasticsearch-tests"
  )
  .settings(Dependencies.testSupport)
  .dependsOn(
    `elasticsearch-core-jvm` % "test->test;compile->compile"
  )
  .dependsOn(
    `elasticsearch-async-search-jvm`,
    `elasticsearch-autoscaling-jvm`,
    `elasticsearch-cluster-jvm`,
    `elasticsearch-ccr-jvm`,
    `elasticsearch-dangling-indices-jvm`,
    `elasticsearch-enrich-jvm`,
    `elasticsearch-eql-jvm`,
    `elasticsearch-features-jvm`,
    `elasticsearch-fleet-jvm`,
    `elasticsearch-graph-jvm`,
    `elasticsearch-indices-jvm`,
    `elasticsearch-ilm-jvm`,
    `elasticsearch-ingest-jvm`,
    `elasticsearch-license-jvm`,
    `elasticsearch-logstash-jvm`,
    `elasticsearch-migration-jvm`,
    `elasticsearch-ml-jvm`,
    `elasticsearch-monitoring-jvm`,
    `elasticsearch-nodes-jvm`,
    `elasticsearch-rollup-jvm`,
    `elasticsearch-searchable-snapshots-jvm`,
    `elasticsearch-security-jvm`,
    `elasticsearch-shutdown-jvm`,
    `elasticsearch-slm-jvm`,
    `elasticsearch-snapshot-jvm`,
    `elasticsearch-sql-jvm`,
    `elasticsearch-ssl-jvm`,
    `elasticsearch-tasks-jvm`,
    `elasticsearch-text-structure-jvm`,
    `elasticsearch-transform-jvm`,
    `elasticsearch-watcher-jvm`,
    `elasticsearch-xpack-jvm`,
    `elasticsearch-orm-jvm`,
    `elasticsearch-client-sttp`,
    `elasticsearch-client-zio-http` //,
  ).settings(disableDocs).settings(disablePublishing)


//lazy val `elasticsearch-orm` = ProjectUtils
//  .setupCrossModule("elasticsearch-orm", CrossType.Full)
//  .settings(
//    moduleName := "zio-elasticsearch-orm"
//  )
//  .dependsOn(`zio-schema-elasticsearch`, `elasticsearch-admin` % "test->test;compile->compile")
//
//lazy val `elasticsearch-orm-jvm` = `elasticsearch-orm`.jvm
//lazy val `elasticsearch-orm-js` = `elasticsearch-orm`.js

/*
lazy val `elasticsearch-client-http4s` = ProjectUtils
  .setupJVMProject("elasticsearch-client-http4s")
  .settings(
    moduleName := "zio-elasticsearch-client-http4s"
  )
  .settings(Dependencies.clientHttp4s)
  .dependsOn(
    `elasticsearch-orm-jvm` % "test->test;compile->compile",
    `elasticsearch-core-jvm` % "test->test;compile->compile",
    `elasticsearch-admin-jvm` % "test->test;compile->compile",
    `elasticsearch-cat-jvm` % "test->test;compile->compile"
  )
 */
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
