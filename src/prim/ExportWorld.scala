package org.nlogo.extensions.web.prim

import java.io.PrintWriter

import org.nlogo.api.{ Argument, Context, LogoList }
import org.nlogo.nvm.ExtensionContext

import util.{ EventEvaluator, StreamHandler }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 3:36 PM
 */

// Hooks in and sends an `export-world` to a remote location
object ExportWorld extends WebReporter with StreamHandler {

  override def report(args: Array[Argument], context: Context) : AnyRef = {
    context match {
      case extContext: ExtensionContext =>
        val hook = {
          (stream: Streamer) =>
            val writer = new PrintWriter(stream)
            try     extContext.workspace.exportWorld(writer)
            finally writer.close()
        }
        val (dest, requestMethod, paramMap) = processArguments(args)
        val exporter = new WorldExporter(hook) with WISEIntegration
        val (response, statusCode) = exporter(dest, requestMethod, paramMap)
        LogoList(response, statusCode)
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }

  private class WorldExporter(hook: (Streamer) => Unit) extends Requester {

    self: WebIntegration =>

    import java.io.{ ByteArrayOutputStream, UnsupportedEncodingException }

    private val DefaultByteEncoding = "UTF-8"

    override protected def generateAddedExportData = {

      val outputStream = new ByteArrayOutputStream()

      try {
        EventEvaluator(outputStream, hook)
        Option(outputStream.toString(DefaultByteEncoding))
      }
      catch {
        case ex: UnsupportedEncodingException =>
          System.err.println("Unable to convert hooked text to desired encoding: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
          None
        case ex: Exception =>
          System.err.println("Unknown error on hooking/exporting: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
          None
      }
      finally {
        outputStream.close()
      }

    }

  }

}



