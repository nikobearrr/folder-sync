fun main(args: Array<String>) {

    val source = "C:\\test"
    val destination = "C:\\test2"

    val fileSync = FileSync(source, destination)
    var report = fileSync.sync()
}