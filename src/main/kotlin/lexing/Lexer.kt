package lexing

import java.io.File

class Lexer(file: File) {
    private val lines: List<String> = file.readLines()
    private var currentIndex: Int = 0
    private var currentLine: Int = 0
    private val automata: Automata = Automata(initStartState())

    fun nextToken(): Token {
        val token: Token

        when {
            currentLine >= lines.size -> token = Token("", TokenType.ENDFILE, currentLine + 1, 0, 0)
            currentIndex >= lines[currentLine].length -> {
                token = Token("", TokenType.NEWLINE, currentLine + 1, currentIndex - 1, currentIndex)
                currentLine++
                currentIndex = 0
            }
            else -> {
                token = automata.scan(lines[currentLine], currentIndex, currentLine + 1)
                currentIndex = token.end
            }
        }

        return token
    }

    fun lookNextToken(): Token {
        return if (currentLine < lines.size) automata.scan(lines[currentLine], currentIndex, currentLine + 1)
        else Token("", TokenType.ENDFILE, currentLine, 0, 0)
    }

    fun getLine(index: Int): String = lines[index]

    private fun initStartState(): State {
        val startState = State()

        val idState = State(TokenType.ID)
        idState.addTransition('a'..'z', idState)

        val integerState = State(TokenType.INTEGER)
        integerState.addTransition('0'..'9', integerState)

        val leftBracketState = State(TokenType.LBRACKET)
        val rightBracketState = State(TokenType.RBRACKET)
        val plusState = State(TokenType.PLUS)
        val minusState = State(TokenType.MINUS)
        val multState = State(TokenType.MULT)
        val divState = State(TokenType.DIV)
        val assignState = State(TokenType.ASSIGN)
        val lessState = State(TokenType.LESS)
        val greaterState = State(TokenType.GREATER)

        startState.addTransition('(', leftBracketState)
        startState.addTransition(')', rightBracketState)
        startState.addTransition('+', plusState)
        startState.addTransition('-', minusState)
        startState.addTransition('*', multState)
        startState.addTransition('/', divState)
        startState.addTransition('<', lessState)
        startState.addTransition('>', greaterState)
        startState.addTransition('=', assignState)

        startState.addTransition('a'..'z', idState)
        startState.addTransition('0'..'9', integerState)

        startState.addTransitionToSkipState(' ')
        startState.addTransitionToSkipState('\t')

        return startState
    }
}