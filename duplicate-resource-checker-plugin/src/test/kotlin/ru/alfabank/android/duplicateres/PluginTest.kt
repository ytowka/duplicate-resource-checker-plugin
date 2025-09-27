package ru.alfabank.android.duplicateres

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.createTempDirectory

class PluginTest {

    @Test
    fun test() {
        val projectDir = createTempDirectory("gradle-plugin-test").toFile()

        deepCopy(javaClass.classLoader.getResource("testProjects/AndroidTestApp").path, projectDir.path)

        println("runner output:")
        val runner = GradleRunner.create()
            .withProjectDir(projectDir)
            .withDebug(true)
            .withPluginClasspath()
            .withGradleVersion("8.11.1")
            //.withArguments("tasks", "--stacktrace")
            .withArguments("checkDebugDuplicateResources")
            .forwardOutput()
            .build()

    }

    private fun deepCopy(
        sourceDirectoryLocation: String,
        destinationDirectoryLocation: String
    ) {
        Files.walk(Paths.get(sourceDirectoryLocation))
            .forEach { source: Path ->
                val destination = Paths.get(
                    destinationDirectoryLocation, source.toString()
                        .substring(sourceDirectoryLocation.length)
                )
                try {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
    }
}