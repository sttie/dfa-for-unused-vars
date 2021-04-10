package ast

import visitors.Visitor

class PlusOperatorNode(
    line: Int
) : OperatorNode("+", line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitPlusOperator(this)
    }
}