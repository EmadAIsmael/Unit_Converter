package converter

enum class Units(val userInput: Array<String>, val factor: Double) {
    METER(arrayOf("m", "meter", "meters"), 1.0),
    KILOMETER(arrayOf("km", "kilometer", "kilometers"), 1000.0),
    CENTIMETER(arrayOf("cm", "centimeter", "centimeters"), 0.01),
    MILLIMETER(arrayOf("mm", "millimeter", "millimeters"), 0.001),
    MILES(arrayOf("mi", "mile", "miles"), 1609.35),
    YARDS(arrayOf("yd", "yard", "yards"), 0.9144),
    FEET(arrayOf("ft", "foot", "feet"), 0.3048),
    INCHES(arrayOf("in", "inch", "inches"), 0.0254),
    GRAM(arrayOf("g", "gram", "grams"), 1.0),
    KILOGRAM(arrayOf("kg", "kilogram", "kilograms"), 1000.0),
    MILLIGRAM(arrayOf("mg", "milligram", "milligrams"), 0.001),
    POUND(arrayOf("lb", "pound", "pounds"), 453.592),
    OUNCE(arrayOf("oz", "ounce", "ounces"), 28.3495),
    CELSIUS(arrayOf("c", "dc", "celsius", "degree Celsius", "degrees Celsius"), 0.0),
    FAHRENHEIT(arrayOf("f", "df", "fahrenheit", "degree Fahrenheit", "degrees Fahrenheit"), 0.0),
    KELVIN(arrayOf("k", "Kelvin", "Kelvins"), 0.0),
    NULL(arrayOf("???", "???", "???"), 0.0),
    EXIT(arrayOf("exit"), 0.0);
}

class Converter {

    private fun unitInfo(unit: String): Units {
        for (enum in Units.values()) {
            for (input in enum.userInput)
                if (unit.toLowerCase() == input.toLowerCase())
                    return enum
        }
        return Units.NULL
    }

    private fun commonUnit(unit: Units): Units {
        return when (unit) {
            in Units.METER..Units.INCHES -> Units.METER
            in Units.GRAM..Units.OUNCE -> Units.GRAM
            Units.CELSIUS -> Units.CELSIUS
            Units.FAHRENHEIT -> Units.FAHRENHEIT
            Units.KELVIN -> Units.KELVIN
            else -> Units.NULL
        }
    }

    private fun changeTemperatureUnit(measure: Double, fromUnit: Units, toUnit: Units): Double {
        return when (fromUnit) {
            Units.CELSIUS -> {
                val v = when (toUnit) {
                    Units.FAHRENHEIT -> measure * 9.0 / 5.0 + 32.0
                    Units.KELVIN -> measure + 273.15
                    else -> Double.NEGATIVE_INFINITY
                }
                v
            }
            Units.FAHRENHEIT -> {
                val v = when (toUnit) {
                    Units.CELSIUS -> (measure - 32.0) * 5.0 / 9.0
                    Units.KELVIN -> (measure + 459.67) * 5.0 / 9.0
                    else -> Double.NEGATIVE_INFINITY
                }
                v
            }
            Units.KELVIN -> {
                val v = when (toUnit) {
                    Units.CELSIUS -> measure - 273.15
                    Units.FAHRENHEIT -> measure * 9.0 / 5.0 - 459.67
                    else -> Double.NEGATIVE_INFINITY
                }
                v
            }
            else -> Double.NEGATIVE_INFINITY
        }
    }

    fun convert(measure: Double, fromUnit: String, toUnit: String): String {
        val from = unitInfo(fromUnit)
        val to = unitInfo(toUnit)

        if (measure < 0 && (commonUnit(from) == Units.METER ||
                    commonUnit(from) == Units.GRAM)
        ) {
            return printInvalidConversion(from)
        }
        if (!convertible(from, to)) {
            return printError(fromUnit, toUnit)
        }

        val toValue = doConvert(measure, from, to)
        return formatOutput(measure, toValue, fromUnit, toUnit)
    }

    private fun doConvert(measure: Double, fromUnit: Units, toUnit: Units): Double {
        var toValue = Double.NEGATIVE_INFINITY

        if (commonUnit(fromUnit) == commonUnit(toUnit)) {
            val commonMeasure = fromUnit.factor * measure
            toValue = commonMeasure / toUnit.factor
        } else if (commonUnit(fromUnit) != Units.NULL && commonUnit(toUnit) != Units.NULL) {
            return changeTemperatureUnit(measure, fromUnit, toUnit)
        }
        return toValue
    }

