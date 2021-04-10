package ast

import visitors.Visitor

class VariableNode(
    val name: String,
) : Node() {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitVariable(this)
    }
}