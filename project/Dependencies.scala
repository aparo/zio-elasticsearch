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
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.3.0",
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio-streams" % "2.1.5"
    ) ++ DependencyHelpers
      .test(
        ScalaTest.test.value,
        "org.codelibs" % "elasticsearch-cluster-runner" % Versions.elasticsearchClusterRunner,
        "com.dimafeng" %% "testcontainers-scala" % Versions.testContainerScala
      )
  }

  lazy val clientHttp4s = Def.settings {
    libraryDependencies ++= Seq(
      HTTP4S.dsl,
      HTTP4S.circe,
      HTTP4S.blazeClient,
      ZIO.interopCats.value,
      "org.typelevel" %% "cats-effect" % "3.3.14"
    ) ++ DependencyHelpers
      .test(
        ScalaTest.test.value,
        "org.codelibs" % "elasticsearch-cluster-runner" % Versions.elasticsearchClusterRunner,
        "com.dimafeng" %% "testcontainers-scala" % Versions.testContainerScala
      )
  }


}
