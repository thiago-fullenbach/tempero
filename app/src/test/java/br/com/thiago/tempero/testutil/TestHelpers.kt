package br.com.thiago.tempero.testutil

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

/** Executa [block] e devolve a exceção do tipo [T] lançada; falha se nada for lançado ou se o tipo for outro. */
suspend inline fun <reified T : Throwable> assertFails(block: suspend () -> Unit): T {
    try {
        block()
    } catch (e: Throwable) {
        if (e is T) return e
        throw AssertionError("Esperava ${T::class.java.simpleName}, mas veio ${e::class.java.name}: $e", e)
    }
    throw AssertionError("Esperava ${T::class.java.simpleName}, mas nada foi lançado")
}

/** Espera (tempo real) o StateFlow atingir um valor que satisfaça [predicate]; usado quando o Room responde em outra thread. */
suspend fun <T> StateFlow<T>.awaitValue(timeoutMs: Long = 5_000, predicate: (T) -> Boolean): T =
    withTimeout(timeoutMs) { first(predicate) }

/** Lê um arquivo de `src/test/resources/json`. */
fun readFixture(name: String): String =
    requireNotNull(object {}.javaClass.classLoader?.getResource("json/$name")) { "Fixture $name não encontrada" }
        .readText()
