package br.com.thiago.tempero.core.data.mapper

import br.com.thiago.tempero.core.model.MealStep

/** Tamanho máximo (em caracteres) ao juntar frases num mesmo passo. Uma frase maior que isso fica sozinha. */
private const val MAX_STEP_LENGTH = 250

/**
 * Quebra o texto de preparo em passos. O texto da API não tem formato fixo, então:
 * 1. descarta linhas que são só rótulo de passo ("STEP 1", "2.", "Step 3:"), já que a UI numera;
 * 2. cola títulos terminados em ":" ("Cook the filling:") ao texto que vem depois; um título
 *    no fim do texto, sem nada depois, é descartado;
 * 3. divide cada linha em frases e junta de novo até [MAX_STEP_LENGTH], para que um parágrafo
 *    não vire um passo gigante nem cada frase vire um passo. Nunca junta linhas diferentes.
 *
 * Nenhuma palavra é alterada: o resultado só tem menos rótulos e espaços normalizados.
 * Reaplicar sobre passos já gerados dá o mesmo resultado; por isso também normaliza os
 * favoritos salvos antes desta regra existir.
 */
internal fun String?.toMealSteps(): List<MealStep> =
    orEmpty()
        // Cada linha do texto original é um bloco; os blocos nunca se misturam nas etapas seguintes.
        .lines()
        .map { it.trim() }
        // Etapa 1: tira linhas vazias e rótulos soltos ("STEP 1", "3.").
        .filter { it.isNotEmpty() && !STEP_MARKER.matches(it) }
        // Etapa 2: títulos terminados em ":" passam a fazer parte da linha seguinte.
        .mergeHeadings()
        // Etapas 3 e 4: quebra cada bloco em frases e junta até o limite de tamanho.
        .flatMap { it.splitSentences().groupUpTo(MAX_STEP_LENGTH) }
        .map { MealStep(it) }

/**
 * Rótulo solto de passo: "STEP 1", "Step 2:", "3", "4.". A linha inteira precisa casar (`matches`),
 * então "Preheat to 350." não é rótulo. Número sozinho só vale com 1 ou 2 dígitos, para não
 * descartar uma linha como "350".
 */
private val STEP_MARKER = Regex("""(step\s*\d+|\d{1,2})\s*[.:)]?""", RegexOption.IGNORE_CASE)

/**
 * Ponto onde uma frase termina e outra começa. O que casa é só o espaço entre elas, então o
 * `split` remove o espaço e mantém a pontuação. Três condições:
 * - antes do espaço: ".", "!" ou "?", talvez seguido de fecha-aspas ou parêntese (`(?<=...)`);
 * - antes do espaço não pode ser reticências ".." (`(?<!\.\.)`), que costumam continuar a frase;
 * - depois do espaço: letra maiúscula (`(?=\p{Lu})`). É isso que evita cortar em "3.5 tbsp"
 *   (sem espaço depois do ponto) e em "approx. 1cm" (minúscula ou número depois).
 */
private val SENTENCE_BREAK = Regex("""(?<=[.!?][)"'’”]{0,2})(?<!\.\.)\s+(?=\p{Lu})""")

/**
 * Abreviações que terminam em ponto mas não encerram a frase ("e.g. Cut"). Como seguem o padrão
 * "ponto + espaço + maiúscula", o [SENTENCE_BREAK] corta ali por engano; esta regex detecta
 * o erro para [splitSentences] desfazer o corte.
 */
private val ABBREVIATION_END = Regex("""\b(e\.g|i\.e|approx|vs)\.$""", RegexOption.IGNORE_CASE)

/**
 * Cola cada título (linha terminada em ":") à linha seguinte. Vários títulos seguidos se
 * acumulam. Um título que sobra no fim, sem linha depois dele, não entra no resultado.
 */
private fun List<String>.mergeHeadings(): List<String> {
    val merged = mutableListOf<String>()
    // Título (ou títulos) esperando a próxima linha; null quando não há nenhum pendente.
    var pending: String? = null
    for (line in this) {
        val text = pending?.let { "$it $line" } ?: line
        if (line.endsWith(":")) {
            // Esta linha também é título: segue acumulando até chegar uma linha de conteúdo.
            pending = text
        } else {
            merged += text
            pending = null
        }
    }
    return merged
}

/** Divide um bloco em frases usando [SENTENCE_BREAK] e desfaz os cortes feitos após abreviações. */
private fun String.splitSentences(): List<String> {
    val sentences = mutableListOf<String>()
    for (piece in split(SENTENCE_BREAK)) {
        val previous = sentences.lastOrNull()
        if (previous != null && ABBREVIATION_END.containsMatchIn(previous)) {
            // A frase anterior terminou numa abreviação ("e.g."): este pedaço é continuação dela.
            sentences[sentences.lastIndex] = "$previous $piece"
        } else {
            sentences += piece
        }
    }
    return sentences
}

/**
 * Junta frases consecutivas num mesmo passo enquanto o total (com um espaço entre elas) não
 * passar de [maxLength]. Uma frase que sozinha já passa do limite fica como um passo só dela;
 * nunca é cortada no meio.
 */
private fun List<String>.groupUpTo(maxLength: Int): List<String> {
    val groups = mutableListOf<String>()
    for (sentence in this) {
        val last = groups.lastOrNull()
        if (last != null && last.length + 1 + sentence.length <= maxLength) {
            groups[groups.lastIndex] = "$last $sentence"
        } else {
            groups += sentence
        }
    }
    return groups
}
