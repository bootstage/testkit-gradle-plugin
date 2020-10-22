package io.bootstage.testkit.util

import io.bootstage.testkit.gradle.rules.copyFromJar
import org.gradle.internal.impldep.com.google.common.io.Files
import java.io.File
import java.util.jar.JarFile
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class ExtensionFunctionTest {

    @Test
    fun `copy entry from jar`() {
        val cwd = File(System.getProperty("user.dir"))
        val build = File(cwd, "build")
        val libs = File(build, "libs")
        val jar = libs.listFiles { f ->
            f.name.startsWith(cwd.name) && f.extension == "jar"
        }?.first() ?: fail()

        val tmp = Files.createTempDir()
        JarFile(jar).use {
            tmp.copyFromJar(it, "META-INF")
        }
        val files = tmp.walkTopDown()
        files.forEach(::println)
        assertTrue(files.count() > 0)
        tmp.deleteRecursively()
    }

    @Test
    fun `copy all entries from jar`() {
        val cwd = File(System.getProperty("user.dir"))
        val build = File(cwd, "build")
        val libs = File(build, "libs")
        val jar = libs.listFiles { f ->
            f.name.startsWith(cwd.name) && f.extension == "jar"
        }?.first() ?: fail()

        val tmp = Files.createTempDir()
        JarFile(jar).use {
            tmp.copyFromJar(it)
        }
        val files = tmp.walkTopDown()
        files.forEach(::println)
        assertTrue(files.count() > 0)
        tmp.deleteRecursively()
    }

}