package visitors

import ast.ConstantNode
import ast.OperatorNode
import java.util.Stack

class ConstantToBoolEvalVisitor : Visitor {
    private val evaluatesStack = Stack<Int>()

    override fun visitOperator(node: OperatorNode) {
        node.leftOperand().acceptVisitor(this)
        node.rightOperand().acceptVisitor(this)

        val right = evaluatesStack.pop()
        val left = evaluatesStack.pop()
        evaluatesStack.push(when (node.operator) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            ">" -> if (left > right) 1 else 0
            "<" -> if (left < right) 1 else 0
            else -> throw UnsupportedOperationException("operator ${node.operator} is unsupported")
        })
    }

    override fun visitConstant(node: ConstantNode) {
        evaluatesStack.add(node.value)
    }

    fun getEvaluatedValue(): Boolean
        = evaluatesStack.pop() != 0
}