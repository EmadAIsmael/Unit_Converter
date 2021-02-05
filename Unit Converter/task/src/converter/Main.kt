package converter

fun getInput(): Array<String> {
    val pattern =
        """(([-]?\d+[.]?\d*)\s+(((degree(s)?)\s+)?(\w+))\s+(\b\w+\b)\s+(((degree(s)?)\s+)?(\w+)))|(exit)""".toRegex()

    print("Enter what you want to convert (or exit): ")
    val inputLine = readLine()!!.toLowerCase()
    val matches = pattern.findAll(inputLine)
    if (matches.count() > 0 && matches.first().value != "exit") {
        return arrayOf(
            matches.first().groupValues[2],         // measure value
            matches.first().groupValues[3],         // fromUnit input
            matches.first().groupValues[9]          // toUnit input
        )
    } else if (matches.count() > 0 && matches.first().value == "exit")
        return arrayOf("exit", "", "")
    return arrayOf()
}

fun main() {

    while (true) {

        val input = getInput()
        if (input.isEmpty()) {
            println("Parse error")
            continue
        } else if (input[0] == "exit")
            break

        val measure = input[0].toDouble()
        val fromUnit = input[1]
        val toUnit = input[2]

        val unit1 = Units.create(fromUnit, true)
        val unit2 = Units.create(toUnit, false)
        println(Units.convert(measure, unit1, unit2))
    }
}
