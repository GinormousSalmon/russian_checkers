package view

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.*
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*
import Checker
import King


class MainView : View("rus checkers") {
    override val root = VBox()
    private val fontSizeDivider = 15
    private val initialWidth = 600.0
    private lateinit var lbl: Label
    private lateinit var restartButton: Button
    private lateinit var surrenderButton: Button
    private var gridpane: GridPane
    private var turn = WHITE

    private val desk = mutableListOf<MutableList<Checker?>>()
    private val tiles = mutableListOf<MutableList<Rectangle>>()
    private var selectedX = -1
    private var selectedY = -1

    init {
        currentStage?.widthProperty()?.onChange {
            lbl.font = Font(currentStage!!.width / fontSizeDivider)
            restartButton.font = Font(currentStage!!.width / 25)
            surrenderButton.font = Font(currentStage!!.width / 25)
            currentStage?.maxHeight = currentStage!!.width + lbl.font.size * 1.5 + 40
            currentStage?.minHeight = currentStage!!.width + lbl.font.size * 1.5 + 40
        }
        with(root) {
            gridpane = gridpane {
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
                spawn(this)
            }
            borderpane {
                paddingTopProperty.bind(root.widthProperty().divide(50))
                left {
                    paddingLeftProperty.bind(root.widthProperty().divide(50))
                    lbl = label("White turn")
                }
                center {
                    paddingRightProperty.bind(root.widthProperty().divide(50))
                    restartButton = button {
                        text = "Restart"
                        action { restart() }
                    }
                }
                right {
//                    paddingRightProperty.bind(root.widthProperty().divide(50))
                    surrenderButton = button {
                        text = "Surrender"
                        action { surrender() }
                    }
                }
            }
        }
        currentStage?.width = initialWidth
        calcPossibleMoves()
    }

    private fun spawn(gp: GridPane) {
        with(gp) {
            for (i in 0..7) {
                desk.add(mutableListOf())
                for (j in 0..7)
                    desk[i].add(null)
            }
            Checker.desk = desk
            for (row in (1..2) + (5..6)) {
//                for (row in (0..2) + (5..7)) {
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
                        desk[column][row] = Checker(imv, if (color == "black") BLACK else WHITE, isKing = false)
                    }
            }
        }
    }

    private fun click(column: Int, row: Int) {
        if (desk[column][row]?.color == WHITE)
            println("selected $column $row king: ${desk[column][row]?.isKing} WHITE ${desk[column][row]?.javaClass}")
        else
            println("selected $column $row king: ${desk[column][row]?.isKing} BLACK ${desk[column][row]?.javaClass}")
        selectedX = column
        selectedY = row
        deskClear()
        drawPossibleMoves(column, row)
    }


    private fun tileClick(color: Paint, newX: Int, newY: Int) {
        if (color == GREEN) {
            deskClear()

            desk[selectedX][selectedY]?.move(newX, newY)
//            desk[selectedX][selectedY]?.image?.gridpaneConstraints { columnRowIndex(newX, newY) }
            desk[newX][newY] = desk[selectedX][selectedY]
            desk[selectedX][selectedY] = null

            if (newY == 0 && turn == WHITE || newY == 7 && turn == BLACK) {
                desk[newX][newY]?.isKing = true
                desk[newX][newY] = King(desk[newX][newY])
                Image("file:src/main/resources/$color.png")
                val col = if (turn == WHITE) "white" else "black"
                desk[newX][newY]?.image?.image = Image("file:src/main/resources/${col}_king.png")
            }

            var changeTurn = true
            if (kotlin.math.abs(selectedX - newX) >= 2) {
                println("attack")
                println("newX $newX newY $newY selX $selectedX selY $selectedY")
                val listX = until(newX, selectedX)
                val listY = until(newY, selectedY)
                for (i in listX.indices)
                    if (desk[listX[i]][listY[i]]?.color != null) {
                        val xDel = listX[i]
                        val yDel = listY[i]
                        desk[xDel][yDel]?.image?.hide()
                        desk[xDel][yDel] = null
//                        desk[xDel][yDel]?.color = null
//                        desk[xDel][yDel]?.image = null
//                        desk[xDel][yDel]?.possibleMoves?.clear()
                        changeTurn = !calcPossibleAttacks(newX, newY)
                        break
                    }
            }

            if (changeTurn) {
                turn = turn.invert()
                lbl.text = (if (turn == WHITE) "White" else "Black") + " turn"
                if (!calcPossibleAttacks())
                    if (!calcPossibleMoves()) {
                        lbl.text = (if (turn == BLACK) "White" else "Black") + " won"
                    }
            }
        }
    }

    private fun surrender() {
        lbl.text = (if (turn == BLACK) "White" else "Black") + " won"
        deskClear()
        for (column in desk)
            for (ch in column)
                ch?.possibleMoves?.clear()
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
            return desk[selectedX][selectedY]?.canAttack() ?: false
//            return canAttack(selectedX, selectedY)
        }
        var canAnyAttack = false
        for (x in 0..7)
            for (y in 0..7) {
                if (desk[x][y]?.color == turn) {
                    canAnyAttack = (desk[x][y]?.canAttack() ?: false) || canAnyAttack
//                    canAnyAttack = canAttack(x, y) || canAnyAttack
                }
            }
        return canAnyAttack
    }

    private fun calcPossibleMoves(): Boolean {
        var canAnyMove = false
        for (x in 0..7)
            for (y in 0..7) {
                if (desk[x][y]?.color == turn)
                    canAnyMove = (desk[x][y]?.canMove() ?: false) || canAnyMove
            }
        return canAnyMove
    }

    private fun drawPossibleMoves(x: Int, y: Int) {
        if (desk[x][y]?.color == turn)
            for (move in desk[x][y]?.possibleMoves!!)
                tiles[move.first][move.second].fill = GREEN
    }

    private fun deskClear() {
        for (i in 0..7)
            for (j in 0..7)
                tiles[i][j].fill = if ((i + j) % 2 == 0) WHITE else color(80.0 / 255, 40.0 / 255, 30.0 / 255)
    }

    private fun movesClear() {
        for (x in 0..7)
            for (y in 0..7)
                desk[x][y]?.possibleMoves?.clear()
    }

    private fun restart() {
        deskClear()
        lbl.text = "White turn"
        turn = WHITE
        for (a in desk)
            for (b in a)
                b?.image?.hide()
        desk.clear()
        spawn(gridpane)
        calcPossibleMoves()
    }
}
