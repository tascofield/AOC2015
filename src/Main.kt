import AOCLib.Companion.allChoicesOf
import AOCLib.Companion.cartprod
import AOCLib.Companion.divRndUp
import AOCLib.Companion.numOccurencesOf
import AOCLib.Companion.pow
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and
import kotlin.math.sqrt
import kotlin.system.exitProcess

fun main() {
    d1()
    d2()
    d3()
    d4()
    d5()
    d6()
    d7()
    d8()
    d9()
    d10()
    d11()
    d12()
    d13()
    d14()
    d15()
    d16()
    d17()
    d18()
    d19()
    d20()
    d21()
    d22()
    d23()
    d24()
    d25()
}

fun rdday(n: Int): String {
    return File("src/d$n.txt").readText()
}

fun rdlines(n: Int) = rdday(n).lines()

fun d25() {
    val inp = "To continue, please consult the code grid in the manual.  Enter the code at row 2981, column 3075."
    val (row,col) = inp.split("\\D+".toRegex()).filter{it.isNotEmpty()}.map{it.toInt()}
    fun codeNumAtRowCol(r: Int, c: Int): Int {
        val diag = r + c - 1
        val tri = diag*(diag + 1)/2
        val rem = r
        return tri - rem + 1
    }
    val test = Array(20){r->IntArray(20){c->codeNumAtRowCol(r+1,c+1)} }
    val codenum = codeNumAtRowCol(row,col)
    var acc = 20151125L
    for (i in 1..<codenum) {
        acc *= 252533
        acc %= 33554393
    }
    println(acc)
    return
}

fun d24() {
    var inp = rdday(24).split("\\D".toRegex()).map{it.toInt()}.sorted()
//    inp = (1..5).toList() + (7..11)
    val totalWeight = inp.sum()
    val target = totalWeight / 3
    val singleBits = inp.mapIndexed{index, it -> it to 1.shl(index)}.toMap()
    fun bit2weight(b: Int) = inp[b.countTrailingZeroBits()]
    val waysToGetTo = Array(target+1){mutableSetOf<Int>()}
    waysToGetTo[0] += 0
    for (trying in 1..target) {
        println("trying $trying")
        for (lookback in inp) {
            val b = singleBits[lookback]!!
            val prevbin = trying - lookback
            if (prevbin !in waysToGetTo.indices) break
            for (backthere in waysToGetTo[prevbin]) {
                if (backthere and b == 0) {
                    waysToGetTo[trying] += backthere or b
                }
            }
        }
    }
    val contenders = waysToGetTo[target].sorted()
    fun fieldQE(f: Int): BigInteger {
        var dec = f
        var ret = BigInteger.ONE
        while (dec != 0) {
            val b = dec.takeLowestOneBit()
            dec = dec xor b
            ret *= bit2weight(b).toBigInteger()
        }
        return ret
    }
    val sizeBins = mutableMapOf<Int,MutableSet<Int>>()
    contenders.forEach {
        sizeBins.putIfAbsent(it.countOneBits(), mutableSetOf())
        sizeBins[it.countOneBits()]!! += it
    }
    val sizes = sizeBins.keys.sorted()
    outer@for (size in sizes) {
        val bin = sizeBins[size]!!
        val sortedByQE = bin.sortedBy {fieldQE(it)}
        for (lowQE in sortedByQE) {
            val hasValidGroup = contenders.any{it and lowQE == 0}
            if (hasValidGroup) {
                val qe = fieldQE(lowQE)
                println(qe)
                break@outer
            }
        }
    }

    val target2 = totalWeight / 4
    val contenders2 = waysToGetTo[target2].sorted()
    val sizeBins2 = mutableMapOf<Int,MutableSet<Int>>()
    contenders2.forEach{
        sizeBins2.putIfAbsent(it.countOneBits(), mutableSetOf())
        sizeBins2[it.countOneBits()]!! += it
    }
    val sizes2 = sizeBins2.keys.sorted()
    outer2@for (size in sizes2) {
        val bin = sizeBins2[size]!!
        val sortedByQE = bin.sortedBy {fieldQE(it)}
        for (lowQE in sortedByQE) {
            val valid = contenders2.any{g2->g2 and lowQE == 0 && contenders2.any{(lowQE or g2) and it == 0}}
            if(valid) {
                val qe = fieldQE(lowQE)
                println(qe)
                break@outer2
            }
        }
    }
    return
}

fun d23() {
    var inp = rdlines(23)
//    inp = """inc a
//jio a, +2
//tpl a
//inc a""".lines()
    var ins = inp.map{it.split("[ ,]+".toRegex())}
    var ip = 0
    var a = BigInteger.ZERO
    var b = BigInteger.ZERO
    fun g(c: Char) = if(c == 'a') a else if(c == 'b') b else throw IllegalStateException("bad register: $c")
    fun s(c: Char, v: BigInteger) = if(c == 'a') a = v else if(c == 'b') b = v else throw IllegalStateException("bad register: $c")
    fun eval() {
        val i = ins[ip]
        if (i[0][0] == 'j') {
            when(i[0]) {
                "jmp" -> ip += i[1].toInt()
                "jie" -> ip += if (g(i[1][0]).testBit(0)) 1 else i[2].toInt()
                "jio" -> ip += if (g(i[1][0]) == BigInteger.ONE) i[2].toInt() else 1
                else -> throw IllegalStateException("bad instruction: ${i[0]}")
            }
        } else {
            when (i[0]) {
                "inc" -> s(i[1][0], g(i[1][0]).inc())
                "tpl" -> s(i[1][0], g(i[1][0]).times(3.toBigInteger()))
                "hlf" -> s(i[1][0], g(i[1][0]).shr(1))
                else -> throw IllegalStateException("bad instruction: ${i[0]}")
            }
            ip++
        }
    }
    while (ip in ins.indices) {
        eval()
    }
    println(b)
    ip = 0
    a = BigInteger.ONE
    b = BigInteger.ZERO
    while (ip in ins.indices) {
        eval()
    }
    println(b)
    return
}

