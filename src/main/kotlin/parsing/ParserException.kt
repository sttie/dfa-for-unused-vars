package parsing

import lexing.Token
import lexing.TokenType

class ParserException(
    message: String,
    private val token: Token
) : Exception(message) {
    fun print() {
        println("$message")

        val outLexeme: String = when (token.type) {
            TokenType.ENDFILE -> "end of file"
            TokenType.NEWLINE -> "newline"
            else              -> token.lexeme
        }
        println("${token.line}::${token.start}: $outLexeme")
    }
}