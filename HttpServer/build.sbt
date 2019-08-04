name := "MyProject"
version := "1.0"
scalaVersion := "2.11.8"

organization := "com.gwu"

libraryDependencies ++= {
  val akkaVersion = "2.5.23"
  Seq(
    "com.typesafe.akka"       %% "akka-actor"                        % akkaVersion,
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "com.typesafe.akka"       %% "akka-remote"                       % akkaVersion,
    "com.typesafe.akka"       %% "akka-cluster"                      % akkaVersion,
    "com.typesafe.akka"       %% "akka-multi-node-testkit"           % akkaVersion   % "test",
    "com.typesafe.akka"       %% "akka-testkit"                      % akkaVersion   % "test",
    "com.typesafe.akka"       %% "akka-stream"                       % akkaVersion, 
    "com.typesafe.akka"       %% "akka-http"                         % "10.1.9",
    "org.scalatest"           %% "scalatest"                         % "3.0.0"       % "test",
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "ch.qos.logback"          %  "logback-classic"                   % "1.0.10"
  )
}

// Assembly settings
mainClass in assembly := Some("aia.http.httpserver.HttpServer")
assemblyJarName in assembly := "httpserver.jar"
