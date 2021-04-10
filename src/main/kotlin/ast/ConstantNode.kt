package ast

import visitors.Visitor

class ConstantNode(val value: Int) : Node() {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitConstant(this)
    }
}