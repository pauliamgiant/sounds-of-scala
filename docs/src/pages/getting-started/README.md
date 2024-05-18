# Getting Started

## Creating a Scala.js Project

In order to use Sounds of Scala you will need to create a Scala.js project.

There are a number of ways to create a Scala.js project for use with Sounds of Scala. At some point you can expect to find a giter8 template here which will do this all for you but for now you can use one of the following methods. 

---

### Scala.js with Vite
```bash
sbt new scala-js/vite.g8
```
Once you have the created the project from the giter8 template bump the minimum versions of the following:

In project/build.properties:
```bash
sbt.version=1.10.0
```
In build.sbt
```scala 3
 scalaVersion := "3.3.3"
 "org.scala-js" %%% "scalajs-dom" % "2.8.0"
```
And in project/plugins.sbt the sbt-scalajs plug in.
```scala 3
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")

```



---

### Scala.js with Tyrian
```bash
sbt new PurpleKingdomGames/tyrian.g8
``` 
---

### Scala.js docs
[Scala.js Docs](https://www.scala-js.org/doc/tutorial/basic/)
And then follow this guide to add Scala

### A Quick Project Scaffold using Scala.js & Vite
Here is an example of a simple Scala.js project using the Vite template:

You can use this to get started with your own project quickly, and simply start using the Sounds of Scala library from within the **firstMusicProgram** method.

```scala 3
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, document}

@main
def helloWorld(): Unit =

  // create a page wrapper
  val homeDiv = document.createElement("div")

  // create page title
  val heading = document.createElement("h1")
  heading.textContent = "My First Music App"

  // create a play button for testing
  val button = document.createElement("button").asInstanceOf[dom.html.Button]
  button.classList.add("button")
  button.textContent = "▶️"
  button.onclick = _ =>
    
    // We always need an AudioContext to play web audio - more info on this coming up
    given AudioContext = new AudioContext()
    // call our first music program method
    firstMusicProgram().unsafeRunAndForget()

  homeDiv.appendChild(heading)
  homeDiv.appendChild(button)
  dom.document.querySelector("#app").append(homeDiv)

def firstMusicProgram(): AudioContext ?=> IO[Unit] = ???
  
  // TODO: Your code
```
### [Next Step: Playing a test song](http://localhost:4242/getting-started/playing-test-song.html)
