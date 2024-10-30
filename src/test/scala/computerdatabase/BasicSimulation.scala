package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://mobile.broker.vtb.ru") // Here is the root for all relative URLs
    .header("Accept", "*/*")
    .acceptHeader(
      "application/json"
    ) // Here are the common headers
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .header("Connection", "keep-alive")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
    )
//  val initStarted = new AtomicBoolean(false)
//  val initCompleteLatch = new CountDownLatch(1)

//  scenario("myScenario”)
//    .doIf(_ => initStarted.getAndSet(true) == false) {
//      exec(http("first”)
//        .get("/endpoint")
//        .check(status.is(200),
//          jsonPath("$…response.id").findAll.saveAs("reponseVariable”)
//          )
//        ).exec { session =>
//        reponseVariable = session("reponseVariable").as[String] initCompleteLatch.countDown() session
//      }
//    }
//    .exec { session =>
//      initCompleteLatch.await()
//      session.set("reponseVariable”, reponseVariable)
//    }
//    .exec(http("second”)
//      .post("/another/endpoint/{responseVariable}")
//      .body(…)
//  .check(status.is(200))
//  )

  val scn =
    scenario("auth") // A scenario is a chain of requests and pauses
      //.feed()
      .exec(
        http("/authenticate/mobileapp/mi?realm=/myinvestments")

          .post("/authenticate/mobileapp/mi?realm=/myinvestments")
          .header("Content-Type", "application/json")
          .body(StringBody("""{
                             |    "callbacks": [
                             |        {
                             |            "type": "NameCallback",
                             |            "input":
                             |            [
                             |                {
                             |                    "name": "IDToken1",
                             |                    "value": "olbv0019508"
                             |                }
                             |            ]
                             |        },
                             |        {
                             |            "type": "PasswordCallback",
                             |            "input":
                             |            [
                             |                {
                             |                    "name": "IDToken2",
                             |                    "value": "larinMB1884!"
                             |                }
                             |            ]
                             |        }
                             |    ]
                             |}""".stripMargin))
          .check(status.is(200))
          .check(substring(session => "tokenId"))
          .check(jsonPath("$.tokenId").saveAs("tokenId"))

        //check(regex("XXXXXX").findAll.transform(selectRandomElementInList).saveAs("idnr"))
      )
      .exec(
        http("/passport/oauth2/token?realm=/myinvestments")
          .post("/passport/oauth2/token?realm=/myinvestments")

          .basicAuth("mpmi_trusted@trust.vtb.ru", "#Ph%3jL*uHL8")

          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("grant_type", "authorization_code")
          .formParam("access_type", "offline")
          .formParam("code", "${tokenId}")
          .check(status.is(200))
          //.check(substring("orderId").findAll.saveAs("orderId"))

      )
//  val scn1 =
//    scenario("Logic") // A scenario is a chain of requests and pauses
//      .exec(
//        http("Create")
//          .post("/ui-api-mi/kib/itrd/mi_api/v1/mi_api/order/create")
//          .body("{\n\n\"accountName\": \"${diasoftNumber}\",\n\n\"securCode\":\"CNYRUB_1\",\n\n\"secBoard\":\"CETS\",\n\n\"bs\":1,\n\n\"amount\": 1,\n\n\"type\": 1,\n\n\"sourceObjectId\": \"1087269246\",\n\n\"sourceType\": 1,\n\n\"price\": 799661\n\n}")
//          .check(substring("orderId").findAll.saveAs("orderId"))
//      )
//      .pause(1 second)
//      .exec(
//        http("Confirm")
//          .post("/ui-api-mi/kib/itrd/mi_api/v1/mi_api/order/confirm/&{}")
//          .check(status.is(200))
//          //.body("wad")
//      )
      //.pause(7) // Note that Gatling has recorder real time pauses

  setUp(
    scn.inject(
      atOnceUsers(1))
//    nothingFor(5 seconds),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5),
//    rampUsersPerSec(0).to(20).during(12.minutes),
//    constantUsersPerSec(20).during(5))
    .protocols(httpProtocol))
}
