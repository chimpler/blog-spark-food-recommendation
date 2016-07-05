package util

import com.ning.http.client.AsyncHttpClientConfig

import scala.concurrent.ExecutionContext.Implicits.global
import dispatch.{Http, url}

object HttpClient {
  val builder = new AsyncHttpClientConfig.Builder()
  builder.setFollowRedirect(true)
  builder.setRequestTimeout(3000)
  builder.setConnectTimeout(10000)
  builder.setMaxRequestRetry(3)


  val http = Http.configure(_ => builder)


  def fetchUrl(urlToFetch: String) = {
    val svc = url(urlToFetch)
    http(svc)
  }
}
