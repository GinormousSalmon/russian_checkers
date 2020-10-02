package view


import Checker
import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import javafx.scene.paint.Color.BLACK
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import tornadofx.*
import java.awt.Point
import java.awt.geom.Point2D
import java.lang.Math.abs
import java.sql.Time
import kotlin.math.absoluteValue


class MainView : View("rus checkers") {
    override val root = VBox()
    private val fontSize = 30.0
    private val radiusDivider = 22
    private var lbl: Label
    private var state = 0   // 0 - white's turn, 1 - black's turn
    private val checkers = mutableListOf<Checker>()
    private val desk = mutableListOf<MutableList<Int>>()    //0 - empty, 1 - white, 2 - black
    private val tiles = mutableListOf<MutableList<Rectangle>>()
    private var selectedCheckerN = -1

    init {
        currentStage?.widthProperty()?.onChange {
            currentStage?.maxHeight = currentStage!!.width + fontSize * 1.5 + 30
            currentStage?.minHeight = currentStage!!.width + fontSize * 1.5 + 30
        }
        currentStage?.width = 500.0

        with(root) {
            gridpane {
                for (i in 0..7) {
                    tiles.add(mutableListOf())
                    for (j in 0..7)
                        tiles[i].add(rectangle {
                            fill = if ((i + j) % 2 == 0) Color.WHITE else Color.color(80.0 / 255, 40.0 / 255, 30.0 / 255)
                            widthProperty().bind(root.widthProperty().divide(8))
                            heightProperty().bind(root.widthProperty().divide(8))
                            gridpaneConstraints { columnRowIndex(i, j) }
                            onLeftClick { tileClick(this, i, j) }
                        })
                }
                for (i in 0..7) {
                    desk.add(mutableListOf())
                    for (j in 0..7)
                        desk[i].add(0)
                }

                for (row in 0..2)
                    for (column in 0..7)
                        if ((row + column) % 2 == 1) {
                            val c = circle {
                                fill = BLACK
                                radiusProperty().bind(root.widthProperty().divide(radiusDivider))
                                centerXProperty().bind(root.widthProperty().divide(40))
                                centerYProperty().bind(root.widthProperty().divide(40))
                                gridpaneConstraints {
                                    columnRowIndex(column, row)
                                    hAlignment = HPos.CENTER
                                }
                            }
                            val index = checkers.size
                            val ch = Checker(BLACK, Point(column, row), c)
                            c.onLeftClick { blackClick(ch, index) }
                            checkers.add(ch)
                            desk[column][row] = 2
                        }
                for (row in 5..7)
                    for (column in 0..7)
                        if ((row + column) % 2 == 1) {
                            val c = circle {
                                fill = WHITE
                                radiusProperty().bind(root.widthProperty().divide(radiusDivider))
                                centerXProperty().bind(root.widthProperty().divide(40))
                                centerYProperty().bind(root.widthProperty().divide(40))
                                gridpaneConstraints {
                                    columnRowIndex(column, row)
                                    hAlignment = HPos.CENTER
                                }
                            }
                            val index = checkers.size
                            val ch = Checker(WHITE, Point(column, row), c)
                            c.onLeftClick { whiteClick(ch, index) }
                            checkers.add(ch)
                            desk[column][row] = 1
                        }

            }
            lbl = label("White turn") {
                textFill = Color.BLACK
                font = Font(fontSize)
            }
        }
    }

    private fun blackClick(checker: Checker, index: Int) {
//        circle.gridpaneConstraints { columnRowIndex(pos.x, pos.y-2) }
        if (state == 1) {
//            lbl.text = "White turn"
            println("${checker.pos.x} ${checker.pos.y} BLACK")
            selectedCheckerN = index
            calcPossibleMoves(checker.circle, checker.pos)
        }
    }

    private fun whiteClick(checker: Checker, index: Int) {
//        circle.gridpaneConstraints { columnRowIndex(pos.x, pos.y-2) }
        if (state == 0) {
//            lbl.text = "Black turn"
            println("${checker.pos.x} ${checker.pos.y} WHITE")
            selectedCheckerN = index
            calcPossibleMoves(checker.circle, checker.pos)
        }
    }

