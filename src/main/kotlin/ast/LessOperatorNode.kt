package ast

import visitors.Visitor

class LessOperatorNode : OperatorNode("<") {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitLessOperator(this)
    }
}