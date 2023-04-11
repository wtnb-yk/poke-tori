import com.opencsv.CSVWriter
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    println("カタカナ１文字を入力")
    val input = readln().first()
    println("頭文字: $input")

    if (!isKatakana(input)) {
        println("カタカナ１文字以外が入力されたため処理終了")
        return
    }

    val pokemonNames = getPokemonList()
    val parties = PartyGenerator.execute(pokemonNames, input)
    println("検索結果: ${parties.size}件")
    writeCSV(parties)
}

private fun isKatakana(char: Char): Boolean =
    when (char) {
         in 'ァ'..'ヶ' -> true
         else -> false
     }

private fun getPokemonList() =
    File("./src/main/resources/pokemon.csv")
        .readLines()
        .drop(1)
        .map { it }

private fun writeCSV(parties: List<List<String>>) {
    val formatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"))
    val csvWriter = CSVWriter(File("/tmp/${formatted}.csv").writer(StandardCharsets.UTF_8))
    csvWriter.use { writer -> parties.forEach { writer.writeNext(it.toTypedArray()) } }
}