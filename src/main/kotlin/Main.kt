import com.google.common.io.Files
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets

fun main() {
    // ポケモンの一覧を取得
//    readCSV("./src/main/resources/pokemon.csv")
    getPokemonList("./src/main/resources/pokemon_test.csv")

    // 先頭の文字ごとにマッピング


    // 取得した一覧でしりとりを開始
    // 濁音、半濁音、拗音は変換する

    // パーティが6匹になったら返却値に追加

}

private fun getPokemonList(filePath: String, doubleQuote: Boolean = false) {
    val enclosure = if (doubleQuote) "\"" else ""
    val charSource = Files.asCharSource(File(filePath), StandardCharsets.UTF_8)

    // ヘッダ行
    val headers = charSource.readFirstLine()?.let {
        parseLine(it, enclosure)
    } ?: throw RuntimeException("Header is empty.")

    // データ行
    charSource.lines()
        .skip(1)
        .map { parseLine(it, enclosure) }
        .map { it.mapIndexed { index, column -> (headers[index] to column) }.toMap() }
        .forEach {
            // 1行ごとの処理
            println(it)
        }
}

private fun parseLine(line: String, enclosure: String): List<String> {
    return line
    .let { StringUtils.removeStart(line, enclosure) }
    .let { StringUtils.removeEnd(it, enclosure) }.split("$enclosure,$enclosure")
}
