package io.bootstage.testkit.gradle.rules

import org.junit.rules.TemporaryFolder
import java.io.File
import java.net.JarURLConnection
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Copy resource with [name] to [TemporaryFolder] root
 */
fun TemporaryFolder.copyFromResource(name: String, target: String = name.replace('/', File.separatorChar)) {
    val out: File by lazy {
        File(root, target)
    }

    ClassLoader.getSystemResources(name).toList().forEach { url ->
        when (url.protocol) {
            "file" -> out.copyFrom(File(url.file))
            "jar" -> (url.openConnection() as JarURLConnection).jarFile.use {
                out.copyFromJar(it, url.file.substringAfterLast("!/"))
            }
            else -> url.openStream().buffered().use { src ->
                out.outputStream().use { dest ->
                    src.copyTo(dest)
                }
            }
        }
    }
}

/**
 * Copy [src] to this file path
 */
internal fun File.copyFrom(src: File) {
    if (src.isDirectory) {
        src.copyRecursively(this, true)
    } else {
        src.copyTo(this, true)
    }
}

/**
 * Copy files under [path] from [src] to this file location
 */
internal fun File.copyFromJar(src: JarFile, path: String = "/") {
    val filter: JarEntryFilter = if (path == "/" || path.isEmpty()) {
        { true }
    } else {
        val entry = src.getJarEntry(path)
        val n = entry.name.length
        { it.name.startsWith(entry.name) && (entry.name.endsWith('/') || it.name[n] == '/') }
    }
    src.entries().asSequence().filter(filter).forEach {
        File(this, it.name).copyFromJarEntry(src, it)
    }
}

/**
 * Copy [entry] from [jar] to this file location
 */
internal fun File.copyFromJarEntry(jar: JarFile, entry: JarEntry) {
    if (entry.isDirectory) {
        mkdirs()
    } else {
        parentFile.mkdirs()
        jar.getInputStream(entry).buffered().use { src ->
            outputStream().buffered().use { dest ->
                src.copyTo(dest)
            }
        }
    }
}

private typealias JarEntryFilter = (JarEntry) -> Boolean
