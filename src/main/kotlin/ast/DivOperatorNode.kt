package ast

import visitors.Visitor

class DivOperatorNode : OperatorNode("/") {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitDivOperator(this)
    }
}