    private fun convertible(fromUnit: Units, toUnit: Units): Boolean {
        val commonFrom = commonUnit(fromUnit)
        val commonTo = commonUnit(toUnit)
        return (fromUnit in Units.CELSIUS..Units.KELVIN && toUnit in Units.CELSIUS..Units.KELVIN) ||
                (commonFrom == commonTo && (commonFrom != Units.NULL && commonTo != Units.NULL))
    }

    private fun pluralize(unit: String?): String? {
        return when (unit?.toLowerCase()) {
            "foot" -> unit[0] + "eet"
            "inch" -> unit[0] + "nches"
            "degree celsius" -> "degrees Celsius"
            "degree fahrenheit" -> "degrees Fahrenheit"
            in arrayOf("feet", "inches", "degrees celsius", "degrees fahrenheit") -> unit
            else -> if (unit!!.endsWith("s")) unit else unit + "s"
        }
    }

    private fun printError(fromUnit: String, toUnit: String): String {
        // both units should be in the plural

        val p1 = if (unitInfo(fromUnit) == Units.NULL)
            "???"
        else
            pluralize(fromUnit)
        val p2 = if (unitInfo(toUnit) == Units.NULL)
            "???"
        else
            pluralize(toUnit)
        return "Conversion from ${if (p1 == "kelvin" || p1 == "kelvins")
            p1.capitalize()
        else
            p1} to $p2 is impossible"
    }

    private fun printInvalidConversion(from: Units): String {
        return "${if (commonUnit(from) == Units.METER)
            "Length"
        else
            "Weight"} shouldn't be negative."
    }

    private fun formatOutput(
        measure: Double,
        convertVal: Double,
        fromUnit: String,
        toUnit: String
    ): String {

        val abbrev = mutableMapOf(
            "m" to arrayOf("meter", "meters"),
            "km" to arrayOf("kilometer", "Kilometers"),
            "cm" to arrayOf("centimeter", "centimeters"),
            "mm" to arrayOf("millimeter", "millimeters"),
            "mi" to arrayOf("mile", "miles"),
            "yd" to arrayOf("yard", "yards"),
            "ft" to arrayOf("foot", "feet"),
            "in" to arrayOf("inch", "inches"),
            "g" to arrayOf("gram", "grams"),
            "kg" to arrayOf("kilogram", "kilograms"),
            "mg" to arrayOf("milligram", "milligrams"),
            "lb" to arrayOf("pound", "pounds"),
            "oz" to arrayOf("ounce", "ounces"),
            "c" to arrayOf("degree Celsius", "degrees Celsius"),
            "dc" to arrayOf("degree Celsius", "degrees Celsius"),
            "celsius" to arrayOf("degree Celsius", "degrees Celsius"),
            "f" to arrayOf("degree Fahrenheit", "degrees Fahrenheit"),
            "df" to arrayOf("degree Fahrenheit", "degrees Fahrenheit"),
            "fahrenheit" to arrayOf("degree Fahrenheit", "degrees Fahrenheit"),
            "k" to arrayOf("Kelvin", "Kelvins")
        )
        val fromU: String
        val toU: String
        val fromKey = fromUnit.toLowerCase()
        val toKey = toUnit.toLowerCase()

        fromU = when (fromKey) {
            in abbrev.keys ->
                (if (measure == 1.0)
                    abbrev[fromKey]?.get(0)
                else
                    pluralize(abbrev[fromKey]?.get(1))).toString()
            !in abbrev.keys ->
                if (measure == 1.0)
                    fromUnit
                else
                    pluralize(fromUnit).toString()
            else -> ""
        }

        toU = when (toKey) {
            in abbrev.keys ->
                (if (convertVal == 1.0)
                    abbrev[toKey]?.get(0)
                else
                    pluralize(abbrev[toKey]?.get(1))
                        ).toString()
            !in abbrev.keys ->
                if (convertVal == 1.0)
                    toUnit
                else
                    pluralize(toUnit).toString()
            else -> ""
        }

        return "$measure $fromU is $convertVal $toU"
    }
}

fun getInput(): Array<String> {
    val pattern =
        """(([-]?\d+[.]?\d*)\s+(((degree(s)?)\s+)?(\w+))\s+(\b\w+\b)\s+(((degree(s)?)\s+)?(\w+)))|(exit)""".toRegex()

    print("Enter what you want to convert (or exit): ")
    val inputLine = readLine()!!
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

        val converter = Converter()
        println(converter.convert(measure, fromUnit, toUnit))
    }
}
