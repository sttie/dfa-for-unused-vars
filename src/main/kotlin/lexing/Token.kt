package lexing

data class Token(
    val lexeme: String,
    val type: TokenType,
    val line: Int,
    val start: Int,
    val end: Int
) {
    override fun toString(): String {
        return "{$lexeme, $type, $line:[$start, $end]}"
    }
}