fun d22() {
    val inp = """Hit Points: 55
Damage: 8
"""
    val (bossStartingHP,bossDMG) = inp.split("\\D".toRegex()).filter{it.isNotEmpty()}.map{it.toInt()}
    data class situation(val myturn: Boolean,
                         val bossHP: Int, val myHP: Int,
                         val shieldTurns: Int, val poisonTurns: Int, val rechargeTurns: Int,
                         val mana: Int,
                         val totalManaSpent: Int) {
        val won : Boolean
            get() = bossHP <= 0
        val lost : Boolean
            get() = myHP <= 0 || (myturn && mana < 53)
        val gameover : Boolean
            get() = won || lost
        fun trySpend(m: Int) = if(mana >= m) copy(mana = mana - m, totalManaSpent = totalManaSpent + m) else null
        fun options(): List<situation> {
            if (gameover) return emptyList()
            val ret = mutableListOf<situation>()
            var now = this
            var armor = if(shieldTurns != 0) 7 else 0
            now = now.copy(shieldTurns = now.shieldTurns-1)
            if (now.shieldTurns == 0) armor -= 7
            else if (now.shieldTurns < 0) now = now.copy(shieldTurns = 0)
            if (now.poisonTurns > 0) {
                now = now.copy(bossHP = now.bossHP - 3)
                now = now.copy(poisonTurns = now.poisonTurns - 1)
            }
            if (now.rechargeTurns > 0) {
                now = now.copy(mana = now.mana + 101)
                now = now.copy(rechargeTurns = now.rechargeTurns - 1)
            }
            if (!now.myturn) {
                val damageToMe = (bossDMG - armor).coerceAtLeast(1)
                now = now.copy(myHP = now.myHP - damageToMe)
                now = now.copy(myturn = true)
                ret += now
                return ret
            }
            now = now.copy(myturn = false)
            now.trySpend(53)?.also{
                ret += it.copy(bossHP = it.bossHP - 4)
                now.trySpend(73)?.also{
                    ret += it.copy(bossHP = it.bossHP - 2, myHP = it.myHP + 2)
                    now.trySpend(113)?.also{
                        if(it.shieldTurns == 0) ret += it.copy(shieldTurns = 6)
                        now.trySpend(173)?.also{
                            if(it.poisonTurns == 0) ret += it.copy(poisonTurns = 6)
                            now.trySpend(229)?.also{
                                if (it.rechargeTurns == 0) ret += it.copy(rechargeTurns = 5)
                            }
                        }
                    }
                }
            }
            return ret
        }
    }
    val start = situation(myturn = true, myHP = 50, mana = 500,
        bossHP = bossStartingHP, shieldTurns = 0, poisonTurns = 0, rechargeTurns = 0, totalManaSpent = 0)
    val q = PriorityQueue<situation>{s1,s2->s1.totalManaSpent - s2.totalManaSpent}
    q += start
    while(true) {
        val now = q.remove()
        if (now.won) {
            println(now.totalManaSpent)
            break
        }
        q.addAll(now.options())
    }
    fun situation.tick() = if(myturn) copy(myHP = myHP - 1) else this
    q.clear()
    q += start.tick()
    while(true) {
        val now = q.remove()
        if (now.won) {
            println(now.totalManaSpent)
            break
        }
        q.addAll(now.options().map{it.tick()})
    }
    return
}

