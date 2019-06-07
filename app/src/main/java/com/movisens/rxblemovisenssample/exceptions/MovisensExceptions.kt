package com.movisens.rxblemovisenssample.exceptions

/**
 * Created by Robert Zetzsche on 28.05.2019.
 */
open class MovisensExceptions : Exception()

class ReconnectException : MovisensExceptions()

class UnrecoverableException : MovisensExceptions()
