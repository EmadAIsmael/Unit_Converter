class Block(val color: String) {
    object DimProperties {
        var length = 14
        var width = 9

        fun blocksInBox(length: Int, width: Int): Int =
            (length / this.length) * (width / this.width)
    }
}
