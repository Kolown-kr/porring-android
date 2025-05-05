package com.kolown.porring

fun main() {
    val sol = Solution()
    val numbers = intArrayOf(200, 100, 20, 18, 16, 14, 12, 10, 5, 4, 3, 2, 1)

    println(sol.newSolution(3, numbers))
}

class Solution {
    fun newSolution(k: Int, score: IntArray): Int {
        val diffMap = mutableMapOf<Int, Int>()
        val deleteSet = mutableSetOf<Int>()

        for (i in 0 until score.size - 1) {
            val diff = score[i] - score[i + 1]
            diffMap[diff] = diffMap.getOrDefault(diff, 0) + 1
        }

        for (i in 0 until score.size - 1) {
            val diff = score[i] - score[i + 1]
            if (diffMap[diff]!! >= k) {
                deleteSet.add(i)
                deleteSet.add(i + 1)
            }
        }

        return score.indices.count { it !in deleteSet }
    }

    fun solution(k: Int, score: IntArray): Int {
        val diffList = mutableListOf<Int>()

        for(i in 0 until score.size - 1) {
            diffList.add(score[i] - score[i+1])
        }

        val diffCount = diffList.groupingBy { it }.eachCount()
        println(diffCount)

        val frequent = diffCount.filter { it.value >= k }.keys
        println(frequent)

        val deleteSet = mutableSetOf<Int>()
        for(i in diffList.indices) {
            if(diffList[i] in frequent) {
                println(deleteSet)
                deleteSet.add(i)
                deleteSet.add(i+1)
            }
        }

        val resultList = score.filterIndexed { index, _ -> index !in deleteSet}

        return resultList.size
    }
}