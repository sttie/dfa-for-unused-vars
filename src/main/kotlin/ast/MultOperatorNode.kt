package ast

import visitors.Visitor

class MultOperatorNode : OperatorNode("*") {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitMultOperator(this)
    }
}