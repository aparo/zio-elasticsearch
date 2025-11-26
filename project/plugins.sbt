// https://github.com/sbt/sbt-release
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")
// https://github.com/xerial/sbt-sonatype
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.12.2")
// https://github.com/sbt/sbt-pgp
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.19.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.4")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
// addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.2")
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.7")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.10.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.3")
libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.2"
