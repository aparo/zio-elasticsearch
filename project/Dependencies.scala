import sbt._
import sbt.Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  import PlatformDependencies._

  lazy val testSupport = Seq(
    libraryDependencies ++= DependencyHelpers.test(
      ScalaTest.test.value,
      "dev.zio" %% "zio-test" % Versions.zio,
      "dev.zio" %% "zio-test-sbt" % Versions.zio
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

  lazy val zioSchemaElasticsearch = Seq(
    libraryDependencies ++= DependencyHelpers.compile(
    "dev.zio" %%% "zio-schema-json" % "0.2.1" 
   )
   )

  lazy val elasticsearchCore = Seq(
    libraryDependencies ++= DependencyHelpers.test(
      ScalaTest.test.value,
      "dev.zio" %% "zio-test" % Versions.zio,
      "dev.zio" %% "zio-test-sbt" % Versions.zio
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

  lazy val elasticsearchAdmin = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      "org.gnieh" %% "diffson-core" % "4.3.0",
      "org.gnieh" %% "diffson-circe" % "4.3.0"
    )
  }

  lazy val zioCirce = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      Circe.derivation.value,
      Circe.parser.value,
      Enumeratum.circe.value,
      ZIO.core.value,
      ZIO.streams.value,
      "org.scala-lang.modules" %%% "scala-collection-compat" % "2.8.1",
      "io.github.cquiroz" %%% "scala-java-time" % "2.4.0"
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value,
        Specs2.scalaCheck.value
      )
  }

  lazy val zioCommon = Def.settings {
    libraryDependencies ++=
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value
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
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.8.3",
      "com.softwaremill.sttp.client3" %% "prometheus-backend" % "3.8.3"
    ) ++ DependencyHelpers.test(
      ScalaTest.test.value,
      "com.dimafeng" %% "testcontainers-scala-elasticsearch" % Versions.testContainerScala
    )
  }

  lazy val clientHttp4s = Def.settings {
    libraryDependencies ++= Seq(
      HTTP4S.dsl,
      HTTP4S.circe,
      HTTP4S.blazeClient,
      ZIO.interopCats.value,
      "org.typelevel" %% "cats-effect" % "3.3.14"
    ) ++ DependencyHelpers.test(
      ScalaTest.test.value,
      "com.dimafeng" %% "testcontainers-scala-elasticsearch" % Versions.testContainerScala
    )
  }

}
