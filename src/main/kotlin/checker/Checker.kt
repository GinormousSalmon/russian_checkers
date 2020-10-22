package checker

import Desk
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import tornadofx.gridpaneConstraints


open class Checker(
    var image: ImageView,
    var color: Color,
    val possibleMoves: MutableList<Triple<Int, Int, Pair<Int, Int>?>> = mutableListOf()
) {
    companion object {
        internal lateinit var desk: Desk

        fun setDesk(d: Desk) {
            desk = d
        }
    }

    private var x: Int = 0
    private var y: Int = 0

    init {
        x = this.image.properties?.get("gridpane-column") as Int
        y = this.image.properties?.get("gridpane-row") as Int
    }

    fun move(toX: Int, toY: Int) {
        this.x = toX
        this.y = toY
        this.image.gridpaneConstraints { columnRowIndex(toX, toY) }
    }

    open fun canAttack(): Boolean {
        for (i in listOf(-2, 2))
            for (j in listOf(-2, 2))
                if (x + i in 0..7 && y + j in 0..7)
                    if (desk.get(x + i, y + j)?.color == null && desk.get(
                            x + i / 2,
                            y + j / 2
                        )?.color == this.color.invert()
                    )
                        this.possibleMoves.add(Triple(x + i, y + j, Pair(x + i / 2, y + j / 2)))
        return this.possibleMoves.isNotEmpty()
    }

    open fun canMove(): Boolean {
        var canAnyMove = false
        val addition = if (this.color == Color.WHITE) -1 else 1
        if (y + addition in 0..7) {
            if (x <= 6)
                if (desk.get(x + 1, y + addition)?.color == null) {
                    this.possibleMoves.add(Triple(x + 1, y + addition, null))
                    canAnyMove = true
                }
            if (x >= 1)
                if (desk.get(x - 1, y + addition)?.color == null) {
                    this.possibleMoves.add(Triple(x - 1, y + addition, null))
                    canAnyMove = true
                }
        }
        return canAnyMove
    }

    override fun toString(): String {
        return this.color.toString() + " " + this.possibleMoves.toString()
    }
}