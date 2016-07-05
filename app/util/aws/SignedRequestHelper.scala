/**
  * Based on https://github.com/koduki/eBookSearch/blob/c7384934aaffa5ca515722a7c7be41f067415319/src/main/scala/commons/aws/SignedRequestsHelper.scala
  */
package util.aws

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Calendar, Date, Iterator, SortedMap, TimeZone, TreeMap, Map => JavaMap}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64

class SignedRequestsHelper {
  private val UTF8_CHARSET: String = "UTF-8"
  private val HMAC_SHA256_ALGORITHM: String = "HmacSHA256"
  private val REQUEST_URI: String = "/onca/xml"
  private val REQUEST_METHOD: String = "GET"



  private var secretKeySpec: SecretKeySpec = null

  def sign(awsAccessKeyId: String, awsSecretKey: String, endpoint: String)(params: Map[String, String], timestamp: Date = Calendar.getInstance().getTime()): String = {
    val sortedParamaters = initParamaters(awsAccessKeyId, params, timestamp)

    val canonicalQS = canonicalize(sortedParamaters)
    val signedQuery = REQUEST_METHOD + "\n" + endpoint + "\n" + REQUEST_URI + "\n" + canonicalQS

    val hmac2 = hmac(mac(awsSecretKey), signedQuery)
    val sig = percentEncodeRfc3986(hmac2)

    return "http://" + endpoint + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig
  }

  private def mac(awsSecretKey: String) = {
    val secretyKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET)
    secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM)
    val mac = Mac.getInstance(HMAC_SHA256_ALGORITHM)
    mac.init(secretKeySpec)
    mac
  }

  private def hmac(mac: Mac, stringToSign: String): String = {
    var signature: String = null
    try {
      var data = stringToSign.getBytes(UTF8_CHARSET)
      var rawHmac = mac.doFinal(data)
      var encoder = new Base64(0)
      signature = new String(encoder.encode(rawHmac))
    } catch {
      case e: UnsupportedEncodingException => throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e)
    }
    return signature
  }

  private def format(date: Date): String = {
    var timestamp: String = null
    var dfm: DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    dfm.setTimeZone(TimeZone.getTimeZone("GMT"))
    timestamp = dfm.format(date)

    return timestamp
  }

  private def canonicalize(sortedParamMap: SortedMap[String, String]): String = {
    if (sortedParamMap.isEmpty()) return ""

    var buffer = new StringBuilder()
    var iter: Iterator[JavaMap.Entry[String, String]] = sortedParamMap.entrySet().iterator()

    while (iter.hasNext()) {
      var kvpair: JavaMap.Entry[String, String] = iter.next()
      buffer.append(percentEncodeRfc3986(kvpair.getKey()))
      buffer.append("=")
      buffer.append(percentEncodeRfc3986(kvpair.getValue()))
      if (iter.hasNext()) {
        buffer.append("&")
      }
    }
    var cannoical = buffer.toString()
    return cannoical
  }

  private def percentEncodeRfc3986(s: String): String = {
    var out: String = null
    try {
      out = URLEncoder.encode(s, UTF8_CHARSET)
        .replace("+", "%20")
        .replace("*", "%2A")
        .replace("%7E", "~")
    } catch {
      case e: UnsupportedEncodingException => out = s
    }
    return out
  }

  private def initParamaters(awsAccessKeyId: String, params: Map[String, String], timestamp: Date): java.util.TreeMap[String, String] = {

    val sortedParamMap = new TreeMap[String, String]()
    for (param <- params) {
      sortedParamMap.put(param._1, param._2)
    }
    sortedParamMap.put("AWSAccessKeyId", awsAccessKeyId)
    sortedParamMap.put("Timestamp", format(timestamp))

    sortedParamMap
  }

}