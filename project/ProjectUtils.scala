import sbt.Keys._
import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import de.heikoseeberger.sbtheader.AutomateHeaderPlugin
import sbtcrossproject._

import scala.sys.process._
import scala.util.Try

object ProjectUtils {
  type PE = Project => Project
  type XPE = CrossProject => CrossProject

  private val pathToSkipInNames = Set("libraries", "pocs", "component", "megl")

  def preventPublication: PE =
    _.settings(
      publishTo := Some(
        Resolver.file("Unused transient repository", target.value / "fakepublish")
      ),
      publishArtifact := false,
      packagedArtifacts := Map.empty
    ) // doesn't work - https://github.com/sbt/sbt-pgp/issues/42

  private def generateName(path: String): String =
    path.split("/").filterNot(v => pathToSkipInNames.contains(v)).mkString("-")

  private def generateId(path: String): String =
    path.split("/").filterNot(v => pathToSkipInNames.contains(v)).flatMap(_.split("-")).reduce(_ + _.capitalize)

  def setupJVMProject(path: String, publish: Boolean = true): Project = {
    val id = generateId(path)
    Project(id = id, file(path)).configure(setupDefaultProject(path, publish)).settings(Common.commonJvmSettings)
  }

  def setupJSProject(path: String, publish: Boolean = true): Project = {
    val id = generateId(path)
    Project(id = id, file(path))
      .enablePlugins(ScalaJSPlugin)
      .configure(setupDefaultProject(path, publish))
      .settings(Common.commonJsSettings)
  }

  def setupDefaultProject(path: String, publish: Boolean = true)(
    project: Project
  ): Project = {
    val docName = path.split("/").flatMap(_.split("-")).map(_.capitalize).mkString(" ")
    val fullname = s"zio-${generateName(path)}"
    project
      .enablePlugins(AutomateHeaderPlugin)
      .settings(
        description := s"ZIO $docName",
        moduleName := fullname,
        name := fullname
      )
      .settings(Common.commonGeneric)
      .settings(
        if (publish) Common.publicationSettings
        else Common.noPublishSettings
      )
  }

  def setupCross(project: CrossProject): CrossProject =
    project
      .platformsSettings(scalajscrossproject.JSPlatform)(
        Common.commonJsSettings
      )
      .platformsSettings(sbtcrossproject.JVMPlatform)(
        Common.commonJvmSettings
      )

  def setupCrossModule(
    path: String,
    crossType: CrossType = CrossType.Full,
    publish: Boolean = true
  ) = {
    val id = generateId(path)
    import CrossPlugin.autoImport._
    CrossProject(id, file(path))(scalajscrossproject.JSPlatform, JVMPlatform)
      .crossType(crossType)
      .withoutSuffixFor(sbtcrossproject.JVMPlatform)
      .configure(setupDefaultProject(path, publish))
      .platformsSettings(scalajscrossproject.JSPlatform)(
        Common.commonJsSettings
      )
      .jvmSettings(Common.commonJvmSettings)
      .settings(
        if (publish) Common.publicationSettings
        else Common.noPublishSettings
      )

  }

}
