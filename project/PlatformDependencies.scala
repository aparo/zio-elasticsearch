import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object PlatformDependencies {

  object HTTP4S {
    lazy val circe =
      "org.http4s" %% "http4s-circe" % Versions.http4s
    lazy val blazeClient =
      "org.http4s" %% "http4s-blaze-client" % Versions.http4s
    lazy val dsl =
      "org.http4s" %% "http4s-dsl" % Versions.http4s

  }

  object Libraries {
    lazy val magnolia = Def.setting("com.propensive" %%% "magnolia" % "0.17.0")
    lazy val shapeless = Def.setting("com.chuusai" %%% "shapeless" % "2.4.0-M1")

  }

  object ScalaTest {
    lazy val test =
      Def.setting("org.scalatest" %%% "scalatest" % Versions.scalaTest)
    lazy val scalactic =
      Def.setting("org.scalactic" %%% "scalactic" % Versions.scalaTest)
  }
  object Specs2 {
    lazy val core = Def.setting("org.specs2" %% "specs2-core" % Versions.specs2)
    lazy val mock = Def.setting("org.specs2" %% "specs2-mock" % Versions.specs2)
    lazy val junit =
      Def.setting("org.specs2" %% "specs2-junit" % Versions.specs2)
    lazy val scalaCheck =
      Def.setting("org.specs2" %% "specs2-scalacheck" % Versions.specs2)
  }
  object ZIO {
    lazy val core = Def.setting("dev.zio" %%% "zio" % Versions.zio)
    lazy val json = Def.setting("dev.zio" %%% "zio-json" % "0.7.45")
    lazy val streams = Def.setting("dev.zio" %%% "zio-streams" % Versions.zio)
    lazy val zioJsonExtra = Def.setting("io.megl" %%% "zio-json-extra" % Versions.zioJsonExtra)
    lazy val zioJsonException = Def.setting("io.megl" %%% "zio-json-exception" % Versions.zioJsonExtra)
  }

}
