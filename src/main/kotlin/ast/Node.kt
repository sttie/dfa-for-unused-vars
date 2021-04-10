package ast

import visitors.Visitor
import java.util.*

abstract class Node(
    val children: LinkedList<Node> = LinkedList(),
    val line: Int
) {
    abstract fun acceptVisitor(visitor: Visitor)

    fun addNodes(nodes: LinkedList<Node>) {
        for (node in nodes)
            children.add(node)
    }

    fun addNode(node: Node) {
        children.add(node)
    }
}