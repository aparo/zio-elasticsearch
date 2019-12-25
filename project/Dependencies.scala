import sbt._
import sbt.Keys._

object Dependencies {
  import PlatformDependencies._

  lazy val elasticsearchCore = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(Izumi.logstageCore.value,
                                                      Circe.derivation.value,
                                                      Circe.parser.value,
                                                      Enumeratum.circe.value,
                                                      ZIO.core.value,
                                                      ZIO.streams.value) ++
      DependencyHelpers.test(
        ScalaTest.test.value
      )
  }
//  ,
//  "org.scalatestplus" %% "scalatestplus-scalacheck" % scalaTestPlusVersion % Test,
//  "org.codelibs" % "elasticsearch-cluster-runner" % elasticsearchClusterRunnerVersion % Test,
//  "com.dimafeng" %% "testcontainers-scala" % testContainerScalaVersion % Test

  lazy val clientSTTP = Def.settings {
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.0.0-RC5",
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio-streams" % "2.0.0-RC5"
    ) ++ DependencyHelpers
      .test(
        ScalaTest.test.value,
//      "org.scalatestplus" %% "scalatestplus-scalacheck" % Versions. ,
        "org.codelibs" % "elasticsearch-cluster-runner" % Versions.elasticsearchClusterRunner,
        "com.dimafeng" %% "testcontainers-scala" % Versions.testContainerScala
      )
  }
}
