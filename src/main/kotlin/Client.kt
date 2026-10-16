import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner


fun main(){
    val host = "127.0.0.1"
    val port = 9000

    println("Conectando al servidor: $host/$port...")

    try {
        Socket(host, port).use { socket ->
            var conexion = true

            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(OutputStreamWriter(socket.getOutputStream()),true)
            val scr = Scanner(System.`in`)

            val pista = reader.readLine()
            println(pista)

            while (conexion){
                println("Introduce el numero: ")
                val texto = scr.nextLine()

                if (texto.equals("EXIT", ignoreCase = true)){
                    println("Desconectando del servidor: ${socket.inetAddress.hostAddress}")
                    conexion = false
                } else {
                    writer.println(texto)

                    val respuesta = reader.readLine()
                    if (respuesta != null) {
                        println("${socket.inetAddress.hostAddress}: -> $respuesta")

                        if (respuesta.contains("acertado", ignoreCase = true) ||
                            respuesta.contains("sin vidas", ignoreCase = true) ||
                            respuesta.contains("perdido", ignoreCase = true)) {
                            println("Partida Finalizada")
                            conexion = false
                        }
                    } else {
                        println("${socket.inetAddress.hostAddress}: -> Ha cerrado la conexion")
                        conexion = false
                    }
                }


            }
        }
    } catch (e: Exception) {
        println("Error: ${e.localizedMessage}")
    }
}