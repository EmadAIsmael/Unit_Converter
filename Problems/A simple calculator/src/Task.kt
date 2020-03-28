import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    val mathOp = List<String>(3) { scanner.next() }

    when (mathOp[1]) {
        "+" -> println(mathOp[0].toLong() + mathOp[2].toLong())
        "-" -> println(mathOp[0].toLong() - mathOp[2].toLong())
        "/" -> println(if (mathOp[2].toLong() == 0L) "Division by 0!" else mathOp[0].toLong() / mathOp[2].toLong())
        "*" -> println(mathOp[0].toLong() * mathOp[2].toLong())
        else -> println("Unknown operator")
    }
}
