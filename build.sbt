enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .enablePlugins(JSDependenciesPlugin)
  .settings(
    inThisBuild(List(
      organization := "com.mogproject",
      scalaVersion := "2.13.2"
    )),
    name := "mog-frontend",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "be.doeraene" %%% "scalajs-jquery" % "1.0.0",
      "com.lihaoyi" %%% "scalatags" % "0.9.1",
      "com.typesafe.play" %%% "play-json" % "2.9.0",
      "org.scalatest" %%% "scalatest" % "3.1.2" % Test,
      "org.scalacheck" %%% "scalacheck" % "1.14.3" % Test,
      "org.scalatestplus" %%% "scalacheck-1-14" % "3.1.2.0" % Test
    ),
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature"),

    scalaJSUseMainModuleInitializer := false,

    Test / scalaJSUseMainModuleInitializer := sys.env.get("BENCHMARK").contains("true"),

    Test / scalaJSUseTestModuleInitializer := sys.env.get("UNITTEST").contains("true"),

    skip in packageJSDependencies := false,

    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),

    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % "1.12.4" / "jquery.js" % Test,
      ProvidedJS / "assets/js/ecl_new.js" % Test,
      ProvidedJS / "assets/js/bootstrap.min.js" % Test,
      ProvidedJS / "assets/js/clipboard.min.js" % Test
    )
  )
  .dependsOn(mogCore)

lazy val mogCore = ProjectRef(uri("ssh://git@github.com/mogproject/mog-core-scala.git#master"), "mogCoreJS")

