package ast

import visitors.Visitor

class MinusOperatorNode : OperatorNode("-") {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitMinusOperator(this)
    }
}