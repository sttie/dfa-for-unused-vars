package ast

import visitors.Visitor

class WhileNode(
    val condition: Node,
    line: Int
) : Node(line = line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitWhile(this)
    }
}