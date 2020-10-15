package view

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.effect.Effect
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Color.*
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import org.omg.CORBA.Object
import tornadofx.*


class MainView : View("rus checkers") {
    override val root = VBox()
    private val fontSizeDivider = 15
    private val radiusDivider = 22
    private val initialWidth = 600.0
    private lateinit var lbl: Label
    private var turn = WHITE

    class Ch(
        var image: ImageView? = null,
        var color: Color? = null,
        val possibleMoves: MutableList<Pair<Int, Int>> = mutableListOf(),
        var isKing: Boolean = false
    )

    private val desk = mutableListOf<MutableList<Ch>>()
    private val tiles = mutableListOf<MutableList<Rectangle>>()
    private var selectedX = -1
    private var selectedY = -1

    init {
        currentStage?.widthProperty()?.onChange {
            lbl.font = Font(currentStage!!.width / fontSizeDivider)
            currentStage?.maxHeight = currentStage!!.width + lbl.font.size * 1.5 + 30
            currentStage?.minHeight = currentStage!!.width + lbl.font.size * 1.5 + 30
        }
        with(root) {
            gridpane {
                for (i in 0..7) {
                    tiles.add(mutableListOf())
                    for (j in 0..7)
                        tiles[i].add(rectangle {
                            fill = if ((i + j) % 2 == 0) WHITE else color(80.0 / 255, 40.0 / 255, 30.0 / 255)
                            widthProperty().bind(root.widthProperty().divide(8))
                            heightProperty().bind(root.widthProperty().divide(8))
                            gridpaneConstraints { columnRowIndex(i, j) }
                            onLeftClick { tileClick(this.fill, i, j) }
                        })
                }
                for (i in 0..7) {
                    desk.add(mutableListOf())
                    for (j in 0..7)
                        desk[i].add(Ch())
                }
                for (row in (1..2) + (5..6)) {
                    val color = if (row == 1) "white" else "black"
//                    val color = if (row < 3) "black" else "white"
                    for (column in 0..7)
                        if ((row + column) % 2 == 1) {
                            val imv = imageview {
                                image = Image("file:src/main/resources/$color.png")
                                fitHeightProperty().bind(root.widthProperty().divide(10))
                                fitWidthProperty().bind(root.widthProperty().divide(10))
                                gridpaneConstraints {
                                    columnRowIndex(column, row)
                                    hAlignment = HPos.CENTER
                                    vAlignment = VPos.CENTER
                                }
                                onLeftClick {
                                    println(properties)
                                    click(properties["gridpane-column"] as Int, properties["gridpane-row"] as Int)
                                }
                            }
                            desk[column][row].image = imv
                            desk[column][row].color = if (color == "black") BLACK else WHITE
                        }
                }
            }
            lbl = label("White turn")
            lbl.textFill = BLACK
        }
        currentStage?.width = initialWidth
        calcPossibleMoves()
    }

    private fun click(column: Int, row: Int) {
        if (desk[column][row].color == WHITE)
            println("selected $column $row king: ${desk[column][row].isKing} WHITE")
        else
            println("selected $column $row king: ${desk[column][row].isKing} BLACK")
        selectedX = column
        selectedY = row
        deskClear()
        drawPossibleMoves(column, row)
    }


    private fun tileClick(color: Paint, newX: Int, newY: Int) {
        if (color == GREEN) {
            deskClear()
            desk[selectedX][selectedY].image?.gridpaneConstraints { columnRowIndex(newX, newY) }
            desk[newX][newY].image = desk[selectedX][selectedY].image
            desk[newX][newY].isKing = desk[selectedX][selectedY].isKing
            desk[newX][newY].color = turn

            desk[selectedX][selectedY].color = null
            desk[selectedX][selectedY].image = null
            desk[selectedX][selectedY].isKing = false

            if (newY == 0 && turn == WHITE || newY == 7 && turn == BLACK) {
                desk[newX][newY].isKing = true
                Image("file:src/main/resources/$color.png")
                val col = if (desk[newX][newY].color == WHITE) "white" else "black"
                desk[newX][newY].image?.image = Image("file:src/main/resources/${col}_king.png")
            }

            var changeTurn = true
            if (kotlin.math.abs(selectedX - newX) >= 2) {
                println("attack")
                println("newx $newX newy $newY selX $selectedX selY $selectedY")
                val listX = until(newX, selectedX)
                val listY = until(newY, selectedY)
                for (i in listX.indices)
                    if (desk[listX[i]][listY[i]].color != null) {
                        val xDel = listX[i]
                        val yDel = listY[i]
                        desk[xDel][yDel].color = null
                        desk[xDel][yDel].image?.hide()
                        desk[xDel][yDel].image = null
                        changeTurn = !calcPossibleAttacks(newX, newY)
                        break
                    }
            }

            if (changeTurn) {
                turn = turn.invert()
                lbl.text = (if (turn == WHITE) "White" else "Black") + " turn"
                if (!calcPossibleAttacks())
                    calcPossibleMoves()
            }
        }
    }

