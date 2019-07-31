name := "singleton"

version := "1.0"

organization := "com.gwu"

libraryDependencies ++= {
  val akkaVersion = "2.4.12"
  Seq(
    "com.typesafe.akka"       %% "akka-actor"                        % akkaVersion,
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "com.typesafe.akka"       %% "akka-remote"                       % akkaVersion,
    "com.typesafe.akka"       %% "akka-cluster"                      % akkaVersion,
    "com.typesafe.akka"	      %% "akka-cluster-tools"                % akkaVersion,
    "com.typesafe.akka"       %% "akka-multi-node-testkit"           % akkaVersion   % "test",
    "com.typesafe.akka"       %% "akka-testkit"                      % akkaVersion   % "test",
    "org.scalatest"           %% "scalatest"                         % "3.0.0"       % "test",
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "ch.qos.logback"          %  "logback-classic"                   % "1.0.10"
  )
}


// Assembly settings
mainClass in assembly := Some("aia.cluster.single.SingletonDemo")

assemblyJarName in assembly := "singleton.jar"