fun d21() {
    val inp = """Hit Points: 103
Damage: 9
Armor: 2
"""
    val bossStatss = inp.split("\\D".toRegex()).filter{it.isNotEmpty()}.map{it.toInt()}
    val (bossHP,bossDMG,bossArmor) = bossStatss

    val shop = """Weapons:    Cost  Damage  Armor
Dagger        8     4       0
Shortsword   10     5       0
Warhammer    25     6       0
Longsword    40     7       0
Greataxe     74     8       0

Armor:      Cost  Damage  Armor
Leather      13     0       1
Chainmail    31     0       2
Splintmail   53     0       3
Bandedmail   75     0       4
Platemail   102     0       5

Rings:      Cost  Damage  Armor
Damage +1    25     1       0
Damage +2    50     2       0
Damage +3   100     3       0
Defense +1   20     0       1
Defense +2   40     0       2
Defense +3   80     0       3"""
    val (wpns,armors,rings) = shop.split("\n\n")
        .map{it.lines().let{it.subList(1,it.size).map{
            it.substring(12).split("\\D".toRegex())
                .filter{it.isNotEmpty()}.map{it.toInt()}}}}
    val weaponChoices = 1..1
    val armorChoices = 0..1
    val ringChoices = 0..2
    val picks = listOf(weaponChoices to wpns, armorChoices to armors, ringChoices to rings)
    data class stats(val dmg: Int, val armor: Int, val hp: Int)
    val boss = stats(bossDMG,bossArmor,bossHP)
    fun winIfYouHave(boss: stats, you: stats): Boolean {
        val damageToBossPerTurn = (you.dmg - boss.armor).coerceAtLeast(1)
        val damageToYouPerTurn = (boss.dmg - you.armor).coerceAtLeast(1)
        val turnWhenYouDie = you.hp.divRndUp(damageToYouPerTurn)
        val turnWhenBossDies = boss.hp.divRndUp(damageToBossPerTurn)
        return turnWhenYouDie >= turnWhenBossDies
    }
    val test = winIfYouHave(boss=stats(7,2,12),you=stats(5,5,8))
    val test2 = "abcdef".toList().allChoicesOf(3)
    val allPossibleChoices = picks.map{(cs,lst)->cs.flatMap{lst.allChoicesOf(it)}}
        .let{(w,a,r)->w.cartprod(a).cartprod(r).map{it.first.first + it.first.second + it.second}}
    val summed = allPossibleChoices.map{it.fold(Triple(0,0,0)){(c,d,a),(c1,d1,a1)->Triple(c+c1,d+d1,a+a1)}}
    val winningLoadouts = summed.filter{(c,d,a)->winIfYouHave(boss,stats(d,a,100))}
    val leastGold = winningLoadouts.minOf{it.first}
    println(leastGold)
    val losingLoadouts =  summed.filter{(c,d,a)->!winIfYouHave(boss,stats(d,a,100))}
    val mostGold = losingLoadouts.maxOf{it.first}
    println(mostGold)
    return
}

fun d20() {
    //presents(housenum) = 10*sum(factors(housenum))
    //if n's prime factorization is p^x, then the sum of its factors is 1 + p + p^2 + ... + p^x = (p^(x+1)-1)/(p-1)
    //if n's prime factorization is 2^a*3^b*5^c*...*p^x, then the sum of it's factors is 1*sum_factors(2^a*3^b*5^c*...) + p*sum_factors(2^a*3^b*5^c*...) + ... + p^x*sum_factors(2^a*3^b*5^c*...)
    // = (p^(x+1)-1)/(p-1)*sum_factors(2^a*3^b*5^c*...)
    val inp = 29000000L

    val primes = (2L..100000).filter{i->(2..<kotlin.math.sqrt(i.toDouble()).toLong() + 1).none{ j->i % j == 0L}}.toLongArray()
    fun presents(counts: Map<Long,Int>): Long {
        val presents = counts.toList().fold(10L){acc: Long, (p,n): Pair<Long, Int> -> acc*(p.pow(n.toLong()+1)-1)/(p-1)}
//        val presents = counts.indices.fold(10L){acc: Long, i: Int -> acc * (primes[i].pow(counts[i].toLong()+1)-1)/(primes[i]-1)}
        return presents
    }

    fun decomp(l: Long): Map<Long, Int> {
        val ret = mutableMapOf<Long,Int>()
        var dec = l
        outer@ while (true) {
            var at = 0
            val sqrt = sqrt(dec.toDouble()).toLong() + 1
            while (primes[at] <= sqrt) {
                val p = primes[at++]
                if (dec % p == 0L) {
                    ret[p] = ret.getOrDefault(p,0) + 1
                    dec /= p
                    continue@outer
                }
            }
            if (dec == 1L) break
            ret[dec] = ret.getOrDefault(dec,0) + 1
            break
        }
        return ret
    }
    var testing = 2L
    while(true) {
        val dc = decomp(testing)
        val presents = presents(dc)
        if (presents > inp) break
        testing++
//        if (testing % 1000 == 0L) println(testing)
    }
//    val dc = decomp(testing)
//    val presents = presents(dc)
    println(testing)

    //sum of factors -> sum of factors whose complement is >=50
    fun sumOfFactorsWhoseComplementIsLTE50(l: Long): Long {
        var ret = 0L
        for (i in 1..50) {
            if (l % i == 0L) {
                val c = l / i
                ret += c
            }
        }
        return ret
    }

    testing = 0
    while(true) {
        val presents = 11*sumOfFactorsWhoseComplementIsLTE50(testing)
        if (presents > inp) break
        testing++
    }
    println(testing)
    val presents = 11*sumOfFactorsWhoseComplementIsLTE50(testing)
    return
}

fun d19() {
    var inp = rdday(19)
    val (replss,start) = inp.split("\n\n")
    val repls = replss.lines().map{it.split(" +=> +".toRegex()).let{(a,b)->a to b}}
    fun allReplacementsFromRule(rule: Pair<String,String>, pattern: String): MutableSet<String> {
        val occurrances = Regex.fromLiteral(rule.first).findAll(pattern).toList()
        val ret = mutableSetOf<String>()
        for (match in occurrances) {
            ret += pattern.substring(0..<match.range.first) + rule.second + pattern.substring(match.range.last + 1)
        }
        return ret
    }
    val test1 = listOf("H" to "HO","H" to "OH", "O" to "HH").fold(emptySet<String>()) {acc,pair->
        acc + allReplacementsFromRule(pair,"HOH")
    }
    val test2 = listOf("H" to "HO","H" to "OH", "O" to "HH").fold(emptySet<String>()) {acc,pair->
        acc + allReplacementsFromRule(pair,"HOHOHO")
    }
    val part1 = repls.fold(emptySet<String>()) {acc,pair ->
        acc + allReplacementsFromRule(pair,start)
    }
    println(part1.size)

//    val slper = repls.map{(a,b)->b to a}
//    val connections : (String) -> List<String> = {
//        slper.fold(emptyList()){acc, pair -> acc + allReplacementsFromRule(pair,it) }
//    }
//    val finish : (String) -> Boolean = {it == "e"}
//    val path = AOCLib.DFSnocyc(start,connections,finish)

    println(start.count { it.isUpperCase() }
            - start.numOccurencesOf("Ar")
            - start.numOccurencesOf("Rn")
            - 2*start.numOccurencesOf("Y")
            - 1)
    //stolen from https://www.reddit.com/r/adventofcode/comments/3xflz8/day_19_solutions/cy4etju/
    return
}

