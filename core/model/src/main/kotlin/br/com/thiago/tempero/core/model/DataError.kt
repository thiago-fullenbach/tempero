package br.com.thiago.tempero.core.model

sealed class DataError(cause: Throwable? = null) : Exception(cause) {
    class Network(cause: Throwable) : DataError(cause)
    class Unavailable(cause: Throwable) : DataError(cause)
    class InvalidResponse(cause: Throwable) : DataError(cause)
    class Storage(cause: Throwable) : DataError(cause)
    class Unknown(cause: Throwable) : DataError(cause)
}
