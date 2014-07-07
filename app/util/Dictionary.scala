package util

class Dictionary(val words: Seq[String]) extends Serializable {
  val wordToIndexMap = words.zipWithIndex.toMap

  val getIndex = wordToIndexMap
  val getWord = words

  val size = words.size
}
