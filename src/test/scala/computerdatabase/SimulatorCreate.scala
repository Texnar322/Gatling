package computerdatabase

import io.gatling.core.Predef.{atOnceUsers, _}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.concurrent.duration.DurationInt

class SimulatorCreate extends Simulation {

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
  val accountNumber = csv("data/accountNumber.csv").circular()
  val price = csv("data/price.csv").circular()
//  val executionTime = 2 hours
//  val authTimeout = 10 minutes
//  val safetyMargin = 30 seconds
//
//  val authenticate: ChainBuilder = exec(refresh)
//    .exec(session => session.set("timeout", authTimeout.fromNow))
///// доделать
  def refresh() = {
    repeat(1) {

      exec(
        http("/passport/oauth2/token?realm=/myinvestments")
          .post("/passport/oauth2/token?realm=/myinvestments")
          .basicAuth("mpmi_trusted@trust.vtb.ru", "#Ph%3jL*uHL8")
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("grant_type", "refresh_token")
          .formParam("refresh_token", "offline")
          //.formParam("code", "${tokenId}")
          .check(status.is(200))
          .check(jsonPath("$.access_token").saveAs("access_token"))
          .check(jsonPath("$.refresh_token").saveAs("refresh_token"))
      )
    }
  }

/////
  def Create() = {
    repeat(1) {
      feed(accountNumber)
      feed(price)
      exec(
        http("Create")
          .post("/ui-api-mi/kib/itrd/mi_api/v1/mi_api/order/create")
          //.header("Authorization", "${access_token}")
          .header("Authorization", "AQIC5wM2LY4SfcyapmlnEck4DIz8eomgacXxekuiaC_hFiQ.*AAJTSQACMDIAAlNLABQtMTg1NDg5ODc0OTM1NDQ3OTA2MAACUzEAAjAx*")
          .header("Content-Type", "application/json")
          .body(StringBody(
            """{
              |    "accountNumber": "${accountNumber}",
              |    "securCode": "MTLR",
              |    "secBoard": "TQBR",
              |    "bs": "B",
              |    "amount": 1,
              |    "type": 1,
              |    "sourceType": 1,
              |    "price": ${price}
              |}""".stripMargin))
          .check(jsonPath("$.orderId").saveAs("orderId"))
      )
        .exec(
          http("Confirm")
            .post("/ui-api-mi/kib/itrd/mi_api/v1/mi_api/order/confirm/${orderId}")
            .header("Authorization", "AQIC5wM2LY4SfcyapmlnEck4DIz8eomgacXxekuiaC_hFiQ.*AAJTSQACMDIAAlNLABQtMTg1NDg5ODc0OTM1NDQ3OTA2MAACUzEAAjAx*")
            //.header("Authorization", "${access_token}")
            .header("Content-Type", "application/json")
            .body(StringBody(
              """{}""".stripMargin))
        )
    }
  }


  val scn1 = scenario("Code reuse")

        .exec(Create())
//    .exec(authenticate )
//    .during(executionTime) {
//      doIf(session => {
//        session.get("timeout").as[Deadline].timeleft <= safteyMargin}) {
//        exec(authenticate )
//      }
    //.exec(refresh())



    //.exec(Confirm())

  setUp(
    scn1.inject(
      atOnceUsers(1)
    //    nothingFor(5 seconds),
    //    rampUsersPerSec(0).to(60).during(7.minutes),
    //    constantUsersPerSec(60).during(5),
    //    rampUsersPerSec(60).to(120).during(7.minutes),
    //    constantUsersPerSec(120).during(5),
    //    rampUsersPerSec(120).to(180).during(7.minutes),
    //    constantUsersPerSec(180).during(5),
    //    rampUsersPerSec(180).to(240).during(7.minutes),
    //    constantUsersPerSec(240).during(5),
    //    rampUsersPerSec(240).to(300).during(7.minutes),
    //    constantUsersPerSec(300).during(5),
    //    rampUsersPerSec(300).to(360).during(7.minutes),
    //    constantUsersPerSec(360).during(5),
    //    rampUsersPerSec(360).to(420).during(7.minutes),
    //    constantUsersPerSec(420).during(5),
    //    rampUsersPerSec(420).to(480).during(7.minutes),
    //    constantUsersPerSec(480).during(5),
    //    rampUsersPerSec(480).to(540).during(7.minutes),
    //    constantUsersPerSec(540).during(5),
    //    rampUsersPerSec(540).to(600).during(7.minutes),
    //    constantUsersPerSec(600).during(5))
    //Cупер целевой
      // 1 тест
//        rampUsersPerSec(0).to(150).during(30.seconds), // 2 250
//        constantUsersPerSec(150).during(30.seconds), // 2250 + 4500 = 6750
//        rampUsersPerSec(150).to(300).during(30.seconds), // 6750 + 6750 = 13 500
//        constantUsersPerSec(300).during(10.seconds) // 13 500 + 4500 = 18 000

  ).protocols(httpProtocol))
}
