package org.nlogo.extensions.web

import org.nlogo.api.{ Argument, Command, Context, ExtensionException, Reporter }
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }
import org.nlogo.extensions.web.requester.{ Requester, SimpleWebIntegration }

object MakeRequest extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    responseToLogoList((new Requester with SimpleWebIntegration)(dest, reqMethod, paramMap))
  }

}
