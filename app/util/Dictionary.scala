package util

class Dictionary(words: Seq[String])  {
  val wordToIndexMap = words.zipWithIndex.toMap

  val getIndex = wordToIndexMap
  val getWord = words

  val size = words.size
}
