class King(ch: Checker?) : Checker(ch?.image, ch?.color, ch?.possibleMoves, ch?.isKing ?: false) {

    override fun canAttack(): Boolean {
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                val (moves, enemyDir) = this.getMoves(dx, dy)
                var found = false
                for ((xMove, yMove) in moves)
                    if (this.checkSecondAttack(xMove, yMove, enemyDir)) {
                        this.possibleMoves?.add(Pair(xMove, yMove))
                        found = true
                    }
                if (!found)
                    this.possibleMoves?.addAll(moves)
            }
        return this.possibleMoves?.isNotEmpty() ?: false
    }

    private fun checkSecondAttack(x: Int, y: Int, enemyDir: Pair<Int, Int>?): Boolean {
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                if (enemyDir != Pair(-dx, -dy))
                    if (getMoves(dx, dy, x, y).first.isNotEmpty()) return true
            }
        return false
    }

    private fun getMoves(dx: Int, dy: Int, xStart: Int? = null, yStart: Int? = null): Pair<List<Pair<Int, Int>>, Pair<Int, Int>?> {
        val (x, y) = if (xStart == null || yStart == null)
            this.image?.properties?.get("gridpane-column") as Int to this.image?.properties?.get("gridpane-row") as Int
        else xStart to yStart
        val turn = this.color
        var k = 1
        val moves = mutableListOf<Pair<Int, Int>>()
        var enemyDir: Pair<Int, Int>? = null
        while (x + dx * k in 0..7 && y + dy * k in 0..7) {
            val currentX = x + dx * k
            val currentY = y + dy * k
            val currentColor = desk[currentX][currentY]?.color
            if (currentColor == turn)
                break
            if (currentColor == turn?.invert()) {
                if (enemyDir != null)
                    break
                if (currentX + dx in 0..7 && currentY + dy in 0..7)
                    if (desk[currentX + dx][currentY + dy]?.color == null) {
                        enemyDir = Pair(dx, dy)
                        moves.add(Pair(currentX + dx, currentY + dy))
                    } else {
                        break
                    }
            }
            if (currentColor == null && enemyDir != null)
                moves.add(Pair(currentX, currentY))
            k += 1
        }
        return Pair(moves, enemyDir)
    }

    override fun canMove(): Boolean {
        val x = this.image?.properties?.get("gridpane-column") as Int
        val y = this.image?.properties?.get("gridpane-row") as Int
        var canAnyMove = false
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                var k = 1
                while (x + dx * k in 0..7 && y + dy * k in 0..7) {
                    val currentX = x + dx * k
                    val currentY = y + dy * k
                    if (desk[currentX][currentY]?.color == null) {
                        this.possibleMoves?.add(Pair(currentX, currentY))
                        canAnyMove = true
                    } else {
                        break
                    }
                    k += 1
                }
            }
        return canAnyMove
    }
}