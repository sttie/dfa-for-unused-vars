package ast

import visitors.Visitor
import java.util.*

class AssignNode(
    linkedNodes: LinkedList<Node> = LinkedList(),
    line: Int
) : Node(linkedNodes, line) {

    override fun acceptVisitor(visitor: Visitor) {
        visitor.visitAssign(this)
    }

    fun getLeftOperandName(): String {
        return (children[0] as VariableNode).name
    }

    fun getRightOperand(): Node {
        return children[1]
    }
}