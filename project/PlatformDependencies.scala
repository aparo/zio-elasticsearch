import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object PlatformDependencies {
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

  object Izumi {
    lazy val logstageCore =
      Def.setting("io.7mind.izumi" %%% "logstage-core" % Versions.izumi)
    lazy val logstageApi =
      Def.setting("io.7mind.izumi" %%% "logstage-api" % Versions.izumi)
    lazy val logstageRenderingCirce =
      Def.setting(
        "io.7mind.izumi" %%% "logstage-rendering-circe" % Versions.izumi)
  }

  object ScalaTest {
    lazy val test =
      Def.setting("org.scalatest" %%% "scalatest" % Versions.scalaTest)
    lazy val scalactic =
      Def.setting("org.scalactic" %%% "scalactic" % Versions.scalaTest)
  }

  object ZIO {
    lazy val core = Def.setting("dev.zio" %%% "zio" % Versions.zio)
    lazy val streams = Def.setting("dev.zio" %%% "zio-streams" % Versions.zio)
  }

}
