package ast

import visitors.Visitor

class VariableNode(
    val name: String,
    line: Int
) : Node(line = line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitVariable(this)
    }
}