    private fun until(a: Int, b: Int): List<Int> {
        val result = mutableListOf<Int>()
        var start = a + if (a < b) 1 else -1
        do {
            result.add(start)
            start += if (a < b) 1 else -1
        } while (start != b)
        return result.toList()
    }

    private fun calcPossibleAttacks(selectedX: Int = -1, selectedY: Int = -1): Boolean {
        movesClear()
        if (selectedX != -1) {
            return canAttack(selectedX, selectedY)
        }
        var canAnyAttack = false
        for (x in 0..7)
            for (y in 0..7) {
                if (desk[x][y].color == turn) {
                    canAnyAttack = canAttack(x, y) || canAnyAttack
                }
            }
        return canAnyAttack
    }

    private fun canAttack(x: Int, y: Int): Boolean {
        if (desk[x][y].isKing) {
            return canKingAttack(x, y)
        } else {
            for (i in listOf(-2, 2))
                for (j in listOf(-2, 2))
                    if (x + i in 0..7 && y + j in 0..7)
                        if (desk[x + i][y + j].color == null && desk[x + i / 2][y + j / 2].color == desk[x][y].color?.invert())
                            desk[x][y].possibleMoves.add(Pair(x + i, y + j))
        }
        return desk[x][y].possibleMoves.isNotEmpty()
    }

    private fun canKingAttack(x: Int, y: Int): Boolean {
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                var k = 1
                var foundEnemy = false
                while (x + dx * k in 0..7 && y + dy * k in 0..7) {
                    val currentX = x + dx * k
                    val currentY = y + dy * k
                    println("curX $currentX currentY $currentY")
                    val currentColor = desk[currentX][currentY].color
                    if (currentColor == turn)
                        break
                    if (currentColor == turn.invert()) {
                        if (foundEnemy)
                            break
                        if (currentX + dx in 0..7 && currentY + dy in 0..7)
                            if (desk[currentX + dx][currentY + dy].color == null) {
                                desk[x][y].possibleMoves.add(Pair(currentX + dx, currentY + dy))
                                foundEnemy = true
                            } else {
                                break
                            }
                    }
                    if (currentColor == null && foundEnemy)
                        desk[x][y].possibleMoves.add(Pair(currentX, currentY))
                    k += 1
                }
            }
        return desk[x][y].possibleMoves.isNotEmpty()
    }

    private fun canKingMove(x: Int, y: Int) {
        for (dx in listOf(-1, 1))
            for (dy in listOf(-1, 1)) {
                var k = 1
                while (x + dx * k in 0..7 && y + dy * k in 0..7) {
                    val currentX = x + dx * k
                    val currentY = y + dy * k
                    if (desk[currentX][currentY].color == null)
                        desk[x][y].possibleMoves.add(Pair(currentX, currentY))
                    else
                        break
                    k += 1
                }
            }
    }

    private fun calcPossibleMoves() {
        for (x in 0..7)
            for (y in 0..7) {
                if (desk[x][y].isKing)
                    canKingMove(x, y)
                else
                    canMove(x, y)
//                if (desk[x][y].possibleMoves.isEmpty()) { ???

//                }
            }
    }

    private fun canMove(x: Int, y: Int) {
        val addition = if (desk[x][y].color == WHITE) -1 else 1
        if (y + addition in 0..7) {
            if (x <= 6)
                if (desk[x + 1][y + addition].color == null)
                    desk[x][y].possibleMoves.add(Pair(x + 1, y + addition))
            if (x >= 1)
                if (desk[x - 1][y + addition].color == null)
                    desk[x][y].possibleMoves.add(Pair(x - 1, y + addition))
        }
    }


    private fun drawPossibleMoves(x: Int, y: Int) {
        if (desk[x][y].color == turn)
            for (move in desk[x][y].possibleMoves)
                tiles[move.first][move.second].fill = GREEN
    }


    private fun deskClear() {
        for (i in 0..7)
            for (j in 0..7)
                tiles[i][j].fill = if ((i + j) % 2 == 0) WHITE else color(80.0 / 255, 40.0 / 255, 30.0 / 255)
    }

    private fun movesClear(){
        for (x in 0..7)
            for (y in 0..7)
                desk[x][y].possibleMoves.clear()
    }
}
