@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("CollectionsKt")

package kotlin.collections

import java.io.Serializable
import java.util.*
import kotlin.comparisons.compareValues



internal fun <T> Array<out T>.asCollection(): Collection<T> = ArrayAsCollection(this)

private class ArrayAsCollection<T>(val values: Array<out T>): Collection<T> {
    override val size: Int get() = values.size
    override fun isEmpty(): Boolean = values.isEmpty()
    override fun contains(element: T): Boolean = values.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
    override fun iterator(): Iterator<T> = values.iterator()
    // override hidden toArray implementation to prevent copying of values array
    public fun toArray(): Array<out Any?> = values.varargToArrayOfAny()
}

/** Returns an empty read-only list.  The returned list is serializable (JVM). */
@kotlin.jvm.JvmVersion
public fun <T> emptyList(): List<T> = Collections.emptyList()

/** Returns a new read-only list of given elements.  The returned list is serializable (JVM). */
public fun <T> listOf(vararg elements: T): List<T> = if (elements.size > 0) elements.asList() else emptyList()

/** Returns an empty read-only list.  The returned list is serializable (JVM). */
@kotlin.internal.InlineOnly
public inline fun <T> listOf(): List<T> = emptyList()

/**
 * Returns an immutable list containing only the specified object [element].
 * The returned list is serializable.
 */
@JvmVersion
public fun <T> listOf(element: T): List<T> = Collections.singletonList(element)

/** Returns a new [LinkedList] with the given elements. */
@JvmVersion
@Deprecated("Use LinkedList constructor.", ReplaceWith("LinkedList(listOf(*elements))", "java.util.LinkedList"), level = DeprecationLevel.ERROR)
public fun <T> linkedListOf(vararg elements: T): LinkedList<T>
        = if (elements.size == 0) LinkedList() else LinkedList(ArrayAsCollection(elements))

@Deprecated("Use LinkedList constructor.", ReplaceWith("LinkedList<T>()", "java.util.LinkedList"), level = DeprecationLevel.ERROR)
public fun <T> linkedListOf() = LinkedList<T>()


/** Returns a new [MutableList] with the given elements. */
public fun <T> mutableListOf(vararg elements: T): MutableList<T>
        = if (elements.size == 0) ArrayList() else ArrayList(ArrayAsCollection(elements))

/** Returns a new [ArrayList] with the given elements. */
public fun <T> arrayListOf(vararg elements: T): ArrayList<T>
        = if (elements.size == 0) ArrayList() else ArrayList(ArrayAsCollection(elements))

/** Returns a new read-only list either of single given element, if it is not null, or empty list it the element is null. The returned list is serializable (JVM). */
public fun <T : Any> listOfNotNull(element: T?): List<T> = if (element != null) listOf(element) else emptyList()

/** Returns a new read-only list only of those given elements, that are not null.  The returned list is serializable (JVM). */
public fun <T : Any> listOfNotNull(vararg elements: T?): List<T> = elements.filterNotNull()

/**
 * Returns an [IntRange] of the valid indices for this collection.
 */
public val Collection<*>.indices: IntRange
    get() = 0..size - 1

/**
 * Returns the index of the last item in the list or -1 if the list is empty.
 *
 * @sample test.collections.ListSpecificTest.lastIndex
 */
public val <T> List<T>.lastIndex: Int
    get() = this.size - 1

/** Returns `true` if the collection is not empty. */
@kotlin.internal.InlineOnly
public inline fun <T> Collection<T>.isNotEmpty(): Boolean = !isEmpty()

/** Returns this Collection if it's not `null` and the empty list otherwise. */
@kotlin.internal.InlineOnly
public inline fun <T> Collection<T>?.orEmpty(): Collection<T> = this ?: emptyList()

/** Returns this List if it's not `null` and the empty list otherwise. */
@kotlin.internal.InlineOnly
public inline fun <T> List<T>?.orEmpty(): List<T> = this ?: emptyList()

