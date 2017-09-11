package com.example.ecs1

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import com.example.ecs1.queue.QueuePutter

import scala.concurrent.Future
//import com.lightbend.akka.http.sample.UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout
import spray.json.{ JsNumber, JsObject, JsString }

//#user-routes-class
//case class UserRoutes(putter: QueuePutter)(implicit system: ActorSystem) extends JsonSupport {
case class UserRoutes(putter: QueuePutter) extends JsonSupport {
  //#user-routes-class

//  lazy val log = Logging(system, classOf[UserRoutes])

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  //#users-get-post
  //#users-get-delete   
  lazy val userRoutes: Route =
    pathPrefix("payload") {
      post {
        entity(as[ApiPayload]) { payload =>
          val messageCreated = putter.put(payload)

          onSuccess(messageCreated) { msgId =>
            complete((StatusCodes.OK, JsObject(
              "test" -> JsNumber(payload.data.length),
              "messageId" -> JsString(msgId.data)
            )))

          }
        }
      } ~
      get {
        complete("Ok NON-REP!!!!!")
      }
    } // ~
  //      pathPrefix("users") {
  //        concat(
  //          //#users-get-delete
  //          pathEnd {
  //            concat(
  //              get {
  //                val users: Future[Users] =
  //                  (userRegistryActor ? GetUsers).mapTo[Users]
  //                complete(users)
  //              },
  //              post {
  //                entity(as[User]) { user =>
  //                  val userCreated: Future[ActionPerformed] =
  //                    (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
  //                  onSuccess(userCreated) { performed =>
  //                    log.info("Created user [{}]: {}", user.name, performed.description)
  //                    complete((StatusCodes.Created, performed))
  //                  }
  //                }
  //              }
  //            )
  //          },
  //          //#users-get-post
  //          //#users-get-delete
  //          path(Segment) { name =>
  //            concat(
  //              get {
  //                //#retrieve-user-info
  //                val maybeUser: Future[Option[User]] =
  //                  (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
  //                rejectEmptyResponse {
  //                  complete(maybeUser)
  //                }
  //                //#retrieve-user-info
  //              },
  //              delete {
  //                //#users-delete-logic
  //                val userDeleted: Future[ActionPerformed] =
  //                  (userRegistryActor ? DeleteUser(name)).mapTo[ActionPerformed]
  //                onSuccess(userDeleted) { performed =>
  //                  log.info("Deleted user [{}]: {}", name, performed.description)
  //                  complete((StatusCodes.OK, performed))
  //                }
  //                //#users-delete-logic
  //              }
  //            )
  //          }
  //        )
  //        //#users-get-delete
  //      }
  //  //#all-routes
}