fun d18() {
    var inp = rdlines(18) to 100
//    inp = """.#.#.#
//...##.
//#....#
//..#...
//#.#..#
//####..""".lines() to 5
    var curLights = inp.first
    fun printLights() = println(curLights.joinToString(separator = "\n") { it })
//    printLights()
    for (i in 1..inp.second) {
        val windows = curLights.indices.map{l->
            curLights[l].indices.map{c->
                (l-1..l+1 intersect curLights.indices).map{l2->
                    (c-1..c+1 intersect curLights[l2].indices).map{c2->
                    curLights[l2][c2]
                    }
                }
            }
        }
        val neighborCounts = windows.mapIndexed{l,it->it.mapIndexed{c,it->it.sumOf{it.count{it == '#'}} - if(curLights[l][c] == '#') 1 else 0 }}
        curLights = curLights.indices.map{l->
            curLights[l].indices.map {c->
                neighborCounts[l][c] == 3 || (neighborCounts[l][c] == 2 && curLights[l][c] == '#')
            }.let{CharArray(it.size){c->if (it[c]) '#' else '.'}.concatToString()}
        }
//        println()
//        println("after $i steps")
//        printLights()
    }
    println(curLights.sumOf{it.count { it == '#' }})
    curLights = inp.first.map{it.toCharArray()}.also{
        it[0][0] = '#'
        it[0][it[0].lastIndex] = '#'
        it.last()[0] = '#'
        it.last()[it.last().lastIndex] = '#'
    }.map{it.concatToString()}
    for (i in 1..inp.second) {
        val windows = curLights.indices.map{l->
            curLights[l].indices.map{c->
                (l-1..l+1 intersect curLights.indices).map{l2->
                    (c-1..c+1 intersect curLights[l2].indices).map{c2->
                        curLights[l2][c2]
                    }
                }
            }
        }
        val neighborCounts = windows.mapIndexed{l,it->it.mapIndexed{c,it->it.sumOf{it.count{it == '#'}} - if(curLights[l][c] == '#') 1 else 0 }.toMutableList()}
        neighborCounts[0][0] = 3
        neighborCounts[0][neighborCounts[0].lastIndex] = 3
        neighborCounts[neighborCounts.lastIndex][0] = 3
        neighborCounts[neighborCounts.lastIndex][neighborCounts[neighborCounts.lastIndex].lastIndex] = 3
        curLights = curLights.indices.map{l->
            curLights[l].indices.map {c->
                neighborCounts[l][c] == 3 || (neighborCounts[l][c] == 2 && curLights[l][c] == '#')
            }.let{CharArray(it.size){c->if (it[c]) '#' else '.'}.concatToString()}
        }
//        println()
//        println("after $i steps")
//        printLights()
    }
    println(curLights.sumOf{it.count { it == '#' }})
}

fun d17() {
    var inp = rdday(17) to 150
//    inp = "20, 15, 10, 5, and 5" to 25
    val containers = inp.first.split("\\D".toRegex()).filter{it.isNotEmpty()}.map{it.toInt()}.sorted()
    fun litersOf(selected: List<Boolean>): Int {
        return containers.indices.sumOf{i->if(selected[i]) containers[i] else 0}
    }
    val waysToGetTo = mutableMapOf<Int,MutableSet<List<Boolean>>>()
    waysToGetTo += 0 to mutableSetOf(List(containers.size){false})
    for (here in 1..inp.second) {
        val doable = mutableSetOf<List<Boolean>>()
        for (i in containers.indices) {
            val container = containers[i]
            if (container > here) break
            val backthere = waysToGetTo[here - container]!!
            val usable = backthere.filter{!it[i]}
            val nowUsing = usable.map{it.mapIndexed{index, b -> b || index == i }}
            doable.addAll(nowUsing)
        }
        waysToGetTo += here to doable
    }
    println(waysToGetTo[inp.second]!!.size)
    val leastContainers = waysToGetTo[inp.second]!!.minOf{it.count{it}}
    println(waysToGetTo[inp.second]!!.count{it.count{it} == leastContainers})
    return
}

fun d16() {
    var inp = rdlines(16)
    val memories = """children: 3
cats: 7
samoyeds: 2
pomeranians: 3
akitas: 0
vizslas: 0
goldfish: 5
trees: 3
cars: 2
perfumes: 1""".lines().map{it.split(": ").let{(a,b)->a to b.toInt()}}.toMap()
    val aunts = inp.map{it.replace("Sue \\d*: ".toRegex(),"")
        .split(", ").map{it.split(": ").let{(a,b)->a to b.toInt()}}.toMap()}
    val possible = aunts.mapIndexedNotNull{i,it->if (it.all{(k,v)->memories.getOrElse(k){throw IllegalStateException("couldn't get \'$k\'")} == v}) i to it else null}
    println(possible.first().first + 1)
    val realPossible = aunts.mapIndexedNotNull{i,sue->
        val gt = listOf("cats","trees")
        val lt = listOf("pomeranians","goldfish")
        val eq = memories.keys - gt - lt
        if ((gt intersect sue.keys).any{sue.getValue(it) <= memories.getValue(it)}) null
        else if ((lt intersect sue.keys).any{sue.getValue(it) >= memories.getValue(it)}) null
        else if ((eq intersect sue.keys).any{sue.getValue(it) != memories.getValue(it)}) null
        else i to sue
    }
    println(realPossible.first().first + 1)
    return
}

