package org.nlogo.extensions.web.prim.util

import org.nlogo.api.ReporterRunnable
import org.nlogo.awt.EventQueue
import org.nlogo.nvm.Workspace
import org.nlogo.swing.Implicits

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:12 PM
 */

object EventEvaluator {

  import actors.Actor

  sealed protected trait EventActorProtocol
  protected object EventActorProtocol {
    case object Evaluate extends EventActorProtocol
  }

  import EventActorProtocol._

  protected class EventEvaluationActor[T, U](stream: T, func: (T) => U) extends Actor {
    import Implicits.thunk2runnable
    def act() {
      loop {
        react {
          case Evaluate => EventQueue.invokeLater{ () => reply(func(stream)); }
        }
      }
    }
  }

  // The stupid `start` method only returns an `Actor`; can't have a more specific return type --JAB (10/23/12)
  protected def generateActor[T, U](stream: T, hook: (T) => U) : Actor =new EventEvaluationActor(stream, hook).start()

  def apply[T, U](stream: T, hook: (T) => U) : U =
    (generateActor(stream, hook) !! Evaluate)().asInstanceOf[U]

}

