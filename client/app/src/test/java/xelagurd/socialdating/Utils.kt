package xelagurd.socialdating

fun <T> mergeListsAsSets(first: List<T>, second: List<T>) =
    (first.toSet() + second).toList()

fun <T> mergeListsAsSets(first: List<T>, second: List<T>, third: List<T>) =
    mergeListsAsSets(mergeListsAsSets(first, second), third)