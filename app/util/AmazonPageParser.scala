package util

import model.AmazonProduct
import util.aws.AmazonWebService
import play.Configuration


import scala.concurrent.Future

object AmazonPageParser {
  def parse(productId: String): Future[AmazonProduct] = {
    val subscriptionId =  Configuration.root().getString("aws.subscriptionId")
    val secretKey =  Configuration.root().getString("aws.secretKey")
    val associateTag =  Configuration.root().getString("aws.associateTag")
    val endpoint =  Configuration.root().getString("aws.endpoint")

    val aws = new AmazonWebService(subscriptionId, secretKey, associateTag, endpoint)

    aws.findItem(productId)

  }

}
