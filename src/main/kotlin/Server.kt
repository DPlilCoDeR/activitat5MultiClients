import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random


fun main () = runBlocking {
    val port = 9000

    val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    try {
        ServerSocket(port).use { socket ->
            println("Escuchando en $port...")

            while (true) {
                val clientSocket = socket.accept()
                println("Cliente conectado > ${clientSocket.inetAddress.hostAddress}/${clientSocket.port}")



                serverScope.launch {
                    atendreClient(clientSocket)
                }
            }
        }
    } finally {
        serverScope.cancel()
    }
}

fun atendreClient(socket: Socket) {
    try {
        socket.use { client ->
            var vidas = 10
            val resultado = Random.nextInt(1, 101)
            var jugando = true

            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
            val writer = PrintWriter(OutputStreamWriter(client.getOutputStream()), true)

            writer.println("Adivina MIN: ${resultado - 20}, MAX: ${resultado + 20}")

            while (jugando && vidas > 0) {
                val texto = reader.readLine() ?: break

                if (texto.isBlank()) {
                    writer.println("Entrada vacía. Introduce un número entero.")
                    continue
                }

                try {
                    val numero = texto.toInt()

                    if (numero == resultado) {
                        writer.println("¡haz acertado!! El número era $resultado")
                        jugando = false
                        continue
                    }
                    vidas--
                    val respuesta = when {
                        vidas == 0 -> {
                            jugando = false
                            "Te has quedado sin vidas. Has perdido. El número era $resultado"
                        }
                        numero > resultado -> "[Vidas: $vidas] El numero es mas pequeño"
                        else -> "[Vidas: $vidas] El numero es mas grande"
                    }

                    writer.println(respuesta)

                } catch (e: NumberFormatException) {
                    writer.println("Entrada no válida. Introduce un número entero.")
                }

            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}