package visitors

class UndeclaredVariableUsingException(
    private val id: String,
    private val line: Int
) : Exception() {
    fun print() {
        println("Detected undeclared variable $id used in $line line!")
    }
}