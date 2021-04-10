package visitors

import ast.IfNode
import ast.OperatorNode
import ast.VariableNode
import ast.WhileNode
import kotlin.collections.ArrayList

class UsedVariablesVisitor : Visitor {
    val usedVariables = ArrayList<String>()

    override fun visitOperator(node: OperatorNode) {
        node.leftOperand().acceptVisitor(this)
        node.rightOperand().acceptVisitor(this)
    }

    override fun visitVariable(node: VariableNode) {
        usedVariables.add(node.name)
    }

    override fun visitWhile(node: WhileNode) {
        node.condition.acceptVisitor(this)
    }

    override fun visitIf(node: IfNode) {
        node.condition.acceptVisitor(this)
    }
}