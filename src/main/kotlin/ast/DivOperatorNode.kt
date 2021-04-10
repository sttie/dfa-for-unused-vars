package ast

import visitors.Visitor

class DivOperatorNode(
    line: Int
) : OperatorNode("/", line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitDivOperator(this)
    }
}