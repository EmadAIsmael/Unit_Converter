class Task(val name: String)

// create the Manager singleton here
object Manager {
    fun solveTask(task: Task) {
        println("Task ${task.name} solved!")
        solvedTask += 1
    }

    var solvedTask: Int = 0

}