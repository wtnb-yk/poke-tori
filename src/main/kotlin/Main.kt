import com.google.common.io.Files
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import java.util.stream.Stream

fun main() {
    // ポケモンの一覧を取得
    val pokemonStream = getPokemonList("./src/main/resources/pokemon.csv")

    // 先頭の文字ごとにマッピング
    val pokemonKanaMap = pokemonStream
        .flatMap { it.entries.stream() }
        //// TODO: 濁音、半濁音、拗音は変換する
        .collect(Collectors.groupingBy { convertToUnvoicedConsonant(it.value.substring(0, 1)) })

    // 取得した一覧でしりとりを開始
    var parties = emptyArray<Array<String>>()
    pokemonKanaMap
        .entries
        .forEach{
            val pokemonGroupingByKane = it.value
            for ((_, name) in pokemonGroupingByKane) {
                var party = emptyArray<String>()

                // 1匹目をパーティに追加
                party += name

                // TODO: 2~6匹目をしりとりで探索
                //// 前のポケモンの末尾を取得
                val lastKana = name.substring(name.lastIndex)

                //// 濁音、半濁音、拗音は変換する
                val pokemonGroupingByNextKana = pokemonKanaMap[convertToUnvoicedConsonant(lastKana)]
                if (pokemonGroupingByNextKana != null) {
                    for ((_, name2) in pokemonGroupingByNextKana) {
                        party += name2
                    }
                }

                // パーティが6匹になったら返却値に追加
                if (party.size == 6) {
                    parties += party
                }
            }
        }
    parties.forEach {
        if (it.isNotEmpty()) {
            println(it.contentToString())
        }
    }
}

private fun getPokemonList(filePath: String, doubleQuote: Boolean = false): Stream<Map<String, String>> {
    val enclosure = if (doubleQuote) "\"" else ""
    val charSource = Files.asCharSource(File(filePath), StandardCharsets.UTF_8)

    // ヘッダ行
    val headers = charSource.readFirstLine()?.let {
        parseLine(it, enclosure)
    } ?: throw RuntimeException("Header is empty.")

    // データ行
    return charSource.lines()
        .skip(1)
        .map { parseLine(it, enclosure) }
        .map { it.mapIndexed { index, column -> (headers[index] to column) }.toMap() }
}

private fun parseLine(line: String, enclosure: String): List<String> {
    return line
        .let { StringUtils.removeStart(line, enclosure) }
        .let { StringUtils.removeEnd(it, enclosure) }.split("$enclosure,$enclosure")
}
private fun convertToUnvoicedConsonant(kana: String): String {
    // TODO: implementation
    return kana
}
