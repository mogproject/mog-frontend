enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.mogproject",
      scalaVersion := "2.12.0"
    )),
    name := "mog-frontend",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
      "com.lihaoyi" %%% "scalatags" % "0.6.2",
      "com.typesafe.play" %%% "play-json" % "2.6.1",
      "org.scalatest" %%% "scalatest" % "3.0.1" % Test,
      "org.scalacheck" %%% "scalacheck" % "1.13.4" % Test
    ),
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature"),

    skip in packageJSDependencies := false,

    jsDependencies ++= Seq(
      RuntimeDOM,
      ProvidedJS / "assets/js/ecl_new.js" % Test
    )
  )
  .dependsOn(mogCore)

lazy val mogCore = ProjectRef(uri("git://github.com/mogproject/mog-core-scala.git#master"), "mogCoreJS")

