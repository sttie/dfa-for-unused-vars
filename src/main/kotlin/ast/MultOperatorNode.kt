package ast

import visitors.Visitor

class MultOperatorNode(
    line: Int
) : OperatorNode("*", line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitMultOperator(this)
    }
}