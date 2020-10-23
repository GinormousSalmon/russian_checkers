package view

import tornadofx.*
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.*
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import checker.Checker
import Movement
import Desk


class MainView : View("russian checkers") {
    override val root = VBox()
    private val fontSizeDivider = 15
    private val buttonFontSizeDivider = 25
    private val checkerSizeDivider = 10
    private val initialWidth = 600.0
    private val paddingDivider = 50
    private val fontSizeMultiplier = 1.5
    private val blackTileColor = color(80.0 / 255, 40.0 / 255, 30.0 / 255)
    private var gridpane: GridPane
    private var movement: Movement
    private var desk: Desk
    private lateinit var lbl: Label
    private lateinit var restartButton: Button
    private lateinit var surrenderButton: Button
    private val tiles = mutableListOf<MutableList<Rectangle>>()

    private var selectedX = -1
    private var selectedY = -1

    init {
        desk = Desk(tiles)
        currentStage?.widthProperty()?.onChange {
            lbl.font = Font(currentStage!!.width / fontSizeDivider)
            restartButton.font = Font(currentStage!!.width / buttonFontSizeDivider)
            surrenderButton.font = Font(currentStage!!.width / buttonFontSizeDivider)
            currentStage?.maxHeight = currentStage!!.width + lbl.font.size * fontSizeMultiplier + 40
            currentStage?.minHeight = currentStage!!.width + lbl.font.size * fontSizeMultiplier + 40
        }
        with(root) {
            gridpane = gridpane {
                for (i in 0..7) {
                    tiles.add(mutableListOf())
                    for (j in 0..7)
                        tiles[i].add(rectangle {
                            fill = if ((i + j) % 2 == 0) WHITE else blackTileColor
                            widthProperty().bind(root.widthProperty().divide(8))
                            heightProperty().bind(root.widthProperty().divide(8))
                            gridpaneConstraints { columnRowIndex(i, j) }
                            onLeftClick { tileClick(fill, i, j) }
                        })
                }
                spawn(this)
            }
            borderpane {
                paddingTopProperty.bind(root.widthProperty().divide(paddingDivider))
                left {
                    paddingLeftProperty.bind(root.widthProperty().divide(paddingDivider))
                    lbl = label("White turn")
                }
                center {
                    paddingRightProperty.bind(root.widthProperty().divide(paddingDivider))
                    restartButton = button {
                        text = "Restart"
                        action { restart() }
                    }
                }
                right {
                    surrenderButton = button {
                        text = "Surrender"
                        action { surrender() }
                    }
                }
            }
        }
        currentStage?.width = initialWidth
        movement = Movement(desk)
        movement.calcPossibleMoves()
    }

    private fun spawn(gp: GridPane) {
        with(gp) {
            Checker.setDesk(desk)
            for (row in (0..2) + (5..7)) {
                val color = if (row < 3) "black" else "white"
                for (column in 0..7)
                    if ((row + column) % 2 == 1) {
                        val imv = imageview {
                            image = Image("file:src/main/resources/$color.png")
                            fitHeightProperty().bind(root.widthProperty().divide(checkerSizeDivider))
                            fitWidthProperty().bind(root.widthProperty().divide(checkerSizeDivider))
                            gridpaneConstraints {
                                columnRowIndex(column, row)
                                hAlignment = HPos.CENTER
                                vAlignment = VPos.CENTER
                            }
                            onLeftClick {
                                checkerClick(properties["gridpane-column"] as Int, properties["gridpane-row"] as Int)
                            }
                        }
                        desk.set(column, row, Checker(imv, if (color == "black") BLACK else WHITE))
                    }
            }
        }
    }

    private fun checkerClick(column: Int, row: Int) {
        val col = if (desk.get(column, row)?.color == WHITE) "WHITE" else "BLACK"
        println("selected $column $row ${desk.get(column, row)?.javaClass} " + col)
        selectedX = column
        selectedY = row
        desk.tilesClear()
        desk.drawPossibleMoves(column, row)
    }

    private fun tileClick(color: Paint, newX: Int, newY: Int) {
        if (color == GREEN) {
            desk.tilesClear()

            val attack = desk.move(selectedX, selectedY, newX, newY)
            if (newY == 0 && movement.turn == WHITE || newY == 7 && movement.turn == BLACK)
                desk.makeKing(newX, newY)
            var changeTurn = true
            if (attack)
                changeTurn = !movement.calcPossibleAttacks(newX, newY)
            if (changeTurn) {
                movement.invertTurn()
                setText(movement.turn, "turn")
                if (!movement.calcPossibleAttacks())
                    if (!movement.calcPossibleMoves())
                        setText(movement.turn.invert(), "won", GREEN)
            }
        }
        if (desk.draw()) {
            lbl.text = "Draw"
            lbl.textFill = RED
            desk.tilesClear()
            movement.movesClear()
        }
    }

    private fun surrender() {
        setText(movement.turn.invert(), "won", GREEN)
        desk.tilesClear()
        movement.movesClear()
    }

    private fun restart() {
        setText(WHITE, "turn")
        desk.tilesClear()
        desk.hideAll()
        desk = Desk(tiles)
        spawn(gridpane)
        movement = Movement(desk)
        movement.calcPossibleMoves()
    }

    private fun setText(turn: Color, add: String, color: Color = BLACK) {
        lbl.text = (if (turn == WHITE) "White" else "Black") + " " + add
        lbl.textFill = color
    }
}
