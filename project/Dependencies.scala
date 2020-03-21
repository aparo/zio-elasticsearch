import sbt._
import sbt.Keys._

object Dependencies {
  import PlatformDependencies._

  lazy val elasticsearchCore = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(ZIO.logging.value,
                                                      Circe.derivation.value,
                                                      Circe.parser.value,
                                                      Enumeratum.circe.value,
                                                      ZIO.core.value,
                                                      ZIO.streams.value) ++
      DependencyHelpers.test(
        ScalaTest.test.value
      )
  }

  lazy val zioCirce = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(ZIO.logging.value,
                                                      Circe.derivation.value,
                                                      Circe.parser.value,
                                                      Enumeratum.circe.value,
                                                      ZIO.core.value,
                                                      ZIO.streams.value) ++
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value,
        Specs2.scalaCheck.value
      )
  }

  lazy val zioCommon = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      ZIO.interopCats.value,
      Cats.core.value,
      Cats.catsEffect.value
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value
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
      Libraries.shapeless.value
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value
      )
  }

  lazy val clientSTTP = Def.settings {
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % Versions.sttp,
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio-streams" % Versions.sttp
    ) ++ DependencyHelpers.test(
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
      Cats.catsEffect.value
    ) ++ DependencyHelpers.test(
      ScalaTest.test.value,
      "org.codelibs" % "elasticsearch-cluster-runner" % Versions.elasticsearchClusterRunner,
      "com.dimafeng" %% "testcontainers-scala" % Versions.testContainerScala
    )
  }

}
