import sbt._
import sbt.Keys._

object Dependencies {
  import PlatformDependencies._

  lazy val elasticsearchCore = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      ZIO.logging.value,
      Circe.derivation.value,
      Circe.parser.value,
      Enumeratum.circe.value,
      ZIO.core.value,
      ZIO.streams.value
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value
      )
  }

  lazy val zioCirce = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      ZIO.logging.value,
      Circe.derivation.value,
      Circe.parser.value,
      Enumeratum.circe.value,
      Libraries.javaTime.value,
      ZIO.core.value,
      ZIO.streams.value
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value,
        Specs2.scalaCheck.value
      )
  }

  lazy val zioCommon = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      ZIO.logging.value,
      ZIO.interopCats.value,
      Cats.core.value,
      Cats.catsEffect.value
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value
      )
  }

  lazy val zioCommonJS = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      Libraries.javaLocales.value
    )
  }

  lazy val circeMinimal = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      Circe.derivation.value,
      Circe.parser.value,
      Enumeratum.circe.value
    )
  }

  lazy val zioSchema = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      Libraries.magnolia.value,
      Libraries.shapeless.value,
      ZIO.macros.value
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value
      )
  }

  lazy val clientSTTP = Def.settings {
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % Versions.sttp,
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio-streams" % Versions.sttpStream
    ) 
  }

  lazy val clientHttp4s = Def.settings {
    libraryDependencies ++= Seq(
      HTTP4S.dsl,
      HTTP4S.circe,
      HTTP4S.blazeClient,
      ZIO.interopCats.value,
      Cats.catsEffect.value
    ) 
  }

  lazy val commonESTest=Def.settings {
    libraryDependencies ++= DependencyHelpers.test(
      "ch.qos.logback"       % "logback-classic"          % "1.2.3",
      ScalaTest.test.value,
      "org.codelibs" % "elasticsearch-cluster-runner" % Versions.elasticsearchClusterRunner,
      "com.dimafeng" %% "testcontainers-scala" % Versions.testContainerScala,
      ZIO.loggingSlf4
    )
  }
}
