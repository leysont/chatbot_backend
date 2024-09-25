package bot

class Issue {
    private val map = mutableMapOf<String, String>()

    operator fun set(key: String, value: String) {
        map[key] = value
    }

    operator fun get(key: String): String? {
        return map[key]
    }

    fun isComplete(): Boolean {
        TODO("Not yet implemented")
    }
}