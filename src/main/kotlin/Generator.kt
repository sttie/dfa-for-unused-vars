import java.io.File
import kotlin.random.Random


class Generator(
    private val file: File,
    private val instructionsAmount: Int = 15
) {
    private var currentInstruction: Int = 0

    fun generateCode() {
        // Очистка кода
        file.writeText("")
        currentInstruction = 0
        generateStatementList()
    }

    private fun generateStatementList() {
        while (currentInstruction++ < instructionsAmount) {
            when (Random.nextInt(0, 20)) {
                0 -> generateIfStatement()
                1 -> generateWhileStatement()
                in 2..19 -> generateAssignmentStatement()
            }
        }
    }

    private fun generateIfStatement() {
        file.appendText("if ")
        generateExpression()
        file.appendText("\n")
        generateStatementList()
        file.appendText("end\n")
    }

    private fun generateWhileStatement() {
        file.appendText("while ")
        generateExpression()
        file.appendText("\n")
        generateStatementList()
        file.appendText("end\n")
    }

    private fun generateAssignmentStatement() {
        generateVariable()
        file.appendText(" = ")
        generateExpression()
        file.appendText("\n")
    }

    // expression ::= compare_term (compare_operator compare_term)*
    private fun generateExpression() {
        generateCompareTerm()
        while (decideToContinue()) {
            file.appendText(listOf("<", ">")[Random.nextInt(0, 2)])
            generateCompareTerm()
        }
    }

    // compare_term ::= add_term (add_operator add_term)*
    private fun generateCompareTerm() {
        generateAddTerm()
        while (decideToContinue()) {
            file.appendText(listOf("+", "-")[Random.nextInt(0, 2)])
            generateAddTerm()
        }
    }

    // add_term ::= factor_term (mult_operator factor_term)*
    private fun generateAddTerm() {
        generateFactorTerm()
        while (decideToContinue()) {
            file.appendText(listOf("*", "/")[Random.nextInt(0, 2)])
            generateFactorTerm()
        }
    }

    // factor_term ::= '(' expression ')' | variable | constant
    private fun generateFactorTerm() {
        // Лично я хочу, чтобы вероятность '(' expression ')' была меньше вероятности остальных продукций
        when (Random.nextInt(0, 12)) {
            in 0..1 -> {
                file.appendText("(")
                generateExpression()
                file.appendText(")")
            }
            in 2..6 -> generateVariable()
            in 7..11 -> generateConstant()
        }
    }

    private fun generateVariable() {
        file.appendText(('a'..'z').toList()[Random.nextInt(0, 26)].toString())
    }

    private fun generateConstant() {
        file.appendText(Random.nextInt(0, 4243).toString())
    }

    private fun decideToContinue(): Boolean = Random.nextInt(0, 10) in 7..9
}