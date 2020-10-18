import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import tornadofx.gridpaneConstraints


open class Checker(
    var image: ImageView? = null,
    var color: Color? = null,
    val possibleMoves: MutableList<Pair<Int, Int>>? = mutableListOf(),
    var isKing: Boolean = false
) {
    companion object {
        lateinit var desk: MutableList<MutableList<Checker?>>
    }

    private var x: Int = 0
    private var y: Int = 0

    init {
        x = this.image?.properties?.get("gridpane-column") as Int
        y = this.image?.properties?.get("gridpane-row") as Int
    }

    fun move(toX: Int, toY: Int) {
        this.x = toX
        this.y = toY
        this.image?.gridpaneConstraints { columnRowIndex(toX, toY) }
    }

    open fun canAttack(): Boolean {
//        val x = this.image?.properties?.get("gridpane-column") as Int
//        val y = this.image?.properties?.get("gridpane-row") as Int
        for (i in listOf(-2, 2))
            for (j in listOf(-2, 2))
                if (x + i in 0..7 && y + j in 0..7)
                    if (desk[x + i][y + j]?.color == null && desk[x + i / 2][y + j / 2]?.color == desk[x][y]?.color?.invert())
                        this.possibleMoves?.add(Pair(x + i, y + j))
        return this.possibleMoves?.isNotEmpty() ?: false
    }

    open fun canMove(): Boolean {
//        val x = this.image?.properties?.get("gridpane-column") as Int
//        val y = this.image?.properties?.get("gridpane-row") as Int
        var canAnyMove = false
        val addition = if (this.color == Color.WHITE) -1 else 1
        if (y + addition in 0..7) {
            if (x <= 6)
                if (desk[x + 1][y + addition]?.color == null) {
                    this.possibleMoves?.add(Pair(x + 1, y + addition))
                    canAnyMove = true
                }
            if (x >= 1)
                if (desk[x - 1][y + addition]?.color == null) {
                    this.possibleMoves?.add(Pair(x - 1, y + addition))
                    canAnyMove = true
                }
        }
        return canAnyMove
    }

}