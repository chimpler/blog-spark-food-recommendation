package util

import jodd.lagarto.dom.{NodeSelector, LagartoDOMBuilder}
import model.AmazonItem
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AmazonPageParser {
  def parse(itemId: String): Future[AmazonItem] = {
    val url = s"http://www.amazon.com/dp/$itemId"
    HttpClient.fetchUrl(url) map {
      httpResponse =>
        if (httpResponse.getStatusCode == 200) {
          val body = httpResponse.getResponseBody
          val domBuilder = new LagartoDOMBuilder()
          val doc = domBuilder.parse(body)

          val responseUrl = httpResponse.getUri.toString
          val nodeSelector = new NodeSelector(doc)
          val title = nodeSelector.select("span#productTitle").head.getTextContent
          val img = nodeSelector.select("div#main-image-container img").head.getAttribute("src")
          val description = nodeSelector.select("div.productDescriptionWrapper").headOption.map(_.getHtml).mkString

          AmazonItem(itemId, title, responseUrl, img, description)
        } else {
          throw new RuntimeException(s"Invalid url $url")
        }
    }
  }
}
