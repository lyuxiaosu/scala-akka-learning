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
    "org.scalatest"           %% "scalatest"                         % "3.0.0"       % "test",
    "com.typesafe.akka"       %% "akka-slf4j"                        % akkaVersion,
    "ch.qos.logback"          %  "logback-classic"                   % "1.0.10",
    "com.typesafe.akka"       %% "akka-persistence"                  % "2.5.23",
    "com.typesafe.akka"       %% "akka-stream"                       % "2.5.23",
    "com.typesafe.akka"       %% "akka-cluster-sharding"             % "2.5.23",
    "com.typesafe.akka"       %% "akka-http"                         % "10.1.9",
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
  )
}

// Assembly settings
mainClass in assembly := Some("aia.cluster.words.Main")
assemblyJarName in assembly := "words-node.jar"
