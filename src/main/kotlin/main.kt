import ast.StatementListNode
import lexing.Lexer
import lexing.LexerException
import parsing.Parser
import parsing.ParserException
import visitors.AssignmentsVisitor
import visitors.PrintVisitor
import visitors.UndeclaredVariableUsingException
import java.io.File


fun main(args: Array<String>) {
    var file: File
    do {
        print("Enter a path to a file you want to analyse: ")
        val filename = readLine() ?: ""
        file = File(filename)

        if (!file.exists())
            println("Can't find $filename, try again")
    } while (!file.exists())

    startAnalyse(file)
}

fun startAnalyse(file: File) {
    val parser = Parser(Lexer(file))

    val program: StatementListNode
    try {
        program = parser.parseProgram()
    } catch (ex: ParserException) {
        ex.print()
        return
    } catch (ex: LexerException) {
        ex.print()
        return
    }

    val assignmentsVisitor = AssignmentsVisitor()

    try {
        program.acceptVisitor(assignmentsVisitor)
    } catch (ex: UndeclaredVariableUsingException) {
        ex.print()
        return
    }

    println("\nUnused variables:")
    for (unusedAssignment in assignmentsVisitor.unusedAssignments) {
        for (unusedAssignNode in unusedAssignment.value) {
            unusedAssignNode.acceptVisitor(PrintVisitor())
            print("\n")
        }
    }
}