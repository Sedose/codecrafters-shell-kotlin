package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.Scanner

@SpringBootApplication
class Main

fun main(args: Array<String>) {
  runApplication<Main>(*args)
}
