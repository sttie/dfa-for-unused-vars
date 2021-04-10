package ast

import visitors.Visitor

class IfNode(val condition: Node) : Node() {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitIf(this)
    }
}