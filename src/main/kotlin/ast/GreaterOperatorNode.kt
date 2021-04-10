package ast

import visitors.Visitor

class GreaterOperatorNode : OperatorNode(">") {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitGreaterOperator(this)
    }
}