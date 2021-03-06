package com.rayabavj

import scala.collection._
import scala.io.Source
import java.io._

object fc_test {
    def main(args: Array[String]) {
      
      val bufferedSource = Source.fromFile("data/testInp_v2.txt")
      val item_trans_map = ItemsMapTrans()
      for (line <- bufferedSource.getLines) {
        val item_trans_ar = line.split("\t")
        val trans_set = item_trans_ar(1).split(",").map(_.toInt)
        val trans = new mutable.TreeSet[Int]()++trans_set
        val itemset = new ItemSet()+=(item_trans_ar(0),trans_set.size)
        item_trans_map.imt+=( itemset -> trans)
      }
      bufferedSource.close
      val it = item_trans_map.imt.iterator
      while(it.hasNext){
        val one_map = it.next()
        println(one_map._1.isets.mkString("")+ "sup"+one_map._1.sup+"=>"+one_map._2.mkString(" ")) 
      }
      
      val concepts = Charm(item_trans_map, 1)
      concepts.toString
      var concepts_str = new mutable.ListBuffer[Tuple2[String, String]]
      val file = new File("data/testInp_v2_out.txt")
      val bw = new BufferedWriter(new FileWriter(file))
      for(concept <- concepts.imt){
        val itemset = concept._1.isets
        val trans = concept._2
        concepts_str += Tuple2(trans.mkString(" "), itemset.mkString(" "))
        bw.write(trans.mkString(" ")+"\t"+itemset.mkString(" ")+"\n")
        }
        println(concepts_str.toList.size)
        bw.close()  
    }
}