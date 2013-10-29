import android.Keys._

android.Plugin.androidBuild

name := "ScaloidChat"

scalaVersion := "2.10.3"

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize",
"-keep public class org.joda.time.** {public private protected *;}",
"-dontwarn org.joda.time.**",
"-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry"
)

libraryDependencies += "org.scaloid" %% "scaloid" % "2.4-8"

scalacOptions in Compile += "-feature"

javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")

run <<= run in Android

install <<= install in Android
