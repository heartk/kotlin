// WITH_RUNTIME
// INTENTION_TEXT: "Replace with 'filterIndexed{}.firstOrNull()'"
fun foo(list: List<String>): String? {
    <caret>for ((index, s) in list.withIndex()) {
        if (s.length > index) continue
        if (s.isNotBlank()) {
            return s
        }
    }
    return null
}