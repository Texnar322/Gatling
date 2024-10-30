package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import scala.reflect.io.Path

class Simulator extends Simulation {

  val httpProtocol = http
    .baseUrl("https://mobile.broker.vtb.ru") // Here is the root for all relative URLs
    .header("Accept", "*/*")
    .acceptHeader(
      "application/json"
    ) // Here are the common headers
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
    )

  val initStarted = new AtomicBoolean(false)
  val initCompleteLatch = new CountDownLatch(1)

  def authenticate() = {
      exec(
        http("/authenticate/mobileapp/mi?realm=/myinvestments")
          .post("/authenticate/mobileapp/mi?realm=/myinvestments")
          .header("Content-Type", "application/json")
          .body(StringBody(
            """{
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


      )



  }


  def oauth2() = {

      exec(
        http("/passport/oauth2/token?realm=/myinvestments")
          .post("/passport/oauth2/token?realm=/myinvestments")

          .basicAuth("mpmi_trusted@trust.vtb.ru", "#Ph%3jL*uHL8")

          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("grant_type", "authorization_code")
          .formParam("access_type", "offline")
          .formParam("code", "${tokenId}")
          .check(status.is(200))
          .check(jsonPath("$.access_token").saveAs("access_token"))
          .check(jsonPath("$.refresh_token").saveAs("refresh_token"))
      )

  }
/////
  def refresh() = {
    repeat(1) {
      exec(
        http("/passport/oauth2/token?realm=/myinvestments")
          .post("/passport/oauth2/token?realm=/myinvestments")
          .basicAuth("mpmi_trusted@trust.vtb.ru", "#Ph%3jL*uHL8")
          .header("content-type", "application/x-www-form-urlencoded")
          .formParam("grant_type", "authorization_code")
          .formParam("access_type", "offline")
          .formParam("code", "${tokenId}")
          .check(status.is(200))
          //.check(jsonPath("$.access_token").saveAs(session => session("access_token").as[String]))
          .check(jsonPath("$.refresh_token").saveAs("refresh_token"))
          .check(jsonPath ("$..response.id").findAll.saveAs("reponseVariable")
      )

      )
    }
  }
/////
  def Create() = {
    repeat(1) {
      exec(
              http("Create")
                .post("/ui-api-mi/kib/itrd/mi_api/v1/mi_api/order/create")
                .header("Authorization", "")
                .header("Content-Type", "application/json")
                .body(StringBody(
                  """{
                    |    "accountNumber": "500119",
                    |    "securCode": "CNYRUB_TOM",
                    |    "secBoard": "CETS",
                    |    "bs": "B",
                    |    "amount": 1000,
                    |    "type": 1,
                    |    "sourceType": 1,
                    |    "price": 14
                    |}""".stripMargin))
                .check(jsonPath("$.orderId").saveAs("orderId"))

      )
    }
  }

  def Confirm() = {
    repeat(1) {
      exec(
        http("Confirm")
          .post("/ui-api-mi/kib/itrd/mi_api/v1/mi_api/order/confirm/${orderId}")
          .header("Authorization", "${access_token}")
          .header("Content-Type", "application/json")
          .body(StringBody(
            """{}""".stripMargin))
      )
    }
  }

  val scn1 = scenario("Code reuse")
//    doWhile()
//      {
//        .exec(authenticate())
//        .exec(oauth2())
//      }

    .pause(1)
    .exec(Create())

 // val scn = scenario("Code reuse1")

  var reponseVariable = ""

  val scn2 = scenario("myScenario")
    .doIf(_ => initStarted.getAndSet(true) == false) {
      exec(authenticate())
        .exec(oauth2())
      .exec { session => reponseVariable = session("reponseVariable").as[String]
        initCompleteLatch.countDown()
        session
      }
    }
    .exec { session =>
      initCompleteLatch.await()
      session.set("reponseVariable", reponseVariable)
    }
    .exec(Create())


    //.exec(Confirm())

  setUp(
    //scn1.inject(atOnceUsers(1)),
    scn2.inject(atOnceUsers(3))
    //    nothingFor(5 seconds),
    //    rampUsersPerSec(0).to(60).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(60).to(120).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(120).to(180).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(180).to(240).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(300).to(360).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(420).to(20).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(0).to(20).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(0).to(20).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(0).to(20).during(7.minutes),
    //    constantUsersPerSec(20).during(5),
    //    rampUsersPerSec(0).to(20).during(7.minutes),
    //    constantUsersPerSec(20).during(5))
  ).protocols(httpProtocol)

}
