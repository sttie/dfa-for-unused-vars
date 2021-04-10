package ast

import visitors.Visitor

class GreaterOperatorNode(
    line: Int
) : OperatorNode(">", line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitGreaterOperator(this)
    }
}