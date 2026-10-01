package br.com.thiago.tempero.domain.error

sealed class AppError(cause: Throwable? = null) : Exception(cause) {
    class NoConnection(cause: Throwable) : AppError(cause)
    class Server(val code: Int) : AppError()
    class InvalidData(cause: Throwable) : AppError(cause)
    class Unknown(cause: Throwable) : AppError(cause)
}