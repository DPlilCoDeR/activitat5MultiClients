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
            println("Escuchando en $port")

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
    socket.use { client ->
        var vidas = 10
        val resultado = Random.nextInt(1,101)
        var jugando = true

        val reader = BufferedReader(InputStreamReader(client.getInputStream()))
        val writer = PrintWriter(OutputStreamWriter(client.getOutputStream()))

        writer.println("Adivina MIN: ${resultado - Random.nextInt(20, resultado)}, MAX: ${resultado + Random.nextInt(resultado, 20)}")

        val numero = reader.readLine().toInt()

        while (jugando) {
            if (vidas == 0) {
                writer.println("Haz perdido")
                jugando = false
            }
            when {
                numero > resultado -> {
                    writer.println("Numero mas grande")
                    vidas--
                }
                numero < resultado -> {
                    writer.println("Numero mas pequeño")
                    vidas--
                } else -> {
                    writer.println("haz acertado!!")
                    jugando = false
                }

            }
        }

    }
}