fun d15() {
    var inp = rdlines(15)
//    inp = """Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
//Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3""".lines()
    val nums = inp.map{it.split("[^-\\d]".toRegex()).filterNot{it.isEmpty()}.map{it.toInt()}}
    fun forAllProportionsStartingWith(len: Int, front: IntArray, f: (IntArray) -> Unit){
        val used = (0..<len).sumOf{front[it]}
        if (used > 100) return
        val left = 100 - used
        if (len+1 == nums.size) {
            front[len] = left
            f(front)
            return
        }
        for (using in 0..left) {
            front[len] = using
            forAllProportionsStartingWith(len+1,front,f)
        }
    }
    var best = 0
    var argbest: List<Int>? = null
    forAllProportionsStartingWith(0,IntArray(nums.size)) {
        val thisMix = nums.first().indices.map{c->nums.indices.sumOf{r->nums[r][c]*it[r]}}
        val floored = thisMix.map{it.coerceAtLeast(0)}
        val noncalories = floored.subList(0,floored.lastIndex)
        val score = noncalories.fold(1){acc, i -> acc*i}
        if (best < score) {
            best = score
            argbest = it.toList()
        }
    }
    println(best)
    best = 0
    forAllProportionsStartingWith(0,IntArray(nums.size)) {
        val thisMix = nums.first().indices.map{c->nums.indices.sumOf{r->nums[r][c]*it[r]}}
        val floored = thisMix.map{it.coerceAtLeast(0)}
        val noncalories = floored.subList(0,floored.lastIndex)
        val score = noncalories.fold(1){acc, i -> acc*i}
        if (best < score && floored.last() == 500) {
            best = score
            argbest = it.toList()
        }
    }
    println(best)
    return
}

fun d14() {
    var inp = rdlines(14) to 2503
//    inp = """Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
//Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.""".lines() to 1000
    val names = inp.first.map{it.split(' ')[0]}
    val stats = inp.first.map{it.split("\\D".toRegex()).filter{it.isNotEmpty()}.map{it.toInt()}}
    fun locationAtTime(t: Int, stats: List<Int>): Int {
        val (speed,sprint,rest) = stats
        val cycle = sprint + rest
        val cycles = t/cycle
        val cycleDist = speed*sprint
        val prevCyclesDist = cycles*cycleDist
        val inCycle = t % cycle
        val sprinted = kotlin.math.min(inCycle,sprint)
        return prevCyclesDist + sprinted*speed
    }
    val dists = names.indices.map{names[it] to locationAtTime(inp.second,stats[it])}.toMap()
    println(dists.values.max())
    val points = IntArray(names.size)
    for (t in 1.. inp.second) {
        val locations = stats.map{locationAtTime(t,it)}
        val max = locations.max()
        locations.indices.forEach{if (locations[it] == max) points[it]++}
    }
    println(points.max())
    return
}

fun d13() {
    var inp = rdlines(13)
//    inp = """Alice would gain 54 happiness units by sitting next to Bob.
//Alice would lose 79 happiness units by sitting next to Carol.
//Alice would lose 2 happiness units by sitting next to David.
//Bob would gain 83 happiness units by sitting next to Alice.
//Bob would lose 7 happiness units by sitting next to Carol.
//Bob would lose 63 happiness units by sitting next to David.
//Carol would lose 62 happiness units by sitting next to Alice.
//Carol would gain 60 happiness units by sitting next to Bob.
//Carol would gain 55 happiness units by sitting next to David.
//David would gain 46 happiness units by sitting next to Alice.
//David would lose 7 happiness units by sitting next to Bob.
//David would gain 41 happiness units by sitting next to Carol.""".lines()
    val edges = inp.map{
        it.replace("would ","")
            .replace("gain ","")
            .replace("lose ","-")
            .replace("happiness units by sitting next to ","")
            .replace(".","")
            .split(' ')
            .let{(a,n,b)->(a to b) to n.toInt()}
    }.toMap()
    val people = edges.map{(k,_)->k.first}.toSet().toList()
    val fixed = people.first()
    val arranging = people.subList(1,people.size)
    var best = 0
    fun<X> List<X>.forEachPermutation(f: (List<X>)->Unit) {
        val arrangements = indices.fold(1){acc,i->acc*(i+1)}
        for (a in 0..<arrangements) {
            val dec = this.toMutableList()
            val arrangement = mutableListOf<X>()
            var acc = a
            while (dec.size != 0) {
                arrangement += dec.removeAt(acc % dec.size)
                acc /= dec.size + 1
            }
            f(arrangement)
        }
    }
    arranging.forEachPermutation {
        val arrangement = it + fixed
        val happiness = arrangement.windowed(2)
            .sumOf{(a,b)->edges[a to b]!! + edges[b to a]!!} +
                arrangement.let{edges[it.first() to it.last()]!! + edges[it.last() to it.first()]!!}
        if (happiness > best) best = happiness
    }
    println(best)
    var best2 = 0
    people.forEachPermutation {
        val happiness = it.windowed(2)
            .sumOf { (a,b)->edges[a to b]!! + edges[b to a]!! }
        if (happiness > best2) best2 = happiness
    }
    println(best2)
    return
}

