package ast

import visitors.Visitor

class StatementListNode : Node() {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitProgram(this)
    }
}