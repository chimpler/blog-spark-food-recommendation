package util.aws

/**
  * Based on https://github.com/koduki/eBookSearch/blob/c7384934aaffa5ca515722a7c7be41f067415319/src/main/scala/commons/aws/AmazonWebService.scala
  */

import java.text.SimpleDateFormat
import java.util.Date

import model.AmazonProduct
import play.Logger

import scala.concurrent.Future
import scala.io.Source
import scala.xml.XML

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by tomlous on 03/07/16.
  */
class AmazonWebService(awsAccessKeyId: String, awsSecretKey: String, associateTag: String, endpoint: String) {
  val signedRequestsHelper = new SignedRequestsHelper()
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")


  def searchItem(keyword: String): Future[List[AmazonProduct]] = {
    val params = Map(
      "Timestamp" -> dateFormat.format(new Date()),
      "Operation" -> "ItemSearch",
      "SearchIndex" -> "Books",
      "ResponseGroup" -> "Images,ItemAttributes,Small",
      "Keywords" -> keyword,
      "AssociateTag" -> associateTag,
      "Sort" -> "titlerank",
      "Service" -> "AWSECommerceService")


    val queryUrl = signedRequestsHelper.sign(awsAccessKeyId, awsSecretKey, endpoint)(params)
    Future {
      val xml = readXml(queryUrl)
      (xml \\ "Item").map { node => parse(node) }.toList
    }

  }

  def findItem(asin: String): Future[AmazonProduct] = {
    Logger.info("Find:" +asin)

    val params = Map(
      "Timestamp" -> dateFormat.format(new Date()),
      "Operation" -> "ItemLookup",
      "SubscriptionId" -> awsAccessKeyId,
      "ResponseGroup" -> "Images,ItemAttributes,Large",
      "ItemId" -> asin,
      "AssociateTag" -> associateTag,
      "IdType" -> "ASIN",
      "Service" -> "AWSECommerceService")

    val queryUrl = signedRequestsHelper.sign(awsAccessKeyId, awsSecretKey, endpoint)(params)

    Future {
      val xml = readXml(queryUrl)
      (xml \\ "Item").map { node => parse(node) }.toList(0)
    }
  }



  protected def readXml(queryUrl: String): scala.xml.Elem = {
//    Logger.info(queryUrl)
    val source = Source.fromURL(queryUrl, "UTF-8")
    val xml = XML.loadString(source.mkString)
    xml
  }

  def parse(node: scala.xml.Node) = {
    val asin = node \ "ASIN" text
    val author = node \ "ItemAttributes" \ "Author" text
    val manufacturer = node \ "ItemAttributes" \ "Manufacturer" text
    val title = node \ "ItemAttributes" \ "Title" text
    val url = node \ "DetailPageURL" text
    val small = node \ "SmallImage" \ "URL" text
    val medium = node \ "MediumImage" \ "URL" text
    val large = node \ "LargeImage" \ "URL" text
    //    val description = nodeSelector.select("div#feature-bullets").headOption.map(_.getHtml).mkString

    Logger.info(s"Found: ${asin} (${title})")

    AmazonProduct(asin, title, url, medium, author + " " + manufacturer)
  }

}