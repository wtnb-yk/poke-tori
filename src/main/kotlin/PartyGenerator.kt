class PartyGenerator {

    companion object {
        const val VOICED_AND_SEMI_VOICED_KANA = "ガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
        const val UNVOICED_KANA = "カキクケコサシスセソタチツテトハヒフヘホハヒフヘホ"
        const val LARGE_KANA = "アイウエオヤユヨツ"
        const val SMALL_KANA = "ァィゥェォャュョッ"

        fun execute(pokemonNames: List<String>, initial: Char) = searchParties(pokemonNames, initial)

        private fun searchParties(pokemonNames: List<String>, initial: Char): List<List<String>> =
            pokemonNames.indices
                .filter { pokemonNames[it].first() == initial }
                .flatMap { start -> search(listOf(pokemonNames[start]), setOf(start), pokemonNames) }

        private fun search(path: List<String>, used: Set<Int>, pokemonNames: List<String>): List<List<String>> {
            if (path.size == 6) return listOf(path)
            val lastStr = retrieveLastCharacter(path.last())
            val current = convertToUnvoicedConsonant(lastStr)

            return pokemonNames.indices.filter { index ->
                index !in used && convertToUnvoicedConsonant(pokemonNames[index].first()) == current
            }.flatMap { next ->
                val newPath = path + pokemonNames[next]
                val newUsed = used + next
                search(newPath, newUsed, pokemonNames)
            }
        }

        private fun retrieveLastCharacter(str: String): Char = str.trimEnd('ー').last()

        private fun convertToUnvoicedConsonant(kana: Char) =
            when {
                isVoicedOrSemiVoicedKana(kana) -> getUnvoicedKana(kana)
                isSmallKana(kana) -> getCorrespondingLargeKana(kana)
                else -> kana
            }

        private fun isVoicedOrSemiVoicedKana(kana: Char): Boolean = kana in VOICED_AND_SEMI_VOICED_KANA
        private fun isSmallKana(kana: Char): Boolean = kana in SMALL_KANA
        private fun getUnvoicedKana(kana: Char): Char = VOICED_AND_SEMI_VOICED_KANA.indexOf(kana).let { UNVOICED_KANA[it] }
        private fun getCorrespondingLargeKana(kana: Char): Char = SMALL_KANA.indexOf(kana).let { LARGE_KANA[it] }

    }
}