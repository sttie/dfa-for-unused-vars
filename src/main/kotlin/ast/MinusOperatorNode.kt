package ast

import visitors.Visitor

class MinusOperatorNode(
    line: Int
) : OperatorNode("-", line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitMinusOperator(this)
    }
}