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
      )
  }

  lazy val zioJsonExtra = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      ZIO.json.value,
      ZIO.core.value,
      ZIO.streams.value,
//      "org.scala-lang" % "scala-reflect" % Versions.scala %Provided,
      "org.gnieh" %%% "diffson-core" % "4.3.0",
//      "com.softwaremill.magnolia1_2" %%% "magnolia" % "1.1.2",
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
      HTTP4S.blazeClient
    ) ++ DependencyHelpers.test(
      ScalaTest.test.value,
      "com.dimafeng" %% "testcontainers-scala-elasticsearch" % Versions.testContainerScala
    )
  }

}
