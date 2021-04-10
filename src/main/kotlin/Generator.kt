import java.io.File
import kotlin.random.Random

object Generator {
    private var instructionsAmount: Int = 0
    private var currentInstruction: Int = 0
    private var indentation: Int = 0
    private lateinit var file: File
    private const val maxDepth: Int = 15
    private var initialDepth: Int = 0

    fun generateProgram(file: File, instructionsAmount: Int) {
        this.file = file
        this.instructionsAmount = instructionsAmount
        initialDepth = Thread.currentThread().stackTrace.size
        currentInstruction = 0
        indentation = 0

        file.writeText("")
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

    // if_statement ::= 'if' expression statement_list 'end'
    private fun generateIfStatement() {
        file.appendText("\t".repeat(indentation))

        indentation++
        file.appendText("if ")
        generateExpression()
        file.appendText("\n")
        generateStatementList()

        indentation--
        file.appendText("\t".repeat(indentation))
        file.appendText("end\n")
    }

    // while_statement ::= 'while' expression statement_list 'en  d'
    private fun generateWhileStatement() {
        file.appendText("\t".repeat(indentation))

        indentation++
        file.appendText("while ")
        generateExpression()
        file.appendText("\n")
        generateStatementList()

        indentation--
        file.appendText("\t".repeat(indentation))
        file.appendText("end\n")
    }

    // assignment_statement ::= variable '=' expression
    private fun generateAssignmentStatement() {
        file.appendText("\t".repeat(indentation))

        generateVariable()
        file.appendText(" = ")
        generateExpression()
        file.appendText("\n")
    }

    // expression ::= compare_term (compare_operator compare_term)*
    private fun generateExpression() {
        generateCompareTerm()

        while (decideToContinue()) {
            file.appendText(listOf(" < ", " > ").random())
            generateCompareTerm()
        }
    }

    // compare_term ::= add_term (add_operator add_term)*
    private fun generateCompareTerm() {
        generateAddTerm()

        while (decideToContinue()) {
            file.appendText(listOf(" + ", " - ").random())
            generateAddTerm()
        }
    }

    // add_term ::= factor_term (mult_operator factor_term)*
    private fun generateAddTerm() {
        generateFactorTerm()

        while (decideToContinue()) {
            file.appendText(listOf(" * ", " / ").random())
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
        file.appendText(('a'..'z').toList().random().toString())
    }

    private fun generateConstant() {
        file.appendText(Random.nextInt(0, 4243).toString())
    }


    private fun appendTextWithIndentation(text: String) {
        file.appendText("${"\t".repeat(indentation)}text")
    }

    private fun decideToContinue(): Boolean
        = Thread.currentThread().stackTrace.size - initialDepth < maxDepth && Random.nextInt(0, 10) in 7..9
}