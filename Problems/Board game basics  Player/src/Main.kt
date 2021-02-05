class Player(val id: Int, val name: String, val hp: Int = 100) {


    companion object {
        var uniqueID = 0
        fun create(name: String): Player = Player(++uniqueID, name)
    }
}
