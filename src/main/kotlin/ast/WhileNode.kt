package ast

import visitors.Visitor

class WhileNode(val condition: Node) : Node() {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitWhile(this)
    }
}