import ast.StatementListNode
import lexing.Lexer
import lexing.TokenType
import parsing.Parser
import parsing.ParserException
import visitors.AssignmentsVisitor
import visitors.PrintVisitor
import java.io.File

fun main(args: Array<String>) {
    var filename: String?
    do {
        print("Enter a path to a file you want to analyse: ")
        filename = readLine()
    } while (filename == null)

    val file = File(filename)
    val parser = Parser(Lexer(file))

    val program: StatementListNode
    try {
        program = parser.parseProgram()
    } catch (ex: ParserException) {
        ex.print()
        return
    }

    val assignmentsVisitor = AssignmentsVisitor()
    program.acceptVisitor(assignmentsVisitor)

    println("Unused variables:")
    for (unusedAssignment in assignmentsVisitor.unusedAssignments) {
        for (unusedAssignNode in unusedAssignment.value) {
            unusedAssignNode.acceptVisitor(PrintVisitor())
            print("\n")
        }
    }
}