    private fun tileClick(rectangle: Rectangle, x: Int, y: Int) {
        if (rectangle.fill == Color.GREEN) {
            clear()
            println(selectedCheckerN)
            val oldPos = checkers[selectedCheckerN].pos
            if (kotlin.math.abs(oldPos.x - x) == 2) {
                val xMed = ((oldPos.x + x) / 2).toInt()
                val yMed = ((oldPos.y + y) / 2).toInt()
                desk[xMed][yMed] = 0
                val temp = checkers.find { it.pos == Point(xMed, yMed) }
                temp?.circle?.hide()
                temp?.pos = Point(-10, -10)
                temp?.circle?.onLeftClick { nothing() }
            }
            checkers[selectedCheckerN].circle.gridpaneConstraints { columnRowIndex(x, y) }
            checkers[selectedCheckerN].pos = Point(x, y)
            desk[oldPos.x][oldPos.y] = 0
            if (state == 0) {
                desk[x][y] = 1
                state = 1
                lbl.text = "Black turn"
            } else {
                desk[x][y] = 2
                state = 0
                lbl.text = "White turn"
            }
        }
    }

    private fun calcPossibleMoves(circle: Circle, pos: Point) {
        val x = pos.x
        val y = pos.y
        clear()
        if (circle.fill == WHITE) {
            if (!canOtherWhitesMove(pos)) {
                var canAttack = false
                if ((x > 1) and (y > 1))
                    if ((desk[x - 2][y - 2] == 0) and (desk[x - 1][y - 1] == 2)) {
                        tiles[x - 2][y - 2].fill = Color.GREEN
                        canAttack = true
                    }
                if ((x < 6) and (y > 1))
                    if ((desk[x + 2][y - 2] == 0) and (desk[x + 1][y - 1] == 2)) {
                        tiles[x + 2][y - 2].fill = Color.GREEN
                        canAttack = true
                    }
                if (!canAttack) {
                    if ((x > 0) and (y > 0))
                        if (desk[x - 1][y - 1] == 0)
                            tiles[x - 1][y - 1].fill = Color.GREEN
                    if ((x < 7) and (y > 0))
                        if (desk[x + 1][y - 1] == 0)
                            tiles[x + 1][y - 1].fill = Color.GREEN
                }
            }
        } else {
            if (!canOtherBlacksMove(pos)) {
                var canAttack = false
                if ((x > 1) and (y < 6))
                    if ((desk[x - 2][y + 2] == 0) and (desk[x - 1][y + 1] == 1)) {
                        tiles[x - 2][y + 2].fill = Color.GREEN
                        canAttack = true
                    }
                if ((x < 6) and (y < 6))
                    if ((desk[x + 2][y + 2] == 0) and (desk[x + 1][y + 1] == 1)) {
                        tiles[x + 2][y + 2].fill = Color.GREEN
                        canAttack = true
                    }
                if (!canAttack) {
                    if ((x > 0) and (y < 7))
                        if (desk[x - 1][y + 1] == 0)
                            tiles[x - 1][y + 1].fill = Color.GREEN
                    if ((x < 7) and (y < 7))
                        if (desk[x + 1][y + 1] == 0)
                            tiles[x + 1][y + 1].fill = Color.GREEN
                }
            }
        }
    }

    private fun canOtherWhitesMove(pos: Point): Boolean {
        for (checker in checkers) {
            if (checker.color == WHITE) {
                val x = checker.pos.x
                val y = checker.pos.y
                if ((pos != Point(x, y)) and (Point(x, y) != Point(-10, -10))) {
                    if ((x > 1) and (y > 1))
                        if ((desk[x - 2][y - 2] == 0) and (desk[x - 1][y - 1] == 2))
                            return true
                    if ((x < 6) and (y > 1))
                        if ((desk[x + 2][y - 2] == 0) and (desk[x + 1][y - 1] == 2))
                            return true
                }
            }
        }
        return false
    }

    private fun canOtherBlacksMove(pos: Point): Boolean {
        for (checker in checkers) {
            if (checker.color == BLACK) {
                val x = checker.pos.x
                val y = checker.pos.y
                if ((pos != Point(x, y)) and (Point(x, y) != Point(-10, -10))) {
                    if ((x > 1) and (y < 6))
                        if ((desk[x - 2][y + 2] == 0) and (desk[x - 1][y + 1] == 1))
                            return true
                    if ((x < 6) and (y < 6))
                        if ((desk[x + 2][y + 2] == 0) and (desk[x + 1][y + 1] == 1))
                            return true
                }
            }
        }
        return false
    }

    private fun clear() {
        for (i in 0..7)
            for (j in 0..7)
                tiles[i][j].fill = if ((i + j) % 2 == 0) Color.WHITE else Color.color(80.0 / 255, 40.0 / 255, 30.0 / 255)
    }

    private fun nothing() {}
}
