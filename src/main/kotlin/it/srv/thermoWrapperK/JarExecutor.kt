package it.srv.thermoWrapperK

import it.srv.thermoWrapperK.exception.JarExecutorException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class JarExecutor {
    private var error: BufferedReader? = null
    private var op: BufferedReader? = null
    private var exitVal = 0
    private lateinit var process: Process

    /**
     * @param jarFileName name of the file to run without the jar extension
     * @throws JarExecutorException when the process is interrupted or happens an IOException
     */
    @Throws(JarExecutorException::class)
    fun executeJar(jarFileName: String?) {
        val sb = StringBuilder()
        sb.append("java -Xms512m -Xmx2g ")
        sb.append("-jar ")
        sb.append(jarFileName)
        sb.append(".jar")
        try {
            val re = Runtime.getRuntime()
            val command = sb.toString()
            process = re.exec(command)
            error = BufferedReader(InputStreamReader(process.errorStream))
            op = BufferedReader(InputStreamReader(process.inputStream))
            var s: String?
            while (op!!.readLine().also { s = it } != null) {
                println(s)
            }
            process.waitFor()
            exitVal = process.exitValue()
            if (exitVal != 0 && exitVal != 1) {
                throw IOException("Failed to execure jar, " + getExecutionLog())
            }
        } catch (e: IOException) {
            throw JarExecutorException(e.message)
        } catch (e: InterruptedException) {
            throw JarExecutorException(e.message)
        }
    }

    /**
     * Fast way to kill the specific running process
     */
    fun destroy() {
        process.destroy()
    }

    private fun getExecutionLog(): String {
        val error = StringBuilder()
        var line: String
        try {
            while (this.error!!.readLine().also { line = it } != null) {
                error.append("\n").append(line)
            }
        } catch (ignored: IOException) {
        }
        var output = ""
        try {
            while (op!!.readLine().also { line = it } != null) {
                output = "$output\n$line"
            }
        } catch (ignored: IOException) {
        }
        try {
            this.error!!.close()
            op!!.close()
        } catch (ignored: IOException) {
        }
        return "exitVal: $exitVal, error: $error, output: $output"
    }
}