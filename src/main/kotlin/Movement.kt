import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE


class Movement(private val desk: Desk) {

    var turn: Color = WHITE

    fun invertTurn() {
        turn = turn.invert()
    }

    fun calcPossibleAttacks(selectedX: Int = -1, selectedY: Int = -1): Boolean {
        movesClear()
        if (selectedX != -1)
            return desk.get(selectedX, selectedY)?.canAttack() ?: false
        var canAnyAttack = false
        for (x in 0..7)
            for (y in 0..7)
                if (desk.get(x, y)?.color == turn)
                    canAnyAttack = (desk.get(x, y)?.canAttack() ?: false) || canAnyAttack
        return canAnyAttack
    }

    fun calcPossibleMoves(): Boolean {
        var canAnyMove = false
        for (x in 0..7)
            for (y in 0..7)
                if (desk.get(x, y)?.color == turn)
                    canAnyMove = (desk.get(x, y)?.canMove() ?: false) || canAnyMove
        return canAnyMove
    }

    fun movesClear() {
        for (x in 0..7)
            for (y in 0..7)
                desk.get(x, y)?.possibleMoves?.clear()
    }
}