/**
 * Returns a list containing the elements returned by this enumeration
 * in the order they are returned by the enumeration.
 */
@JvmVersion
@kotlin.internal.InlineOnly
public inline fun <T> Enumeration<T>.toList(): List<T> = Collections.list(this)

/**
 * Checks if all elements in the specified collection are contained in this collection.
 *
 * Allows to overcome type-safety restriction of `containsAll` that requires to pass a collection of type `Collection<E>`.
 */
@kotlin.internal.InlineOnly
public inline fun <@kotlin.internal.OnlyInputTypes T> Collection<T>.containsAll(elements: Collection<T>): Boolean = this.containsAll(elements)


// copies typed varargs array to array of objects
@JvmVersion
private fun <T> Array<out T>.varargToArrayOfAny(): Array<Any?>
        = Arrays.copyOf(this, this.size, Array<Any?>::class.java)

/**
 * Searches this list or its range for the provided [element] index using binary search algorithm.
 * The list is expected to be sorted into ascending order according to the Comparable natural ordering of its elements.
 *
 * If the list contains multiple elements equal to the specified object, there is no guarantee which one will be found.
 */
public fun <T: Comparable<T>> List<T?>.binarySearch(element: T?, fromIndex: Int = 0, toIndex: Int = size): Int {
    rangeCheck(size, fromIndex, toIndex)

    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = compareValues(midVal, element)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

/**
 * Searches this list or its range for the provided [element] index using binary search algorithm.
 * The list is expected to be sorted into ascending order according to the specified [comparator].
 *
 * If the list contains multiple elements equal to the specified object, there is no guarantee which one will be found.
 */
public fun <T> List<T>.binarySearch(element: T, comparator: Comparator<in T>, fromIndex: Int = 0, toIndex: Int = size): Int {
    rangeCheck(size, fromIndex, toIndex)

    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = comparator.compare(midVal, element)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

/**
 * Searches this list or its range for an index of an element with the provided [key] using binary search algorithm.
 * The list is expected to be sorted into ascending order according to the Comparable natural ordering of keys of its elements.
 *
 * If the list contains multiple elements with the specified [key], there is no guarantee which one will be found.
 */
public inline fun <T, K : Comparable<K>> List<T>.binarySearchBy(key: K?, fromIndex: Int = 0, toIndex: Int = size, crossinline selector: (T) -> K?): Int =
        binarySearch(fromIndex, toIndex) { compareValues(selector(it), key) }

// do not introduce this overload --- too rare
//public fun <T, K> List<T>.binarySearchBy(key: K, comparator: Comparator<K>, fromIndex: Int = 0, toIndex: Int = size(), selector: (T) -> K): Int =
//        binarySearch(fromIndex, toIndex) { comparator.compare(selector(it), key) }

/**
 * Searches this list or its range for an index of an element for which [comparison] function returns zero.
 * The list is expected to be sorted into ascending order according to the provided [comparison].
 *
 * @param comparison function that compares an element of the list with the element being searched.
 */
public fun <T> List<T>.binarySearch(fromIndex: Int = 0, toIndex: Int = size, comparison: (T) -> Int): Int {
    rangeCheck(size, fromIndex, toIndex)

    var low = fromIndex
    var high = toIndex - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = comparison(midVal)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

/**
 * Checks that `from` and `to` are in
 * the range of [0..size] and throws an appropriate exception, if they aren't.
 */
private fun rangeCheck(size: Int, fromIndex: Int, toIndex: Int) {
    when {
        fromIndex > toIndex -> throw IllegalArgumentException("fromIndex ($fromIndex) is greater than toIndex ($toIndex)")
        fromIndex < 0 -> throw IndexOutOfBoundsException("fromIndex ($fromIndex) is less than zero.")
        toIndex > size -> throw IndexOutOfBoundsException("toIndex ($toIndex) is greater than size ($size).")
    }
}

