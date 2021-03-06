/*
 * Copyright (C) 2009-2020 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.http.scaladsl.server
package directives

import scala.concurrent.Future
import scala.collection.immutable
import akka.http.scaladsl.marshalling.{ ToEntityMarshaller, ToResponseMarshallable }
import akka.http.scaladsl.model._
import StatusCodes._
import akka.http.scaladsl.util.FastFuture._

/**
 * @groupname route Route directives
 * @groupprio route 200
 */
trait RouteDirectives {

  /**
   * Rejects the request with an empty set of rejections.
   *
   * @group route
   */
  def reject: StandardRoute = RouteDirectives._reject

  /**
   * Rejects the request with the given rejections.
   *
   * @group route
   */
  def reject(rejections: Rejection*): StandardRoute =
    StandardRoute(_.reject(rejections: _*))

  /**
   * Completes the request with redirection response of the given type to the given URI.
   *
   * @group route
   */
  def redirect(uri: Uri, redirectionType: Redirection): StandardRoute =
    StandardRoute(_.redirect(uri, redirectionType))

  /**
   * Completes the request using the given arguments.
   *
   * @group route
   */
  def complete(m: => ToResponseMarshallable): StandardRoute =
    StandardRoute(_.complete(m))

  /**
   * Completes the request using the given arguments.
   *
   * @group route
   */
  def complete[T](status: StatusCode, v: => T)(implicit m: ToEntityMarshaller[T]): StandardRoute =
    StandardRoute(_.complete((status, v)))

  /**
   * Completes the request using the given arguments.
   *
   * @group route
   */
  def complete[T](status: StatusCode, headers: immutable.Seq[HttpHeader], v: => T)(implicit m: ToEntityMarshaller[T]): StandardRoute =
    complete((status, headers, v))

  /**
   * Bubbles the given error up the response chain, where it is dealt with by the closest `handleExceptions`
   * directive and its ExceptionHandler.
   *
   * @group route
   */
  def failWith(error: Throwable): StandardRoute =
    StandardRoute(_.fail(error))

  /**
   * Handle the request using a function.
   *
   * @group route
   */
  def handle(handler: HttpRequest => Future[HttpResponse]): StandardRoute =
    { ctx => handler(ctx.request).fast.map(RouteResult.Complete)(ctx.executionContext) }

}

object RouteDirectives extends RouteDirectives {
  private val _reject = StandardRoute(_.reject())
}
