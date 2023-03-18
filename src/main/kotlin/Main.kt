import com.google.common.io.Files as gFiles
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

fun main() {
    // ポケモンの一覧を取得
    readCSV()

    // 取得した一覧でしりとりを開始
    // 濁音、半濁音、拗音は変換する

    // パーティが6匹になったら返却値に追加

}

private fun readCSV() {
    gFiles.asCharSource(File("./src/main/resources/pokemon.csv"), StandardCharsets.UTF_8)
        .lines()
        .map { it.split(",") }
        .forEach {
            // 1行ごとの処理
            println(it)
        }
}
