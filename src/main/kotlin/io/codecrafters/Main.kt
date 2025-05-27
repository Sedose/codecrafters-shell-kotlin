package io.codecrafters

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

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
      val inputLine = readln()

      when {
        inputLine.startsWith("exit") -> {
          val status = inputLine.substringAfter("exit ").toInt()
          exitProcess(status)
        }

        inputLine.startsWith("echo") -> {
          val printable = inputLine.substringAfter("echo ")
          println(printable)
        }

        else -> println("$inputLine: command not found")
      }
    }
  }
}
