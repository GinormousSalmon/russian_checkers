package view

import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Color.*
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*


class MainView : View("rus checkers") {
    override val root = VBox()
    private val fontSizeDivider = 15
    private val radiusDivider = 22
    private val initialWidth = 500.0
    private lateinit var lbl: Label
    private var turn = WHITE

    class Ch(
        var circle: Circle? = null,
        var color: Color? = null,
        val possibleMoves: MutableList<Pair<Int, Int>> = mutableListOf()
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
                            onLeftClick { tileClick(this, i, j) }
                        })
                }
                for (i in 0..7) {
                    desk.add(mutableListOf())
                    for (j in 0..7)
                        desk[i].add(Ch())
                }
                for (row in (0..2) + (5..7)) {
                    val color = if (row < 3) BLACK else WHITE
                    for (column in 0..7)
                        if ((row + column) % 2 == 1) {
                            val c = circle {
                                fill = color
                                radiusProperty().bind(root.widthProperty().divide(radiusDivider))
                                centerXProperty().bind(root.widthProperty().divide(40))
                                centerYProperty().bind(root.widthProperty().divide(40))
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
                            desk[column][row].circle = c
                            if (row < 3)
                                desk[column][row].color = BLACK
                            else
                                desk[column][row].color = WHITE
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
            println("selected $column $row WHITE")
        else
            println("selected $column $row BLACK")
        selectedX = column
        selectedY = row
        clear()
        drawPossibleMoves(column, row)
    }


    private fun tileClick(rectangle: Rectangle, newX: Int, newY: Int) {
        if (rectangle.fill == GREEN) {
            clear()
            println("circle - " + desk[selectedX][selectedY].circle)
            desk[selectedX][selectedY].circle?.gridpaneConstraints { columnRowIndex(newX, newY) }
            desk[newX][newY].circle = desk[selectedX][selectedY].circle
            desk[selectedX][selectedY].color = null
            desk[newX][newY].color = turn

            var changeTurn = true
            if (kotlin.math.abs(selectedX - newX) == 2) {
                println("attack")
                val xMed = (selectedX + newX) / 2
                val yMed = (selectedY + newY) / 2
                desk[xMed][yMed].color = null
                desk[xMed][yMed].circle?.hide()
                desk[xMed][yMed].circle = null
                println("newx $newX newy $newY")
                changeTurn = !calcPossibleAttacks(newX, newY)
            }

            if (changeTurn) {
                if (!calcPossibleAttacks())
                    calcPossibleMoves()
                turn = turn.invert()
                lbl.text = (if (turn == WHITE) "White" else "Black") + " turn"
            }
        }
    }

    private fun calcPossibleAttacks(selectedX: Int = -1, selectedY: Int = -1): Boolean {
        var canAnyAttack = false
        if (selectedX != -1) {
            println("fffuck")
            return canAttack(selectedX, selectedY)
        }
        for (x in 0..7)
            for (y in 0..7) {
                if (desk[x][y].color == turn.invert()) {
                    canAnyAttack = canAttack(x, y) || canAnyAttack
                }
            }
        return canAnyAttack
    }

    private fun canAttack(x: Int, y: Int): Boolean {
        var canAnyAttack = false
        desk[x][y].possibleMoves.clear()
        for (i in listOf(-2, 2))
            for (j in listOf(-2, 2))
                if (x + i in 0..7 && y + j in 0..7)
                    if (desk[x + i][y + j].color == null && desk[x + i / 2][y + j / 2].color == desk[x][y].color?.invert()) {
                        desk[x][y].possibleMoves.add(Pair(x + i, y + j))
                        canAnyAttack = true
                    }
        return canAnyAttack
    }

    private fun calcPossibleMoves() {
        for (x in 0..7)
            for (y in 0..7)
                if (desk[x][y].possibleMoves.isEmpty()) {
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
    }


    private fun drawPossibleMoves(x: Int, y: Int) {
        if (desk[x][y].color == turn)
            for (move in desk[x][y].possibleMoves)
                tiles[move.first][move.second].fill = GREEN
    }


    private fun clear() {
        for (i in 0..7)
            for (j in 0..7)
                tiles[i][j].fill = if ((i + j) % 2 == 0) WHITE else color(80.0 / 255, 40.0 / 255, 30.0 / 255)
    }
}
