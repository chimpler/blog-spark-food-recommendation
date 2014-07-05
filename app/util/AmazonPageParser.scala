package util

import jodd.lagarto.dom.{NodeSelector, LagartoDOMBuilder}
import model.Item
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AmazonPageParser {
  def parse(itemId: String): Future[Item] = {
    HttpClient.fetchUrl(s"http://www.amazon.com/dp/$itemId") map {
      httpResponse =>
        val body = httpResponse.getResponseBody
        val domBuilder = new LagartoDOMBuilder()
        val doc = domBuilder.parse(body)

        val responseUrl = httpResponse.getUri.toString
        val nodeSelector = new NodeSelector(doc)
        val title = nodeSelector.select("span#productTitle").head.getTextContent
        val img = nodeSelector.select("div#main-image-container img").head.getAttribute("src")
        val description = nodeSelector.select("div.productDescriptionWrapper").headOption.map(_.getHtml).mkString

        Item(itemId, title, responseUrl, img, description)
    }
  }
}
