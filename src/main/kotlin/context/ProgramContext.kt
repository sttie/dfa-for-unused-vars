package context

import java.util.HashSet


object ProgramContext {
    private val declaredVariables = HashSet<String>()

    fun updateContext(id: String) {
        if (id.isNotEmpty())
            declaredVariables.add(id)
    }

    fun contains(id: String): Boolean = declaredVariables.contains(id)
}