package visitors

import ast.*
import java.util.*
import kotlin.collections.HashMap

class AssignmentsVisitor : Visitor {
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

        // Идея в том, чтобы добавлять новые записи в unusedAssignments только при первом проходе по телу цикла,
        // а при втором - только удалять
        if (!cycleWalk) {
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

        // Если условие константное и всегда false, то не нужно проверять эту ветку выполнения программы
        if (detectConstantFalse(node.condition, variablesInCondition)) return

        for (ast in node.children)
            ast.acceptVisitor(this)
    }

    override fun visitWhile(node: WhileNode) {
        val varsVisitor = UsedVariablesVisitor()
        node.acceptVisitor(varsVisitor)
        val variablesInCondition = varsVisitor.usedVariables

        // Если условие константное и всегда false, то не нужно проверять эту ветку исполнения
        if (detectConstantFalse(node.condition, variablesInCondition)) return

        val walkThroughCycleBody: () -> Unit = {
            if (variablesInCondition.isNotEmpty())
                updateUnused(variablesInCondition)

            for (ast in node.children)
                ast.acceptVisitor(this)
        }

        // Суть такая: если нам нужно проверить цикл, мы можем пройтись по его телу один раз по обычному алгоритму,
        // а во второй раз пройтись по нему, только удаляя записи из unusedAssignments
        walkThroughCycleBody()
        cycleWalk = true
        walkThroughCycleBody()
        cycleWalk = false
    }

    private fun detectConstantFalse(node: Node, variablesInCondition: ArrayList<String>): Boolean {
        if (variablesInCondition.isNotEmpty()) {
            updateUnused(variablesInCondition)
            return false
        }

        val constantEvalVisitor = ConstantToBoolEvalVisitor()
        node.acceptVisitor(constantEvalVisitor)
        return constantEvalVisitor.evaluatedValue!!
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
}