package lexing

class LexerException(
    message: String,
    private val line: Int,
) : Exception(message) {
    fun print() {
        println("$line line: $message")
    }
}