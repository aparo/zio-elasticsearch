import scala.collection.mutable
import scala.util.Try
import better.files._

object EnvironmentGlobal {
  def appName: String = sys.env.getOrElse("APP_NAME", "megl")

  lazy val CI_MEGL: Boolean = sys.env.contains("CI_MEGL")

  // use yarn for javascript
  def useYarn: Boolean =
    Try(sys.env.getOrElse("USE_YARN", "false").toBoolean).toOption.getOrElse(false)

  var configSearchMethods: List[(String, String) => File] = List(
    //    check in etc
    { (filename: String, scope: String) =>
      File(s"/etc/$scope/$appName/$filename")
    },
    //    check in config
    { (filename: String, scope: String) =>
      File(s"config/$filename")
    },
    //    check in config+app
    { (filename: String, scope: String) =>
      File(s"config/$appName/$filename")
    },
    //    check in ../config
    { (filename: String, scope: String) =>
      File(s"../config/$filename")
    },
    //    check in ../config+app
    { (filename: String, scope: String) =>
      File(s"../config/$appName/$filename")
    }
  )

  def validateFile(filename: String, scope: String = "megl"): File = {
    val notFounds = new mutable.ListBuffer[File]()
    var result: Option[File] = None
    configSearchMethods.find { func =>
      val res = func(filename, scope)
      val exists = res.exists
      if (exists)
        result = Some(res)
      else
        notFounds += res
      exists
    }

    result match {
      case Some(value) => value
      case None =>
        throw new RuntimeException(
          s"unable to find configuration in ${notFounds.map(_.toString()).mkString(", ")}"
        )
    }
  }

  def applicationConfig(scope: String = "megl"): String = {
    val value = sys.env.getOrElse(
      "APPLICATION_CONFIG",
      validateFile("application.conf", scope).toString()
    )
    value
  }

  def logbackXML(scope: String = "megl"): String = {
    val value = sys.env.getOrElse(
      "LOGBACK_CONFIG",
      validateFile("logback.xml", scope).toString()
    )
    value
  }

  def sonatypeHost: String = {
    val ssl =
      if (sys.env.getOrElse("SONATYPE_USE_SSL", "true") == "true") "s" else ""
    val server = sys.env.getOrElse("SONATYPE_HOST", "maven.megl.io")
    s"http$ssl://$server"
  }
}