fun d12() {
    var inp = rdday(12)
//    println(inp.split("[^\\d-]+".toRegex()).filter{it.isNotEmpty()}.map{it.toLong()}.sum())
    var at = 0
    val inner = object {
        fun takeNext() : Any {
            return when(inp[at]) {
                '[' -> takeArray()
                '{' -> takeMap()
                '"' -> takeString()
                in "-01223456789" -> takeNum()
                else -> throw IllegalStateException("unknown start of token: " + inp.substring(at))
            }
        }
        fun takeArray(): Array<Any> {
            val ret = mutableListOf<Any>()
            at++
            if (inp[at] == ']') return ret.toTypedArray()
            ret += takeNext()
            while(inp[at] != ']') {
                if (inp[at] != ',') throw IllegalStateException("expected comma in array (at $at): " + inp.substring(at))
                at++
                ret += takeNext()
            }
            at++
            return ret.toTypedArray()
        }
        fun takeMap(): Map<Any, Any> {
            val ret = mutableMapOf<Any,Any>()
            at++
            if (inp[at] == '}') return ret
            val key = takeNext()
            if (inp[at] != ':') throw IllegalStateException("Unknown map entry separator: " + inp.substring(at))
            at++
            val v = takeNext()
            ret += key to v
            while (inp[at] != '}') {
                if (inp[at] != ',') throw IllegalStateException("expected comma in map: " + inp.substring(at))
                at++
                val key = takeNext()
                if (inp[at] != ':') throw IllegalStateException("Unknown map entry separator: " + inp.substring(at))
                at++
                val v = takeNext()
                ret += key to v
            }
            at++
            return ret
        }
        fun takeNum(): Long {
            val first = at
            while(inp[at] in "-01223456789") at++
            val ns = inp.substring(first,at)
            return ns.toLong()
        }
        fun takeString(): String {
            val quote = at
            at++
            while (inp[at] != '"') at++
            val unquote = at
            at++
            val ret = inp.substring(quote + 1, unquote)
            return ret
        }
    }
    val parsed = inner.takeNext()
    fun count1(t: Any) : Long{
        return when(t) {
            is Long -> t
            is String -> 0
            is Array<*> -> t.sumOf{count1(it!!)}
            is Map<*,*> -> t.keys.sumOf{count1(it!!)} + t.values.sumOf{count1(it!!)}
            else -> throw IllegalStateException("unknown thingy: $t")
        }
    }
    println(count1(parsed))
    fun count2(t: Any) : Long {
        return when(t) {
            is Long -> t
            is String -> 0
            is Array<*> -> t.sumOf{count2(it!!)}
            is Map<*,*> -> if (t.containsValue("red")) 0 else t.keys.sumOf{count2(it!!)} + t.values.sumOf{count2(it!!)}
            else -> throw IllegalStateException("unknown thingy: $t")
        }
    }
    println(count2(parsed))
    return
}


fun d11() {
    var inp = "hepxcrrq"
    fun inc(c: CharArray) {
        for (i in c.indices.reversed()) {
            if (c[i] != 'z') {
                c[i]++
                return
            }
            c[i] = 'a'
        }
    }
    fun isValid(c: CharArray): Boolean {
        if (c.toList().windowed(3).all{(a,b,c)->b-a != 1 || c-b != 1}) return false
        if (c.any{it in "iol"}) return false
        var samePairs = c.toList().windowed(2).map{(a,b)->a==b}
        samePairs = listOf(false) + samePairs + false
        if (samePairs.windowed(3).count{(a,b,c)->b && !(a || c)} == 2) return true
        return false
    }
//    val toCheck = listOf("hijklmmn","abbceffg","abbcegjk","abcdffaa","ghjaabcc")
//    val check = toCheck.map{isValid(it.toCharArray())}
    fun nextValidAfter(s: String): String {
        val cur = s.toCharArray()
        while (!isValid(cur)) inc(cur)
        return cur.concatToString()
    }
//    val toCheck2 = listOf("abcdefgh","ghijklmn")
//    val check2 = toCheck2.map{nextValidAfter(it)}
    val a1 = nextValidAfter(inp)
    println(a1)
    println(nextValidAfter(a1.toCharArray().also{inc(it)}.concatToString()))
    return
}

fun d10() {
    var inp = "3113322113"
//    inp = "1"
    val digits = IntArray(inp.length){inp[it]-'0'}
    fun iter(d: IntArray): IntArray {
        val ret = mutableListOf<Int>()
        var cur = 0
        var look = 1
        while(look < d.size) {
            if (d[cur] != d[look]) {
                ret += look - cur
                ret += d[cur]
                cur = look
            }
            look++
        }
        ret += look - cur
        ret += d[cur]
        return ret.toIntArray()
    }
    var cur = digits
    for (i in 0..<40) {
        cur = iter(cur)
    }
    println(cur.size)
    for (i in 0..<10) {
        cur = iter(cur)
    }
    println(cur.size)
    return
}

