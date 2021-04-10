package ast

import visitors.Visitor

class LessOperatorNode(
    line: Int
) : OperatorNode("<", line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitLessOperator(this)
    }
}