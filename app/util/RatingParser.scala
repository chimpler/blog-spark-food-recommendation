package util

import java.io.File

import model.Rating

import scala.io.Source

object RatingParser {
  def parse(file: File) = {
    for (line <- Source.fromFile(file).getLines()) yield {
      val Array(itemId, userId, scoreStr) = line.split(",")
      Rating(itemId, userId, scoreStr.toDouble)
    }
  }
}
