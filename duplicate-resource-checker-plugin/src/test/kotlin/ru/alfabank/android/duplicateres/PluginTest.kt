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
        val projectDir = createTempDirectory("gradle-plugin-test")
            .toFile()

        //println("output")
        //val testProject = File(javaClass.classLoader.getResource("testProjects/AndroidTestApp")!!.path)

        deepCopy(
            javaClass.classLoader.getResource("testProjects/AndroidTestApp").path, projectDir.path
        )


        //val test1Project = Path("/Users/alfa/AndroidStudioProjects/DuplicateResourceCheckerPlugin/duplicate-resource-checker-plugin/src/test/resources/testProjects/AndroidTestApp1")
        //Files.copy(testProject.toPath(), test1Project, StandardCopyOption.REPLACE_EXISTING)

        println(projectDir.listFiles().map { it.name })

        val runner = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withGradleVersion("8.11.1")
            .withArguments("tasks")
            .build()

        println(runner.output)

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