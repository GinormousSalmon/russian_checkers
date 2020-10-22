import checker.Checker
import checker.King
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color.*
import tornadofx.gridpaneConstraints
import tornadofx.imageview
import org.junit.Test
import kotlin.test.*

class CheckerTest {

    private var desk = Desk(mutableListOf())

    init {
        Checker.setDesk(desk)
        with(GridPane()) {
            for (row in (0..2) + (5..7)) {
                val color = if (row < 3) "black" else "white"
                for (column in 0..7)
                    if ((row + column) % 2 == 1) {
                        val imageview = imageview {
                            gridpaneConstraints { columnRowIndex(column, row) }
                        }
                        desk.set(column, row, Checker(imageview, if (color == "black") BLACK else WHITE))
                    }
            }
        }
    }

    private fun expMoves(x: Int, y: Int, ex: Int, ey: Int): List<Triple<Int, Int, Pair<Int, Int>?>> {
        return if (ex == -1 || ey == -1) listOf(Triple(x, y, null)) else listOf(Triple(x, y, Pair(ex, ey)))
    }

    private fun imv(x: Int, y: Int): ImageView {
        return GridPane().imageview {
            gridpaneConstraints { columnRowIndex(x, y) }
        }
    }

    @Test
    fun testCanAttack() {
        var xFrom = 0
        var yFrom = 5
        var xEnemy = 1
        var yEnemy = 4
        var xTo = 2
        var yTo = 3
        desk.set(xEnemy, yEnemy, Checker(imv(xEnemy, yEnemy), BLACK))
        assertTrue { desk.get(xFrom, yFrom)!!.canAttack() }
        for (x in 0..7)
            for (y in 0..7)
                if (desk.get(x, y) != null && Pair(x, y) != Pair(xFrom, yFrom) && Pair(x, y) != Pair(2, 5))
                    assertFalse { desk.get(x, y)!!.canAttack() }

        assertEquals(expMoves(xTo, yTo, xEnemy, yEnemy), desk.get(xFrom, yFrom)!!.possibleMoves)

        desk.move(xFrom, yFrom, xTo, yTo)
        assertNull(desk.get(xFrom, yFrom))
        assertNull(desk.get(xEnemy, yEnemy))

        xFrom = 1
        yFrom = 2
        xEnemy = 2
        yEnemy = 3
        xTo = 3
        yTo = 4
        assertTrue { desk.get(xFrom, yFrom)!!.canAttack() }
        assertEquals(expMoves(xTo, yTo, xEnemy, yEnemy), desk.get(xFrom, yFrom)!!.possibleMoves)

        desk.move(xFrom, yFrom, xTo, yTo)
        assertNull(desk.get(xFrom, yFrom))
        assertNull(desk.get(xEnemy, yEnemy))

        xFrom = 2
        yFrom = 5
        xEnemy = 3
        yEnemy = 4
        xTo = 4
        yTo = 3
        assertTrue { desk.get(xFrom, yFrom)!!.canAttack() }
        assertEquals(expMoves(xTo, yTo, xEnemy, yEnemy), desk.get(xFrom, yFrom)!!.possibleMoves)

        desk.move(xFrom, yFrom, xTo, yTo)
        assertNull(desk.get(xFrom, yFrom))
        assertNull(desk.get(xEnemy, yEnemy))



        desk = Desk(mutableListOf())
        Checker.setDesk(desk)
        desk.set(6, 1, King(Checker(imv(6, 1), WHITE)))
        desk.set(1, 2, Checker(imv(1, 2), BLACK))
        desk.set(4, 3, Checker(imv(4, 3), BLACK))
        xFrom = 6
        yFrom = 1
        xEnemy = 4
        yEnemy = 3
        xTo = 3
        yTo = 4
        assertTrue { desk.get(xFrom, yFrom)!!.canAttack() }
        assertEquals(expMoves(xTo, yTo, xEnemy, yEnemy), desk.get(xFrom, yFrom)!!.possibleMoves)

        desk.move(xFrom, yFrom, xTo, yTo)
        assertNull(desk.get(xFrom, yFrom))
        assertNull(desk.get(xEnemy, yEnemy))
        desk.get(xTo, yTo)?.possibleMoves?.clear()

        xFrom = 3
        yFrom = 4
        xEnemy = 1
        yEnemy = 2
        xTo = 0
        yTo = 1
        assertTrue { desk.get(xFrom, yFrom)!!.canAttack() }
        assertEquals(expMoves(xTo, yTo, xEnemy, yEnemy), desk.get(xFrom, yFrom)!!.possibleMoves)

        desk.move(xFrom, yFrom, xTo, yTo)
        assertNull(desk.get(xFrom, yFrom))
        assertNull(desk.get(xEnemy, yEnemy))



        desk = Desk(mutableListOf())
        Checker.setDesk(desk)
        desk.set(6, 7, King(Checker(imv(6, 7), BLACK)))
        desk.set(3, 4, Checker(imv(3, 4), WHITE))

        xFrom = 6
        yFrom = 7
        xEnemy = 3
        yEnemy = 4
        assertTrue { desk.get(xFrom, yFrom)!!.canAttack() }
        assertEquals(
            expMoves(2, 3, xEnemy, yEnemy) +
                    expMoves(1, 2, xEnemy, yEnemy) +
                    expMoves(0, 1, xEnemy, yEnemy), desk.get(xFrom, yFrom)!!.possibleMoves
        )
        desk.move(xFrom, yFrom, 1, 2)
        assertNull(desk.get(xFrom, yFrom))
        assertNull(desk.get(xEnemy, yEnemy))
    }

    @Test
    fun testCanMove() {
        var xFrom = 6
        var yFrom = 5
        assertTrue { desk.get(xFrom, yFrom)!!.canMove() }
        assertEquals(expMoves(7, 4, -1, -1) + expMoves(5, 4, -1, -1), desk.get(xFrom, yFrom)!!.possibleMoves)
        desk.move(xFrom, yFrom, 7, 4)
        assertNull(desk.get(xFrom, yFrom))

        xFrom = 7
        yFrom = 2
        assertTrue { desk.get(xFrom, yFrom)!!.canMove() }
        assertEquals(expMoves(6, 3, -1, -1), desk.get(xFrom, yFrom)!!.possibleMoves)
        desk.move(xFrom, yFrom, 6, 3)
        assertNull(desk.get(xFrom, yFrom))

        desk = Desk(mutableListOf())
        Checker.setDesk(desk)
        desk.set(1, 4, King(Checker(imv(1, 4), WHITE)))
        desk.set(0, 5, Checker(imv(0, 5), BLACK))
        desk.set(4, 7, Checker(imv(4, 7), WHITE))
        xFrom = 1
        yFrom = 4
        assertTrue { desk.get(xFrom, yFrom)!!.canMove() }
        assertEquals(
            expMoves(0, 3, -1, -1) +
                    expMoves(2, 3, -1, -1) +
                    expMoves(3, 2, -1, -1) +
                    expMoves(4, 1, -1, -1) +
                    expMoves(5, 0, -1, -1) +
                    expMoves(2, 5, -1, -1) +
                    expMoves(3, 6, -1, -1), desk.get(xFrom, yFrom)!!.possibleMoves
        )
    }
}

