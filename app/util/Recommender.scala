package util

import model.AmazonRating
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.{ALS, Rating}

import scala.util.Random

class Recommender(@transient sc: SparkContext) extends Serializable {
  val NumRecommendations = 10
  val MyUserId = 0

  @transient val random = new Random() with Serializable
  // first create an RDD out of the rating file

  val trainingRatings = sc.textFile("ratings.csv").map {
    line =>
      val Array(itemId, userId, scoreStr) = line.split(",")
      AmazonRating(userId, itemId, scoreStr.toDouble)
  }

  // create user and item dictionaries
  val userDict = new Dictionary("myself" +: trainingRatings.map(_.userId).distinct().collect)
  val productDict = new Dictionary(trainingRatings.map(_.productId).distinct().collect)

  private def toSparkRating(amazonRating: AmazonRating) = {
    Rating(userDict.getIndex(amazonRating.userId),
      productDict.getIndex(amazonRating.productId),
      amazonRating.rating)
  }

  private def toAmazonRating(rating: Rating) = {
    AmazonRating(userDict.getWord(rating.user),
      productDict.getWord(rating.product),
      rating.rating
    )
  }

  // convert to Spark Ratings using the dictionaries
  val sparkRatings = trainingRatings.map(toSparkRating)

  def getRandomProductId = productDict.getWord(random.nextInt(productDict.size))

  def predict(ratings: Seq[AmazonRating]) = {
    // train model
    val myRatings = ratings.map(toSparkRating)
    val myRatingRDD = sc.parallelize(myRatings)
    val model = ALS.train(sparkRatings ++ myRatingRDD, 5, 20, 0.1)

    val myProducts = myRatings.map(_.product).toSet
    val candidates = sc.parallelize((0 until productDict.size).filter(!myProducts.contains(_)))

    // get all products not in my history
    model.predict(candidates.map((MyUserId, _))).collect().sortBy(-_.rating).take(NumRecommendations).map(toAmazonRating)
  }
}
