package io.codecrafters

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class Main

fun main(args: Array<String>) {
  runApplication<Main>(*args)
}

@Component
class ShellRunner : CommandLineRunner {
  override fun run(vararg args: String) {
    while (true) {
      print("$ ")
      println(readln() + ": command not found")
    }
  }
}