fun d9() {
    var inp = rdlines(9)
//    inp = """London to Dublin = 464
//London to Belfast = 518
//Dublin to Belfast = 141""".lines()
    val data = inp.map{it.split(" = ").let{(a,b)->a.split(" to ").let{(a1,a2)->a1 to a2 to b.toInt()}}}
    val distance = (data + data.map{(ab,d)->ab.second to ab.first to d}).toMap()
    val allLocations = data.flatMap{it.first.toList()}.toSet().toList()
    val possibilities = allLocations.indices.fold(1){acc, i -> acc*(i+1)}
    var best = Int.MAX_VALUE
    var worst = 0
    for (n in 0..possibilities) {
        var acc = n
        val tearing = allLocations.toMutableList()
        val building = mutableListOf<String>()
        while (tearing.isNotEmpty()) {
            building += tearing.removeAt(acc % tearing.size)
            acc /= tearing.size + 1
        }
        val dist = building.windowed(2).sumOf{(a,b)->distance[a to b]!!}
        if (dist < best) best = dist
        if (dist > worst) worst = dist
    }
    println(best)
    println(worst)
    return
}

fun d8() {
    var inp = rdlines(8)
    val inp1 = inp.map{
        it.removePrefix("\"")
            .removeSuffix("\"")
            .replace("\\\"","\"")
            .replace("\\\\","\\")
            .replace("\\\\x[0-9a-f][0-9a-f]".toRegex(),"R")
    }
    println(inp.sumOf{it.length} - inp1.sumOf { it.length })
    val inp2 = inp.map{
        it.map{when(it){
            '"' -> 2
            '\\' -> 2
            else -> 1
        } }
    }
    println(inp2.sumOf{it.sum() + 2} - inp.sumOf{it.length})
    return
}

fun d7() {
    var inp = rdlines(7)
//    inp = """123 -> x
//456 -> y
//x AND y -> d
//x OR y -> e
//x LSHIFT 2 -> f
//y RSHIFT 2 -> g
//NOT x -> h
//NOT y -> i""".lines()
    val instructions: List<Pair<String, List<String>>> = inp.map { line->
        val structions = listOf("and","or","lshift","rshift","not")
        for (keyword in structions) {
            if (line.contains(keyword.uppercase())) {
                return@map keyword to line.split(' ').filter{it !in listOf("->",keyword.uppercase())}
            }
        }
        if (".* -> .*".toRegex().matches(line)) {
            return@map "imm" to line.split(" -> ")
        }
        println("OOPSIE!!! $line")
        exitProcess(999)
    }
    val dests = instructions.map{it.second.last()}
    val instructionsDependingUpon = dests.map{d->d to instructions.indices.filter{instructions[it].second.contains(d)}}.toMap()
    fun emulate(progress : MutableMap<String,UShort?>) {
        val imms = instructions.indices.filter{instructions[it].first == "imm" && instructions[it].second.first().all{it.isDigit()}}
        val completed = BooleanArray(instructions.size){false}
        val possibleTODOs = imms.toMutableList()
        while (possibleTODOs.isNotEmpty()) {
            val doing = possibleTODOs.removeFirst()
            if (completed[doing]) continue
            val it = instructions[doing]
            val dependents = it.second.subList(0,it.second.lastIndex)
            if (dependents.any{it.any{!it.isDigit()} && progress[it] == null}) continue
            val values = dependents.map{progress[it] ?: it.toUShort()}
            val ans : UShort = when(it.first) {
                "imm" -> values[0]
                "and" -> values[0] and values[1]
                "or" -> values[0] or values[1]
                "lshift" -> (values[0].toUInt() shl values[1].toInt()).toUShort()
                "rshift" -> (values[0].toUInt() shr values[1].toInt()).toUShort()
                "not" -> values[0].inv()
                else -> println("OOPSIEE!!!").let { exitProcess(999) }
            }
            val dest = it.second.last()
            if (progress[dest] == null) {
                progress[dest] = ans
            } else {
                println("skipped overwriting $dest with $ans")
            }
            possibleTODOs += instructionsDependingUpon[dest]!!
            completed[doing] = true
        }
    }
    val progress: MutableMap<String,UShort?> = dests.associateWith{null}.toMutableMap()
    emulate(progress)
    val output = progress["a"]
    println(output)
    progress.keys.forEach{progress[it] = null}
    progress["b"] = output
    emulate(progress)
    println(progress["a"])
    return
}

fun d6() {
    var inp = rdlines(6)
    val rangesStrings = inp.map{"\\d*,\\d* through \\d*,\\d*".toRegex().find(it)!!.value}
    val ranges = rangesStrings.map{it.split(" through ").map{it.split(',').map{it.toInt()}}}
    val orders = inp.map{"[nef] \\d".toRegex().find(it)!!.value.first()}
    val grid = Array(1000){BooleanArray(1000){false}}
    for (i in ranges.indices) {
        val (tl,br) = ranges[i]
        val (t,l) = tl
        val (b,r) = br
        val o = orders[i]
        when(o) {
            /* on */ 'n' -> {
                (t..b).forEach{y->(l..r).forEach{x->grid[y][x] = true}}
            }
            /* toggle */ 'e' -> {
                (t..b).forEach{y->(l..r).forEach{x->grid[y][x] = !grid[y][x]}}
            }
            /* off */ 'f' -> {
                (t..b).forEach{y->(l..r).forEach{x->grid[y][x] = false}}
            }
            else -> println("OOPPSIEEEE!!!!").also { exitProcess(999) }
        }
    }
    println(grid.sumOf{it.count{it}})
    val grid2 = Array(1000){IntArray(1000){0}}
    for (i in ranges.indices) {
        val (tl,br) = ranges[i]
        val (t,l) = tl
        val (b,r) = br
        val o = orders[i]
        when(o) {
            /* on */ 'n' -> {
            (t..b).forEach{y->(l..r).forEach{x->grid2[y][x]++}}
        }
            /* toggle */ 'e' -> {
            (t..b).forEach{y->(l..r).forEach{x->grid2[y][x] += 2}}
        }
            /* off */ 'f' -> {
            (t..b).forEach{y->(l..r).forEach{x->grid2[y][x] = (grid2[y][x] - 1).coerceAtLeast(0)}}
        }
            else -> println("OOPPSIEEEE!!!!").also { exitProcess(999) }
        }
    }
    println(grid2.sumOf{it.sum()})
    return
}

