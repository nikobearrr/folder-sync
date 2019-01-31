import java.io.File

fun readDirectory(dirPath: String): ArrayList<File>? {
    val walk = File(dirPath).walkTopDown()

    return null
}