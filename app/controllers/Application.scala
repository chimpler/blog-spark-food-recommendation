package controllers

import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import play.api.mvc._
import util.{AmazonPageParser, Dictionary}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import model.{AmazonItem, AmazonRating}

object Application extends Controller {
  val random = new Random()

  val sc = new SparkContext("local[4]", "recommender")
  sc.addJar("target/scala-2.10/blog-spark-recommendation_2.10-1.0-SNAPSHOT.jar")

  // first create an RDD out of the rating file
  lazy val ratings = sc.textFile("ratings.csv").map {
    line =>
      val Array(itemId, userId, scoreStr) = line.split(",")
      AmazonRating(itemId, userId, scoreStr.toDouble)
  }

  // create user and item dictionaries
  val userDict = new Dictionary(ratings.map(_.userId).distinct().collect)
  val itemDict = new Dictionary(ratings.map(_.itemId).distinct().collect)

  // convert to Spark Ratings using the dictionaries
  val sparkRatings = ratings.map {
    case AmazonRating(itemId, userId, score) =>
      Rating(userDict.getIndex(userId),
        itemDict.getIndex(itemId),
        score)
  }

  // train the recommender
//  ALS.train(sparkRatings, 2, 8)

  def index = Action.async {
    val itemId = itemDict.getWord(random.nextInt(itemDict.size))
    println(s"Item $itemId")
    AmazonPageParser.parse(itemId) map (item => Ok(views.html.rating(item)))
  }
}