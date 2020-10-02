import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val whiteCell by cssclass()
        val blackCell by cssclass()
    }

    init {
        whiteCell {
            fill = Color.WHITE
//            shape[0] = 100.0
//            shape[1] = 100.0
//            width = 100.0
//            height = 100.0
        }
        blackCell {
            fill = Color.BLACK
//            width = 100.0
//            height = 100.0
        }
    }
}