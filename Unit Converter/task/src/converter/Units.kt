enum class Units(
    val short: String,
    val singular: String,
    val plural: String,
    val other: Array<String>,
    val factor: Double,
) {
    METER("m", "meter", "meters", arrayOf(), 1.0),
    KILOMETER("km", "kilometer", "kilometers", arrayOf(), 1000.0),
    CENTIMETER("cm", "centimeter", "centimeters", arrayOf(), 0.01),
    MILLIMETER("mm", "millimeter", "millimeters", arrayOf(), 0.001),
    MILES("mi", "mile", "miles", arrayOf(), 1609.35),
    YARDS("yd", "yard", "yards", arrayOf(), 0.9144),
    FEET("ft", "foot", "feet", arrayOf(), 0.3048),
    INCHES("in", "inch", "inches", arrayOf(), 0.0254),
    GRAM("g", "gram", "grams", arrayOf(), 1.0),
    KILOGRAM("kg", "kilogram", "kilograms", arrayOf(), 1000.0),
    MILLIGRAM("mg", "milligram", "milligrams", arrayOf(), 0.001),
    POUND("lb", "pound", "pounds", arrayOf(), 453.592),
    OUNCE("oz", "ounce", "ounces", arrayOf(), 28.3495),
    CELSIUS("c", "degree Celsius", "degrees Celsius", arrayOf("dc", "celsius"), 0.0),
    FAHRENHEIT("f", "degree Fahrenheit", "degrees Fahrenheit", arrayOf("df", "fahrenheit"), 0.0),
    KELVIN("k", "kelvin", "kelvins", arrayOf(), 0.0),
    TEMPERATURE("temperature", "temperature", "temperature", arrayOf(), 0.0),
    NULL("???", "???", "???", arrayOf(), 0.0);

    fun printFullInfo() {
        println(
            """
            name:     $name 
            Short:    $short 
            Singular: $singular
            Plural:   $plural
            Other:    $other
            Factor:   $factor""".trimIndent()
        )
    }

    private fun commonUnit(): Units {
        return when (valueOf(name)) {
            in METER..INCHES -> METER
            in GRAM..OUNCE -> GRAM
            CELSIUS -> TEMPERATURE
            FAHRENHEIT -> TEMPERATURE
            KELVIN -> TEMPERATURE
            else -> NULL
        }
    }

    companion object {

        private var strFromUnit: String = "???"
        private var strToUnit: String = "???"

        fun create(token: String, isFrom: Boolean): Units {
            for (enum in values())
                when (token) {
                    enum.short.toLowerCase(),
                    enum.singular.toLowerCase(),
                    enum.plural.toLowerCase(),
                    in enum.other -> {
                        if (isFrom)
                            strFromUnit = token
                        else
                            strToUnit = token
                        return enum
                    }
                }
            return NULL
        }

        fun convert(measure: Double, fromUnit: Units, toUnit: Units): String {
            val fromCommon = fromUnit.commonUnit()
            val toCommon = toUnit.commonUnit()
            var toValue = Double.NEGATIVE_INFINITY
            if (fromCommon == toCommon && fromCommon != NULL) {
                toValue = when (fromCommon) {
                    TEMPERATURE -> convertTemperature(measure, fromUnit, toUnit)
                    METER, GRAM -> {
                        if (measure < 0)
                            return printInvalidConversion(fromUnit)
                        convertLengthsOrWeights(measure, fromUnit, toUnit)
                    }
                    else -> 0.0
                }
            } else {
                // unConvertible units, print error
                return printError(measure, fromUnit, toUnit)
            }
            return formatOutput(measure, toValue, fromUnit, toUnit)
        }

        private fun convertTemperature(measure: Double, fromUnit: Units, toUnit: Units): Double {
            return when (fromUnit) {
                CELSIUS -> {
                    val v = when (toUnit) {
                        FAHRENHEIT -> measure * 9.0 / 5.0 + 32.0
                        KELVIN -> measure + 273.15
                        CELSIUS -> measure
                        else -> Double.NEGATIVE_INFINITY
                    }
                    v
                }
                FAHRENHEIT -> {
                    val v = when (toUnit) {
                        CELSIUS -> (measure - 32.0) * 5.0 / 9.0
                        KELVIN -> (measure + 459.67) * 5.0 / 9.0
                        FAHRENHEIT -> measure
                        else -> Double.NEGATIVE_INFINITY
                    }
                    v
                }
                KELVIN -> {
                    val v = when (toUnit) {
                        CELSIUS -> measure - 273.15
                        FAHRENHEIT -> measure * 9.0 / 5.0 - 459.67
                        KELVIN -> measure
                        else -> Double.NEGATIVE_INFINITY
                    }
                    v
                }
                else -> Double.NEGATIVE_INFINITY
            }
        }

        private fun convertLengthsOrWeights(measure: Double, fromUnit: Units, toUnit: Units): Double {
            var toValue = Double.NEGATIVE_INFINITY

            val commonMeasure = fromUnit.factor * measure
            toValue = commonMeasure / toUnit.factor
            return toValue
        }

        private fun printError(measure: Double, fromUnit: Units, toUnit: Units): String {
            // both units should be in the plural
            return "Conversion from ${fromUnit.plural} to ${toUnit.plural} is impossible"
        }

        private fun printInvalidConversion(from: Units): String {
            return "${
                if (from.commonUnit() == Units.METER)
                    "Length"
                else
                    "Weight"
            } shouldn't be negative."
        }

        private fun formatOutput(
            measure: Double,
            convertVal: Double,
            fromUnit: Units,
            toUnit: Units
        ): String {

            val fromU: String
            val toU: String

            val fromUserInput = Units.strFromUnit
            val toUserInput = Units.strToUnit

            fromU = when (measure) {
                1.0 -> fromUnit.singular
                else -> fromUnit.plural
            }

            toU = when (convertVal) {
                1.0 -> toUnit.singular
                else -> toUnit.plural
            }

            return "$measure $fromU is $convertVal $toU"
        }
    }
}
