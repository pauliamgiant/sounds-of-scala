# Getting Started

## Adding Sounds of Scala to your project

Add the following to your build.sbt file:

```scala 3
libraryDependencies += "org.soundsofscala" %%% "sounds-of-scala" % "0.8.2"
```

## Playing Audio

It occurred to me two years into the project that the most basic functionality to provide is to simply be able to play audio from a Scala project.

The fastest way to make some sound emanate from your device is with the SimpleAudioPlayer.

In the code of your Scala.js project, pass the path to an audio file to the SimpleAudioPlayer constructor.

Making sure you have the volume turned up is on you.

```scala 3
val audioPlayer = SimpleAudioPlayer("<PATH_TO_LOCAL_AUDIO_FILE>")
audioPlayer.play()
audioPlayer.pause()
audioPlayer.play()
audioPlayer.stop()
```

## Creating a Scala.js Project

In order to use Sounds of Scala to build a web audio application you will need to create a Scala.js project.

There are a number of ways to create a Scala.js project. At some point you can expect to find a giter8 template here which will do this all for you but for now you can use one of the following methods.

---

### Scala.js with Tyrian (Recommended)

[Tyrian](https://tyrian.indigoengine.io/) is a Scala.js web framework built on Cats Effect. Since Sounds of Scala also uses Cats Effect for all of its IO and concurrency, Tyrian is the natural pairing. You get a consistent functional programming model across your UI and audio code, with no need to bridge between different effect systems.

```bash
sbt new PurpleKingdomGames/tyrian.g8
```

---

### Scala.js with Laminar

[Laminar](https://laminar.dev/) is another excellent Scala.js UI framework. It uses its own reactive system (Airstream) rather than Cats Effect, so you will need to bridge between the two when connecting your UI to the Sounds of Scala API.

```bash
sbt new raquo/scalajs.g8
```

Say "yes" to Laminar and CSS.

---

### Scala.js with Vite

If you prefer a minimal setup without a UI framework, you can use the Scala.js Vite template.

```bash
sbt new scala-js/vite.g8
```

Once you have created the project from the giter8 template, bump the minimum versions of the following:

In project/build.properties:

```bash
sbt.version=1.10.0
```

In build.sbt:

```scala 3
 scalaVersion := "3.3.3"
 "org.scala-js" %%% "scalajs-dom" % "2.8.0"
```

And in project/plugins.sbt:

```scala 3
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
```

---

### Scala.js docs

[Scala.js Docs](https://www.scala-js.org/doc/tutorial/basic/)

### A Quick Project Scaffold using Scala.js & Vite

Here is an example of a simple Scala.js project using the Vite template.

You can use this to get started quickly and start using the Sounds of Scala library from within the **firstMusicProgram** method.

```scala 3
import cats.effect.{IO, Ref}
import cats.effect.unsafe.implicits.global
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, document}
import org.soundsofscala.models.*
import org.soundsofscala.transport.Sequencer

@main
def helloWorld(): Unit =

  val homeDiv = document.createElement("div")

  val heading = document.createElement("h1")
  heading.textContent = "My First Music App"

// asInstanceOf - you'll have to get over it on this occasion
  val button = document.createElement("button").asInstanceOf[dom.html.Button]
  button.classList.add("button")
  button.textContent = "▶️"
  button.onclick = _ =>
    given AudioContext = new AudioContext()
    firstMusicProgram().unsafeRunAndForget()

  homeDiv.appendChild(heading)
  homeDiv.appendChild(button)
  dom.document.querySelector("#app").append(homeDiv)

def firstMusicProgram(): AudioContext ?=> IO[Unit] = ???

  // TODO: Your code
```

### [Next Step: Playing a test song](../getting-started/playing-test-song.md)
