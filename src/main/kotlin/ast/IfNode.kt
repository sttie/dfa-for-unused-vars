package ast

import visitors.Visitor

class IfNode(
    val condition: Node,
    line: Int
) : Node(line = line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitIf(this)
    }
}