package model

case class Item(itemId: String, title: String, url: String, img: String, description: String)
case class Rating(itemId: String, userId: String, rating: Double)