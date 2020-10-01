package com.example.view


import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*


class MainView : View("rus checkers") {
    override val root = VBox()

    init {
        currentStage!!.widthProperty().onChange {
            currentStage!!.maxHeight = currentStage!!.width
            currentStage!!.minHeight = currentStage!!.width
        }
        currentStage?.width = 500.0

        with(root) {
            gridpane {
                for (i in 0..7)
                    for (j in 0..7)
                        rectangle {
                            fill = if ((i + j) % 2 == 0) Color.WHITE else Color.color(80.0 / 255, 40.0 / 255, 30.0 / 255)
                            widthProperty().bind(root.widthProperty().divide(8))
                            heightProperty().bind(root.heightProperty().divide(8))
                        }.gridpaneConstraints { columnRowIndex(i, j) }
            }
//            label("Lorem ipsum") {
//                textFill = Color.BLACK
//                font = Font(30.0)
//            }
        }
    }
}
