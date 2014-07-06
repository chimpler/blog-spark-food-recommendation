package model

case class AmazonItem(itemId: String, title: String, url: String, img: String, description: String)
case class AmazonRating(itemId: String, userId: String, rating: Double)