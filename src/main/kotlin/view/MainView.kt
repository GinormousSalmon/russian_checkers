package view


import Checker
import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import javafx.scene.paint.Color.BLACK
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import tornadofx.*
import java.awt.Point
import java.awt.geom.Point2D
import java.sql.Time
import kotlin.math.absoluteValue


class MainView : View("rus checkers") {
    override val root = VBox()
    private val fontSize = 30.0

    init {
        currentStage?.widthProperty()?.onChange {
            currentStage?.maxHeight = currentStage!!.width + fontSize * 1.5 + 30
            currentStage?.minHeight = currentStage!!.width + fontSize * 1.5 + 30
        }
        currentStage?.width = 500.0

        with(root) {
            gridpane {
                for (i in 0..7)
                    for (j in 0..7)
                        rectangle {
                            fill = if ((i + j) % 2 == 0) Color.WHITE else Color.color(80.0 / 255, 40.0 / 255, 30.0 / 255)
                            widthProperty().bind(root.widthProperty().divide(8))
                            heightProperty().bind(root.widthProperty().divide(8))
                        }.gridpaneConstraints { columnRowIndex(i, j) }

                val checkers = mutableListOf<Checker>()
                for (row in 0..2)
                    for (column in 0..7)
                        if ((row + column) % 2 == 1) {
                            val c = circle {
                                fill = BLACK
                                radiusProperty().bind(root.widthProperty().divide(23))
                                centerXProperty().bind(root.widthProperty().divide(40))
                                centerYProperty().bind(root.widthProperty().divide(40))
                            }
                            c.gridpaneConstraints {
                                columnRowIndex(column, row)
                                hAlignment = HPos.CENTER
                            }
                            checkers.add(Checker(BLACK, Point(column, row), c))
                        }
                for (row in 5..7)
                    for (column in 0..7)
                        if ((row + column) % 2 == 1) {
                            val c = circle {
                                fill = WHITE
                                radiusProperty().bind(root.widthProperty().divide(23))
                                centerXProperty().bind(root.widthProperty().divide(40))
                                centerYProperty().bind(root.widthProperty().divide(40))
                                onLeftClick(1) { click(this, Point(column, row)) }
                            }
                            c.gridpaneConstraints {
                                columnRowIndex(column, row)
                                hAlignment = HPos.CENTER
                            }
                            checkers.add(Checker(WHITE, Point(column, row), c))
                        }

            }
            label("Lorem ipsum") {
                textFill = Color.BLACK
                font = Font(fontSize)
            }
        }
    }

    private fun click(circle: Circle, pos: Point) {
        circle.gridpaneConstraints { columnRowIndex(pos.x, pos.y-2) }
        println("${pos.x} ${pos.y}")
    }

}
