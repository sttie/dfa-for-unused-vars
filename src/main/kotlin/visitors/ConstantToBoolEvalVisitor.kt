package visitors

import ast.ConstantNode
import ast.OperatorNode
import java.util.Stack

class ConstantToBoolEvalVisitor : Visitor {
    var evaluatedValue: Boolean? = null
    private val evaluatesStack = Stack<Int>()

    override fun visitOperator(node: OperatorNode) {
        node.leftOperand().acceptVisitor(this)
        node.rightOperand().acceptVisitor(this)

        evaluatedValue = when (node.operator) {
            "+" -> evaluatesStack.pop() + evaluatesStack.pop()
            "-" -> evaluatesStack.pop() - evaluatesStack.pop()
            "*" -> evaluatesStack.pop() * evaluatesStack.pop()
            "/" -> evaluatesStack.pop() / evaluatesStack.pop()
            ">" -> evaluatesStack.pop() > evaluatesStack.pop()
            "<" -> evaluatesStack.pop() < evaluatesStack.pop()
            else -> throw UnsupportedOperationException("operator ${node.operator} is unsupported")
        } != 0
    }

    override fun visitConstant(node: ConstantNode) {
        evaluatesStack.add(node.value)
    }
}