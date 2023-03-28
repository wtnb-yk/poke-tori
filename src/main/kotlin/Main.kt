import com.google.common.io.Files
import com.opencsv.CSVWriter
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import java.util.stream.Stream

fun main() {
    // カタカナを入力
    println("カタカナ１文字を入力")
    val input = readln()
    println("頭文字: $input")

    // カタカナ１文字以外が入力されたら処理終了
    if (!isKatakanaOneChar(input)) {
        println("カタカナ１文字以外が入力されたため処理終了")
        return
    }

    val initial = input.toCharArray()[0]

    // ポケモンの一覧を取得
    val pokemonList = getPokemonList("./src/main/resources/pokemon.csv")

    // 名前のlistを作成
    val pokemonNames = pokemonList
        .flatMap { it.entries.stream() }
        .filter { it.key == "name" }
        .map { it.value }
        .collect(Collectors.toList())

    // 取得した一覧でしりとりを開始
    val parties = mutableListOf<List<String>>()

    for (start in pokemonNames.indices) {
        if (pokemonNames[start].first() != initial) continue

        val used = mutableSetOf(start)
        val path = mutableListOf(pokemonNames[start])
        var lastStr = retrieveLastCharacter(pokemonNames[start])
        var current = convertToUnvoicedConsonant(lastStr)

        fun search() {
            if (path.size == 6) {
                parties.add(path.toList())
                return
            }

            for (next in pokemonNames.indices) {
                if (next !in used
                    && convertToUnvoicedConsonant(pokemonNames[next].first()) == current) {
                    used.add(next)
                    path.add(pokemonNames[next])
                    lastStr = retrieveLastCharacter(pokemonNames[next])
                    current = convertToUnvoicedConsonant(lastStr)
                    search()
                    used.remove(next)
                    path.removeAt(path.lastIndex)
                    lastStr = retrieveLastCharacter(path.last())
                    current = convertToUnvoicedConsonant(lastStr)
                }
            }
        }

        search()
    }

    println("検索結果: ${parties.size}件")
    writeCSV(parties)
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
private fun convertToUnvoicedConsonant(kana: Char): Char {
    val t = "アアイイウウエエオオカカキキククケケココササシシススセセソソタタチチツツツテテトトナニヌネノハハハヒヒヒフフフヘヘヘホホホマミムメモヤヤユユヨヨラリルレロワワヰヱヲン"
    // ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲン

    return if (kana in 'ァ'..'ン') {
        t[kana.code - 'ァ'.code]
    } else {
        kana
    }
}
private fun retrieveLastCharacter(str: String): Char {
    val lastStrIndex = str.lastIndex
    return if (lastStrIndex > 0 && str[lastStrIndex] == 'ー') {
        str[lastStrIndex - 1]
    } else {
        str[lastStrIndex]
    }
}

private fun isKatakanaOneChar(str: String): Boolean {
    return str.matches(Regex("[ァ-ヶ]"))
}

private fun writeCSV(parties: MutableList<List<String>>) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val now = LocalDateTime.now()
    val formatted = now.format(formatter)

    val filePath = "./src/main/resources/${formatted}.csv"
    val csvWriter = CSVWriter(File(filePath).writer())

    for (line in parties) {
        csvWriter.writeNext(line.toTypedArray())
    }

    csvWriter.close()
}
