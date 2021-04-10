package visitors

import ast.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AssignmentsVisitor : Visitor {
    var declaredVariables = HashSet<String>()
    var unusedAssignments = HashMap<String, Stack<AssignNode>>()
        private set
    private var cycleWalk: Boolean = false

    override fun visitProgram(node: StatementListNode) {
        for (ast in node.children)
            ast.acceptVisitor(this)
    }

    override fun visitAssign(node: AssignNode) {
        val leftOperandName = node.getLeftOperandName()
        val rightOperand = node.getRightOperand()

        // Получаем список используемых переменных справа от =
        val varsVisitor = UsedVariablesVisitor()
        rightOperand.acceptVisitor(varsVisitor)
        val variablesInExpr = varsVisitor.usedVariables

        throwIfWasNotDeclared(variablesInExpr, node.line)

        // Идея в том, чтобы добавлять новые записи в unusedAssignments только при первом проходе по телу цикла,
        // а при втором - только удалять
        if (!cycleWalk) {
            declaredVariables.add(leftOperandName)
            if (!unusedAssignments.containsKey(leftOperandName))
                unusedAssignments[leftOperandName] = Stack()
            unusedAssignments[leftOperandName]!!.add(node)
        }

        if (variablesInExpr.isNotEmpty())
            updateUnused(variablesInExpr, leftOperandName)
    }

    override fun visitIf(node: IfNode) {
        val varsVisitor = UsedVariablesVisitor()
        node.acceptVisitor(varsVisitor)
        val variablesInCondition = varsVisitor.usedVariables

        throwIfWasNotDeclared(variablesInCondition, node.line)

        // Если условие константное и всегда false, то не нужно проверять эту ветку выполнения программы
        if (!detectConstantFalse(node.condition, variablesInCondition)) return

        for (ast in node.children)
            ast.acceptVisitor(this)
    }

    override fun visitWhile(node: WhileNode) {
        val varsVisitor = UsedVariablesVisitor()
        node.acceptVisitor(varsVisitor)
        val variablesInCondition = varsVisitor.usedVariables

        throwIfWasNotDeclared(variablesInCondition, node.line)

        // Если условие константное и всегда false, то не нужно проверять эту ветку выполнения программы
        if (!detectConstantFalse(node.condition, variablesInCondition)) return

        val walkThroughCycleBody: () -> Unit = {
            if (variablesInCondition.isNotEmpty())
                updateUnused(variablesInCondition)

            for (ast in node.children)
                ast.acceptVisitor(this)
        }

        walkThroughCycleBody()
        cycleWalk = true
        walkThroughCycleBody()
        cycleWalk = false
    }

    private fun detectConstantFalse(node: Node, variablesInCondition: ArrayList<String>): Boolean {
        if (variablesInCondition.isNotEmpty()) {
            updateUnused(variablesInCondition)
            return true
        }

        val constantEvalVisitor = ConstantToBoolEvalVisitor()
        node.acceptVisitor(constantEvalVisitor)
        val a = constantEvalVisitor.getEvaluatedValue()
        return a
    }

    private fun updateUnused(variables: ArrayList<String>, leftOperandName: String = "") {
        variables.forEach {
            if (it != leftOperandName && unusedAssignments.containsKey(it)) {
                unusedAssignments[it]!!.pop()
                if (unusedAssignments[it]!!.isEmpty())
                    unusedAssignments.remove(it)
            }
        }
    }

    private fun throwIfWasNotDeclared(variablesIds: ArrayList<String>, line: Int) {
        for (id in variablesIds)
            if (!declaredVariables.contains(id))
                throw UndeclaredVariableUsingException(id, line)
    }
}
