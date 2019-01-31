import java.io.File

class FileSync(private val sourcePath: String, private val destinationPath: String) {

    fun sync() {
        val sourceFiles = getFiles(sourcePath)
        val destinationFiles = getFiles(destinationPath)

        val difference = findDifference(sourceFiles, destinationFiles)

        difference.items
                .sortedWith(compareBy({ it.difference }, { it.file.isDirectory }, { it.file.absolutePath }))
                .forEach(::println)
    }

    private fun findDifference(source: DirectoryInformation, destination: DirectoryInformation): DirectoryDifference {
        return DirectoryDifference(source, destination)
    }

    private fun getFiles(path: String): DirectoryInformation {
        return DirectoryInformation(path)
    }
}

class FileDifference(val file: File, val difference: Difference) {
    private fun fileType(): String {
        return if (this.file.isDirectory) "d" else "f"
    }

    override fun toString(): String {
        return "${this.difference}\t${fileType()}\t${this.file.absolutePath}"
    }
}

enum class Difference {
    ShouldBeRemoved,
    ShouldBeCreated
}

class DirectoryDifference(source: DirectoryInformation, destination: DirectoryInformation) {
    var items: ArrayList<FileDifference> = ArrayList()
        private set

    init {
        val sourceFiles = source.files.map { f -> f.canonicalPath.removePrefix(source.path) }
        val destinationFiles = destination.files.map { f -> f.canonicalPath.removePrefix(destination.path) }

        sourceFiles.subtract(destinationFiles).map { file -> add(source.path, file, Difference.ShouldBeCreated) }
        destinationFiles.subtract(sourceFiles).map { file -> add(destination.path, file, Difference.ShouldBeRemoved) }
    }

    private fun add(path: String, file: String, difference: Difference) {
        items.add(FileDifference(File(path + file), difference))
    }
}

class DirectoryInformation(internal var path: String) {
    var files: ArrayList<File> = arrayListOf()
        private set

    init {
        File(this.path).walkTopDown().forEach { x -> files.add(x) }
    }
}