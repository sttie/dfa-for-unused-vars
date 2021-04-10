package ast

import visitors.Visitor

class PlusOperatorNode : OperatorNode("+") {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitPlusOperator(this)
    }
}