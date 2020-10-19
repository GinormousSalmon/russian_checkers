import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import tornadofx.gridpaneConstraints
import tornadofx.hide
import tornadofx.onLeftClick
import tornadofx.rectangle

class Desk(gp: GridPane, widthProp: ReadOnlyDoubleProperty, click:(color: Paint, newX: Int, newY: Int)->Unit) {
    private val desk = mutableListOf<MutableList<Checker?>>()
    private val tiles = mutableListOf<MutableList<Rectangle>>()

    init {
        for (i in 0..7) {
            desk.add(mutableListOf())
            for (j in 0..7)
                desk[i].add(null)
        }
        with(gp) {
            for (i in 0..7) {
                tiles.add(mutableListOf())
                for (j in 0..7)
                    tiles[i].add(rectangle {
                        fill = if ((i + j) % 2 == 0) WHITE else Color.color(80.0 / 255, 40.0 / 255, 30.0 / 255)
                        widthProperty().bind(widthProp.divide(8))
                        heightProperty().bind(widthProp.divide(8))
                        gridpaneConstraints { columnRowIndex(i, j) }
                        onLeftClick { click(this.fill, i, j) }
                    })
            }
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
        return false
    }

    fun makeKing(x: Int, y: Int) {
        val turn = desk[x][y]?.color
        desk[x][y] = King(desk[x][y]!!)
        val col = if (turn == WHITE) "white" else "black"
        desk[x][y]?.image?.image = Image("file:src/main/resources/${col}_king.png")
    }

    private fun remove(xy:Pair<Int,Int>) {
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
}