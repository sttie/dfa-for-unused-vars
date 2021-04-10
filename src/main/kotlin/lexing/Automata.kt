package lexing

class Automata(private val startState: State) {
    private var currentString = StringBuilder()

    fun scan(line: String, start: Int, lineno: Int): Token {
        var actualStart = start
        var index = start
        var currentState: State = startState

        while (index < line.length && currentState.transit(line[index]) != null) {
            currentState = currentState.transit(line[index])!!

            if (currentState.isSkipState()) {
                currentState = startState
                actualStart++
            }
            else
                currentString.append(line[index])

            index++
        }

        if (currentState.type == TokenType.EMPTY)
            throw LexerException("Unknown token: ${line[index]}", lineno)

        val token: Token =
            if (TokenType.values().firstOrNull { it.name == currentString.toString().toUpperCase() } != null) {
                Token(
                    currentString.toString(),
                    TokenType.valueOf(currentString.toString().toUpperCase()),
                    lineno, actualStart + 1, index
                )
            } else {
                Token(currentString.toString(), currentState.type, lineno, actualStart + 1, index)
            }

        currentString.clear()
        return token
    }
}