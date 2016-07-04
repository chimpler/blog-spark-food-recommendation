package model

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class AmazonProduct(itemId: String, title: String, url: String, img: String, description: String)
case class AmazonRating(userId: String, productId: String, rating: Double)

case class AmazonProductAndRating(product: AmazonProduct, rating: AmazonRating)

// For MongoDB
object AmazonRating {
  implicit val amazonRatingHandler = Macros.handler[AmazonRating]
  implicit val amazonRatingFormat = Json.format[AmazonRating]
}

