package visitors

import ast.*

interface Visitor {
    fun visitProgram(node: StatementListNode)           {}
    fun visitAssign(node: AssignNode)                   {}
    fun visitOperator(node: OperatorNode)               {}
    fun visitVariable(node: VariableNode)               {}
    fun visitConstant(node: ConstantNode)               {}
    fun visitIf(node: IfNode)                           {}
    fun visitWhile(node: WhileNode)                     {}

    // Эти функции не пусты потому, что они наследуются от OperatorNode =>
    // они должны по умолчанию отправляться в visitOperatorNode
    fun visitPlusOperator(node: PlusOperatorNode) {
        visitOperator(node)
    }

    fun visitMinusOperator(node: MinusOperatorNode) {
        visitOperator(node)
    }

    fun visitMultOperator(node: MultOperatorNode) {
        visitOperator(node)
    }

    fun visitDivOperator(node: DivOperatorNode) {
        visitOperator(node)
    }

    fun visitLessOperator(node: LessOperatorNode) {
        visitOperator(node)
    }

    fun visitGreaterOperator(node: GreaterOperatorNode) {
        visitOperator(node)
    }
}