fun d5() {
    var inp = rdlines(5)
//    inp = listOf("ugknbfddgicrmopn","aaa","jchzalrnumimnmhp","haegwjzuvuyypxyu","dvszwmarrgswjxmb")
    fun isNice(s: String): Boolean {
        if (s.count{it in "aeiou"} < 3) return false
        if (s.windowed(2).all{ab->ab.first()!=ab.last()}) return false
        return listOf("ab","cd","pq","xy").all{!s.contains(it)}
    }
    val niceList = inp.map(::isNice)
    println(niceList.count{it})
//    inp = listOf("qjhvhtzxzqqjkmpb","xxyxx","uurcxstgmygtbstg","ieodomkazucvgmuy","aaa")
    fun isNice2(s: String): Boolean {
        val pairs = s.windowed(2)
        val counts = pairs.toSet().map{p->p to pairs.count{p == it}}.sortedBy{-it.second}
        if (counts.first().second == 1) return false
        val candidates = counts.filter{it.second != 1}.map{it.first}
        val indicesOfCandidates = candidates.map{cand->pairs.indices.filter{pairs[it] == cand}}
        if (indicesOfCandidates.all{it.last() - it.first() == 1}) return false
        val singles = s.toSet().map{c->s.indices.filter{s[it] == c}}
        if (singles.all{it.windowed(2).all{it.let{(a,b)->b-a != 2}}}) return false
        return true
    }
    val niceList2 = inp.map(::isNice2)
    println(niceList2.count{it})
    return
}

fun d4() {
    val inp = "bgvyzdsv"
    var n = 0
    val md = MessageDigest.getInstance("MD5")
    while(true) {
        val s = inp + n++.toString()
        md.update(s.encodeToByteArray())
        val hash = md.digest()
        if ((0..1).any{hash[it] != 0.toByte()}) continue
        if (hash[2].and(0xf0.toByte()) == 0.toByte()) {
            println("$s -> ${hash.joinToString(separator = " "){it.toString(16)}}")
            break
        }
    }
    n=0
    while(true) {
        val s = inp + n++.toString()
        md.update(s.encodeToByteArray())
        val hash = md.digest()
        if ((0..1).any{hash[it] != 0.toByte()}) continue
        if (hash[2].and(0xff.toByte()) == 0.toByte()) {
            println("$s -> ${hash.joinToString(separator = " "){it.toString(16)}}")
            break
        }
    }
}

fun d3() {
    var inp = rdday(3)
    fun dir(c: Char) = when(c) {
        '^' -> 0 to 1
        'v' -> 0 to -1
        '<' -> -1 to 0
        '>' -> 1 to 0
        else -> 0 to 0
    }
    operator fun Pair<Int,Int>.plus(other: Pair<Int,Int>) = let{(x,y)->other.let{(x2,y2)->x+x2 to y+y2}}
    val places = inp.scan(0 to 0){acc, c -> acc+dir(c)}
    val visited = places.toSet()
    println(visited.size)
    val places2 = inp.chunked(2).map{it.toCharArray()}.scan((0 to 0) to (0 to 0)){(acc1,acc2),(c1,c2)->acc1+dir(c1) to acc2+dir(c2)}
    val places2real = places2.flatMap{it.toList()}.toSet()
    println(places2real.size)
}

fun d2() {
    var inp = rdlines(2)
    val dims = inp.map{it.split('x').map{it.toInt()}}
    fun needed(dims: List<Int>): Int {
        val areas = dims.map{(dims - it).fold(1,Int::times)}
        val sorted = areas.sorted()
        return 2*sorted.sum() + sorted.first()
    }
    val needs = dims.map(::needed)
    val test1 = needed(listOf(2,3,4))
    val test2 = needed(listOf(1,1,10))
    println(needs.sum())
    fun ribbonNeeded(dims: List<Int>): Int {
        val distancesAround = dims.map{(dims-it).sum()*2}
        val toWrap = distancesAround.min()
        val volume = dims.fold(1,Int::times)
        return toWrap + volume
    }
    val test3 = ribbonNeeded(listOf(2,3,4))
    val test4 = ribbonNeeded(listOf(1,1,10))
    println(dims.map(::ribbonNeeded).sum())
    return
}

fun d1() {
    var inp = rdday(1)
    fun dir(c: Char) = when(c) {
        '('-> 1
        ')'->-1
        else->0
    }
    println(inp.sumOf{dir(it)})
    val positions = inp.scan(0){acc,it->acc+dir(it)}
    println(positions.indexOf(-1))
    return
}
