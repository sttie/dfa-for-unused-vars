package lexing

class State(
    val type: TokenType = TokenType.EMPTY,
    private var transitions: LinkedHashMap<Char, State> = LinkedHashMap()
) {
    companion object {
        private val skipState: State = State()
    }

    fun addTransition(c: Char, newState: State) {
        transitions[c] = newState
    }

    fun addTransition(range: CharRange, newState: State) {
        for (ch in range)
            transitions[ch] = newState
    }

    fun transit(c: Char?): State? {
        return transitions[c]
    }

    fun isSkipState(): Boolean = this == skipState

    fun addTransitionToSkipState(c: Char) {
        transitions[c] = skipState
    }

    fun addTransitionToSkipState(range: CharRange) {
        for (ch in range)
            transitions[ch] = skipState
    }
}