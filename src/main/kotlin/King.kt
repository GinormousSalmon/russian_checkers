class King(ch: Checker) : Checker(ch.image, ch.color, ch.possibleMoves) {

    override fun canAttack(): Boolean {
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                val (moves, enemy) = this.getMoves(dx, dy)
                var found = false
                for ((xMove, yMove) in moves)
                    if (this.checkSecondAttack(xMove, yMove, enemy)) {
                        this.possibleMoves.add(Triple(xMove, yMove, enemy))
                        found = true
                    }
                if (!found) {
                    this.possibleMoves.addAll(moves)
                }
            }
        return this.possibleMoves.isNotEmpty()
    }

    private fun checkSecondAttack(x: Int, y: Int, avoidEnemy: Pair<Int, Int>?): Boolean {
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1))
                if (getMoves(dx, dy, x, y, avoidEnemy).first.isNotEmpty())
                    return true
        return false
    }

    private fun getMoves(
        dx: Int,
        dy: Int,
        xStart: Int? = null,
        yStart: Int? = null,
        avoidEnemy: Pair<Int, Int>? = null
    ): Pair<List<Triple<Int, Int, Pair<Int, Int>>>, Pair<Int, Int>?> {
        val (x, y) = if (xStart == null || yStart == null)
            this.image.properties?.get("gridpane-column") as Int to this.image.properties?.get("gridpane-row") as Int
        else xStart to yStart
        val turn = this.color
        var k = 1
        val moves = mutableListOf<Triple<Int, Int, Pair<Int, Int>>>()
        var enemy: Pair<Int, Int>? = null
        while (x + dx * k in 0..7 && y + dy * k in 0..7) {
            val currentX = x + dx * k
            val currentY = y + dy * k
            val currentColor = desk.get(currentX, currentY)?.color
            if (currentColor == turn)
                break
            if (currentColor == turn.invert()) {
                if (avoidEnemy == Pair(currentX, currentY) || enemy != null)
                    break
                if (currentX + dx in 0..7 && currentY + dy in 0..7)
                    if (desk.get(currentX + dx, currentY + dy)?.color == null) {
                        enemy = Pair(currentX, currentY)
                        moves.add(Triple(currentX + dx, currentY + dy, enemy))
                    } else {
                        break
                    }
            }
            if (currentColor == null && enemy != null)
                if (!moves.contains(Triple(currentX, currentY, enemy)))
                    moves.add(Triple(currentX, currentY, enemy))
            k += 1
        }
        return Pair(moves, enemy)
    }

    override fun canMove(): Boolean {
        val x = this.image.properties?.get("gridpane-column") as Int
        val y = this.image.properties?.get("gridpane-row") as Int
        var canAnyMove = false
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                var k = 1
                while (x + dx * k in 0..7 && y + dy * k in 0..7) {
                    val currentX = x + dx * k
                    val currentY = y + dy * k
                    if (desk.get(currentX, currentY)?.color == null) {
                        this.possibleMoves.add(Triple(currentX, currentY, null))
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