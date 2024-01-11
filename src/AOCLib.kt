import kotlin.math.sign

class AOCLib {
    companion object {
        fun<X> DFSnocyc(start: X, neighbors: (X) -> Collection<X>, goal: (X) -> Boolean): List<X>? {
            if (goal(start)) return listOf(start)
            val options = neighbors(start)
            for (o in options) {
                val here = DFSnocyc(o,neighbors,goal)
                if (here != null) return listOf(start) + here
            }
            return null
        }
        fun String.numOccurencesOf(s: String) = Regex.fromLiteral(s).findAll(this).count()
        fun Int.pow(exp: Int) : Int {
            var ret = 1
            var place = exp
            var acc = this
            while (place != 0) {
                if (place and 1 != 0) {
                    ret *= acc
                }
                acc *= acc
                place = place ushr 1
            }
            return ret
        }
        fun Long.pow(exp: Long) : Long {
            var ret = 1L
            var place = exp
            var acc = this
            while (place != 0L) {
                if (place and 1L != 0L) {
                    ret *= acc
                }
                acc *= acc
                place = place ushr 1
            }
            return ret
        }
        fun Int.divRndUp(i: Int) = this / i + (this % i).sign
        fun Long.divRndUp(i: Long) = this / i + (this % i).sign
        fun<X,Y> Collection<X>.cartprod(other: Collection<Y>): List<Pair<X, Y>> {
            return flatMap{x->other.map{y->x to y} }
        }
        fun<X> Collection<X>.allChoicesOf(n: Int) : List<List<X>>{
            if (n == 0) return listOf(emptyList())
            val l = toList()
            if (n == 1) return map{listOf(it)}
            if (n > size) return emptyList()
            val ret = mutableListOf<List<X>>()
            for (i in 0..l.size - n) {
                val chosen = l[i]
                val subs = l.subList(i+1,l.size).allChoicesOf(n-1)
                subs.mapTo(ret){it + chosen}
            }
            return ret
        }
        fun<X> Collection<X>.forAllChoicesOf(n: Int, f: (List<X>) -> Unit) {
            if (n == 0) {
                f(emptyList())
                return
            }
            if (n > size) return
            val l = toList()
            for (i in 0..l.size-n) {
                val chosen = l[i]
                l.subList(i+1,l.size).forAllChoicesOf(n-1) {
                    f(it + chosen)
                }
            }
        }
        data class LazyTree<X>(val leaf: X, val branches: Sequence<LazyTree<X>>)
        fun<X> Collection<X>.exploreAllChoicesOfN_AsTree(n: Int) : Sequence<LazyTree<X>> {
            if (n==0) return emptySequence()
            val l = toList()
            return Sequence{(0..l.size-n).iterator()}.map{
                val chosen = l[it]
                val sub = l.subList(it+1,l.size)
                LazyTree(chosen,sub.exploreAllChoicesOfN_AsTree(n-1))
            }
        }
    }
}