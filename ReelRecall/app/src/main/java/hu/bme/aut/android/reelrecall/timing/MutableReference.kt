package hu.bme.aut.android.reelrecall.timing

//since in kotlin u cant modify a value in an async inner class,
// we have to use a mutable reference of the value that we modify in the async inner class
class MutableReference<T>(var value: T)