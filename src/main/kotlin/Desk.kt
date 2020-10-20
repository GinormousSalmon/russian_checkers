import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import javafx.scene.shape.Rectangle
import tornadofx.hide

class Desk(private val tiles: MutableList<MutableList<Rectangle>>) {
    private val desk = mutableListOf<MutableList<Checker?>>()
    private var fifteenTurnsCount = 0

    init {
        desk.clear()
        for (i in 0..7) {
            desk.add(mutableListOf())
            for (j in 0..7)
                desk[i].add(null)
        }
    }

    fun get(x: Int, y: Int): Checker? {
        return desk[x][y]
    }

    fun set(x: Int, y: Int, checker: Checker?) {
        desk[x][y] = checker
    }

    fun move(xFrom: Int, yFrom: Int, xTo: Int, yTo: Int): Boolean {
        desk[xFrom][yFrom]?.move(xTo, yTo)
        desk[xTo][yTo] = desk[xFrom][yFrom]
        desk[xFrom][yFrom] = null
        val enemy = desk[xTo][yTo]?.possibleMoves?.filter { it.first == xTo && it.second == yTo }?.get(0)?.third
        if (enemy != null) {
            remove(enemy)
            return true
        }
        if (desk[xTo][yTo] is King)
            fifteenTurnsCount += 1
        else
            fifteenTurnsCount = 0
        return false
    }

    fun makeKing(x: Int, y: Int) {
        val turn = desk[x][y]?.color
        desk[x][y] = King(desk[x][y]!!)
        val col = if (turn == WHITE) "white" else "black"
        desk[x][y]?.image?.image = Image("file:src/main/resources/${col}_king.png")
    }

    private fun remove(xy: Pair<Int, Int>) {
        val (x, y) = xy
        desk[x][y]?.image?.hide()
        desk[x][y] = null
    }

    fun drawPossibleMoves(x: Int, y: Int) {
        for (move in desk[x][y]?.possibleMoves!!)
            tiles[move.first][move.second].fill = Color.GREEN
    }

    fun tilesClear() {
        for (i in 0..7)
            for (j in 0..7)
                tiles[i][j].fill = if ((i + j) % 2 == 0) WHITE else Color.color(80.0 / 255, 40.0 / 255, 30.0 / 255)
    }

    fun hideAll() {
        for (x in 0..7)
            for (y in 0..7)
                desk[x][y]?.image?.hide()
    }

    fun draw(): Boolean {
        return fifteenTurnsCount == 15
    }
}