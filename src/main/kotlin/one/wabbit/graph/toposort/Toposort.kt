package one.wabbit.graph.toposort

import one.wabbit.data.ConsList
import java.util.*

class Graph<V>(nodes: List<V>, pairs: List<Pair<V, V>>) {
    private val vertices = ArrayList<V>(16)
    private val index = HashMap<V, Int>()

    private val vertexCount: Int
    private val edges: Array<Pair<Int, Int>>
    private val adjacency: Array<BooleanArray>
    private val incoming: Array<MutableList<Int>>
    private val outgoing: Array<MutableList<Int>>

    init {
        nodes.forEach { index(it) }
        edges = Array(pairs.size) { Pair(index(pairs[it].first), index(pairs[it].second)) }
        vertexCount = vertices.size

        adjacency = Array(vertexCount) { BooleanArray(vertexCount) }
        incoming = Array(vertexCount) { mutableListOf() }
        outgoing = Array(vertexCount) { mutableListOf() }
        for (edge in edges) {
            adjacency[edge.first][edge.second] = true
            incoming[edge.second].add(edge.first)
            outgoing[edge.first].add(edge.second)
        }
    }

    private fun index(value: V): Int = index.computeIfAbsent(value, { p ->
        val i = vertices.size
        vertices.add(p)
        i
    })

    @Throws(FoundCycle::class)
    fun topoSort(): List<V> {
        val color = IntArray(vertexCount)
        val result = mutableListOf<Int>()

        fun visit(node: Int, path: ConsList<Int>) {
            if (color[node] == 2) return
            if (color[node] == 1) throw FoundCycle(path.reversed())
            color[node] = 1
            for (m in outgoing[node]) visit(m, ConsList.Cons(node, path))
            color[node] = 2
            result.add(node)
        }

        for (it in (0 until vertexCount)) {
            if (color[it] == 0) visit(it, ConsList.Nil)
        }

        assert(result.size == vertexCount)
        return result.map { vertices[it] }
    }
}

data class FoundCycle(val cycle: List<Int>) : Exception()
