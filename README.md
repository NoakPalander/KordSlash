# KordSlash 
<img src="https://i.imgur.com/as2Vqfa.png" width="200" height="200"  alt="logo"/>

## Description
Adds a small extension library to the [Kord](https://github.com/kordlib/kord) discord framework with  a slash command builder.

## Usage
The gradle project has a task with the name `jar`, this will build the library as a jar in `lib/build/libs/slash.jar`. Integrate it into your own gradle project can be done in many ways but as an example: 

```kotlin
// Kotlin DSL
plugins {
    kotlin("jvm") version "1.5.20"
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Kord
    implementation("dev.kord:kord-core:0.7.4")
    
    // KordSlash
    implementation(files("relative/path/to/jar"))
}

application {
    // Define the main class for the application.
    mainClass.set("com.yourapp.AppKt")
}
```

## Example
```kotlin
@KordPreview
suspend fun main() {
    val client = Kord("token")
    client.slashes { // this: SlashesCreateBuilder
        slash("foo").delete()           // deletes a global application command named foo
        slash("foo", guild).delete()    // deletes a guild application command
        
        slash("foo")                    // gets a Command.Global
        slash("foo", guild)             // gets a Command.Guild
        
        // Creates a guild slash command
        slash("name", "description", guild) { // this: SlashCreateBuilder
            onCreate { // this: ApplicationCommandCreateBuilder
                string("arg", "first argument") {
                    required = true
                }
            }
            // Will be invoked when the command is run
            onEvent { // this: InteractionCreateEvent
                interaction.respondPublic {
                    content = "response!"
                }
            }
        }
        
        // Creates a global slash command
        slash("name", "description") {
            onCreate { ... }
            onEvent { ... }
        }
    }
    
    
    client.login()
}
```

## License
MIT, see the LICENSE file for details.