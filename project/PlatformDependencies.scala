import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object PlatformDependencies {

  object Cats {
    val core = Def.setting("org.typelevel" %%% "cats-core" % Versions.cats)
    val laws = Def.setting("org.typelevel" %%% "cats-laws" % Versions.cats)
    val all = Def.setting("org.typelevel" %%% "cats" % Versions.cats)
    val catsEffect = Def.setting("org.typelevel" %%% "cats-effect" % Versions.catsEffect)
  }

  object Circe {
    lazy val core = Def.setting("io.circe" %%% "circe-core" % Versions.circe)
    lazy val generic =
      Def.setting("io.circe" %%% "circe-generic" % Versions.circe)
    lazy val genericExtras =
      Def.setting("io.circe" %%% "circe-generic-extras" % Versions.circe)
    lazy val jawn = Def.setting("io.circe" %% "circe-jawn" % Versions.circe)
    lazy val refined =
      Def.setting("io.circe" %%% "circe-refined" % Versions.circe)
    lazy val parser =
      Def.setting("io.circe" %%% "circe-parser" % Versions.circe)
    lazy val testing =
      Def.setting("io.circe" %% "circe-testing" % Versions.circe)
    lazy val derivation =
      Def.setting(
        "io.circe" %% "circe-derivation-annotations" % Versions.circeDerivation
      )
    lazy val yaml = "io.circe" %% "circe-yaml" % "0.8.0"
  }

  object Enumeratum {
    lazy val core =
      Def.setting("com.beachape" %%% "enumeratum" % Versions.enumeratum)
    lazy val circe = Def.setting(
      "com.beachape" %%% "enumeratum-circe" % Versions.enumeratumCirce
    )
  }

  object HTTP4S {
    lazy val circe =
      "org.http4s" %% "http4s-circe" % Versions.http4s
    lazy val blazeClient =
      "org.http4s" %% "http4s-blaze-client" % Versions.http4s
    lazy val dsl =
      "org.http4s" %% "http4s-dsl" % Versions.http4s

  }

  object Libraries {
    lazy val magnolia = Def.setting("com.propensive" %%% "magnolia" % "0.12.5")
    lazy val shapeless = Def.setting("com.chuusai" %%% "shapeless" % "2.3.3")
    lazy val javaLocales = Def.setting("com.github.cquiroz" %%% "scala-java-locales" % "0.4.0-cldr30") // 0.6.0")
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
    lazy val streams = Def.setting("dev.zio" %%% "zio-streams" % Versions.zio)
    lazy val test = Def.setting("dev.zio" %%% "zio-test" % Versions.zio)
    lazy val testSbt = Def.setting("dev.zio" %%% "zio-test-sbt" % Versions.zio)

    lazy val interopCats = Def.setting("dev.zio" %%% "zio-interop-cats" % "2.0.0.0-RC12")

    lazy val logging= Def.setting("dev.zio" %%% "zio-logging" % Versions.zioLogging)
    lazy val loggingSlf4= "dev.zio" %% "zio-logging-slf4j" % Versions.zioLogging

  }
}
