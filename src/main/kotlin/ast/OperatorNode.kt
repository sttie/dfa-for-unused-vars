package ast

import visitors.Visitor
import java.util.*

open class OperatorNode(
    val operator: String,
    line: Int
) : Node(line = line) {
    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitOperator(this)
    }

    fun leftOperand(): Node {
        return children[0]
    }

    fun rightOperand(): Node {
        return children[1]
    }
}