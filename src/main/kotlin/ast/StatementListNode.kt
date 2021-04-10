package ast

import visitors.Visitor

class StatementListNode(
    line: Int
) : Node(line = line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitProgram(this)
    }
}