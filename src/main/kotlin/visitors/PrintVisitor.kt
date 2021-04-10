package visitors

import ast.*

class PrintVisitor : Visitor {
    override fun visitAssign(node: AssignNode) {
        node.children[0].acceptVisitor(this)
        print(" = ")
        node.children[1].acceptVisitor(this)
    }

    override fun visitOperator(node: OperatorNode) {
        node.children[0].acceptVisitor(this)
        print(" ${node.operator} ")
        node.children[1].acceptVisitor(this)
    }

    override fun visitVariable(node: VariableNode) {
        print(node.name)
    }

    override fun visitConstant(node: ConstantNode) {
        print(node.value)
    }

    override fun visitIf(node: IfNode) {
        TODO()
    }

    override fun visitWhile(node: WhileNode) {
        TODO()
    }
}