import sbt._
import sbt.Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  import PlatformDependencies._

  lazy val testSupport = Seq(
    libraryDependencies ++= DependencyHelpers.test(
      ScalaTest.test.value,
      "dev.zio" %% "zio-test" % Versions.zio,
      "dev.zio" %% "zio-test-sbt" % Versions.zio,
      "com.dimafeng" %% "testcontainers-scala-elasticsearch" % Versions.testContainerScala,
      "ch.qos.logback" % "logback-core" % "1.4.6"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

  lazy val zioSchemaElasticsearch = Seq(
    libraryDependencies ++= DependencyHelpers.compile(
      "dev.zio" %%% "zio-schema-json" % "0.4.12"
    )
  )

  lazy val elasticsearchORM = Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % Versions.scala % Provided
    )
  )

  lazy val elasticsearchAdmin = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      )
  }

  lazy val elasticsearchAdminJS = Def.settings {
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % Versions.scala % Provided
    )
  }

  lazy val zioJsonExtra = Def.settings {
    libraryDependencies ++= DependencyHelpers.compile(
      ZIO.json.value,
      ZIO.core.value,
      ZIO.streams.value,
//      "org.scala-lang" % "scala-reflect" % Versions.scala %Provided,
      "org.gnieh" %%% "diffson-core" % "4.4.0",
//      "com.softwaremill.magnolia1_2" %%% "magnolia" % "1.1.2",
      "org.scala-lang.modules" %%% "scala-collection-compat" % "2.11.0",
      "io.github.cquiroz" %%% "scala-java-time" % "2.5.0"
    ) ++
      DependencyHelpers.test(
        ScalaTest.test.value,
        Specs2.core.value,
        Specs2.scalaCheck.value
      )
  }

  lazy val zioCommon = Def.settings {
    libraryDependencies ++=
      DependencyHelpers.compile(
        ZIO.zioJsonException.value,
        ZIO.zioJsonExtra.value
      ) ++ DependencyHelpers.test(
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
      "com.softwaremill.sttp.client3" %% "zio" % "3.8.15",
      "com.softwaremill.sttp.client3" %% "prometheus-backend" % "3.8.15"
    )
  }

  lazy val clientZioHTTP = Def.settings {
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-http" % "3.0.0-RC2"
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
