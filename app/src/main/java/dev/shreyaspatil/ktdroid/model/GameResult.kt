package dev.shreyaspatil.ktdroid.model

data class GameResult(
    val username: Pair<String, String>,
    val totalRepos: Pair<Int, Int>,
    val followers: Pair<Int, Int>,
    val totalStars: Pair<Int, Int>

) {
    var score = Pair(0.0, 0.0)
        private set
        get() = (totalStars / totalRepos) + followers

    operator fun Pair<Int, Int>.div(other: Pair<Int, Int>): Pair<Double, Double> {
        return Pair(
            if (other.first == 0) 0.0 else (this.first / other.first.toDouble()),
            if (other.second == 0) 0.0 else (this.second / other.second.toDouble())
        )
    }

    operator fun Pair<Double, Double>.plus(other: Pair<Int, Int>): Pair<Double, Double> {
        return Pair(
            (this.first + other.first),
            (this.second + other.second)
        )
    }
}
