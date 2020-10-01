package com.example.view

import com.example.Styles
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import tornadofx.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javax.swing.SpringLayout

class MainView : View("rus checkers") {
    override val root = vbox {
        maxWidth(400.0)
        maxHeight(400.0)
    }

    init {
        with(root) {
            gridpane {
                for (i in 0..7)
                    for (j in 0..7)
                        rectangle {
                            fill = if ((i + j) % 2 == 0) Color.WHITE else Color.BLACK
                            width = 70.0
                            height = 70.0
                        }.gridpaneConstraints {
                            columnRowIndex(i, j)
                        }
            }
            label("Lorem ipsum") {
                textFill = Color.BLACK
                font = Font(30.0)
            }
        }
    }
}
