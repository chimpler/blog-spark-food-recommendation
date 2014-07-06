package util

import scala.concurrent.ExecutionContext.Implicits.global
import dispatch.{url, Http}

object HttpClient {
  val http = Http.configure(_ setFollowRedirects true setRequestTimeoutInMs 10000 setConnectionTimeoutInMs 10000 setMaxRequestRetry 3 setCompressionEnabled true)

  def fetchUrl(urlToFetch: String) = {
    val svc = url(urlToFetch)
    http(svc)
  }
}
