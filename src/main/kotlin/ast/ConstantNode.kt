package ast

import visitors.Visitor

class ConstantNode(
    val value: Int,
    line: Int
) : Node(line = line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitConstant(this)
    }
}