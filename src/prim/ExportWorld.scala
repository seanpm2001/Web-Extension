package org.nlogo.extensions.web.prim

import
  java.io.PrintWriter

import
  org.nlogo.{ api, nvm },
    api.{ Argument, Context },
    nvm.ExtensionContext

import
  util.{ EnsuranceAgent, Streamer },
    EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 3:36 PM
 */

// Hooks in and sends an `export-world` to a remote location
object ExportWorld extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = ((Streamer) => Unit)
  override protected def  generateRequester = (hook: (Streamer) => Unit) => new WorldExporter(hook) with Integration

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { case extContext: ExtensionContext =>
      val hook = {
        (stream: Streamer) =>
          val writer = new PrintWriter(stream)
          try     extContext.workspace.exportWorld(writer)
          finally writer.close()
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

  protected class WorldExporter(hook: (Streamer) => Unit) extends Requester {

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



