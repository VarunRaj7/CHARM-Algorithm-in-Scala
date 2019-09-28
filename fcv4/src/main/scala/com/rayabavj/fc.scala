package com.rayabavj

import scala.collection._

import scodec.bits._

import org.apache.spark.sql.SparkSession

import org.apache.spark.RangePartitioner
import org.apache.spark.HashPartitioner
import org.apache.spark.storage.StorageLevel


object fc {
    def main(args: Array[String]) {
      
      val spark = SparkSession
                  .builder
                  .master("local")
                  .appName("FileRead")
                  .getOrCreate()
      
      /*
      var x: BitVector = bin"1011"
      
      x = bin"1000"
      
      val y:BitVector = bin"1011"
      
      println(x)
      
      println(y)
      
      println(x==y)
      
      println(x|y)
      
      val bit_ar = Array(x,y)
      
      println(bit_ar(0))
      
      */
      val tfile_rdd = spark.sparkContext.textFile(args(3).toString)
      
      val n_trans = spark.sparkContext.broadcast(args(0).toInt)
      
      println(n_trans.value)
      
      def func1(line:String):Tuple2[String, BitVector]={
        
        val item_trans_ar = line.split("\t")
        
        var trans_bitv = BitVector.low(n_trans.value+1)
        
        val trans_set = item_trans_ar(1).split(",")
        
        trans_set.foreach{ trans => trans_bitv = trans_bitv.set(trans.toInt) }
        
        val res_tup = (item_trans_ar(0), trans_bitv)
        
        res_tup
       }
      
      val item_trans_bitv = tfile_rdd.map(func1)
      
      /*
      val item_trans_bitv_ar = item_trans_bitv.collect()
      
      val item1 = item_trans_bitv_ar(1)
      */
      
      def two_hop_func(item_bitv:Tuple2[String, BitVector]):Tuple2[Tuple2[String,BitVector],List[String]] ={
        val key = item_bitv
        val word = new mutable.ListBuffer[String]
        for (i <- 1 to n_trans.value){
          if (item_bitv._2.get(i)){
            word+=i.toString
          }
        }
        (key,word.toList)
      }
      
      val two_hop_rdd = item_trans_bitv.map(two_hop_func)
                                       .flatMapValues(x=>x)
                                       .map(x => (x._2,List(x._1)))
                                       .reduceByKey(_++_)
                                       
      //val res = two_hop_rdd.collect()
      
      //res.map(println)
      
      //two_hop_rdd.saveAsTextFile("data/plants_thop")
                                       
      /* Partition Code*/
      val tunedPartitioner1 = new RangePartitioner(10, two_hop_rdd) 
      val two_hop_p = two_hop_rdd.partitionBy(tunedPartitioner1).persist(StorageLevel.MEMORY_AND_DISK)
                                       
      val minsup = spark.sparkContext.broadcast(args(1).toInt)
      val trans_size = spark.sparkContext.broadcast(args(2).toInt)
                                       
      def func2(t:Tuple2[String, List[Tuple2[String,BitVector]]]):List[Tuple2[BitVector, String]]={       
        val item_trans_list = t._2
        val item_trans_map = ItemsMapTrans()
        item_trans_list.foreach{ item_trans => val item = new ItemSet()+=(item_trans._1,item_trans._2.populationCount.toInt)
                                                item_trans_map.imt+=( item -> item_trans._2)}
        val b_minsup = minsup.value
        val concepts = Charm(item_trans_map, b_minsup, n_trans.value)
        var concepts_str = new mutable.ListBuffer[Tuple2[BitVector, String]]
        for(concept <- concepts.imt){
          val itemset = concept._1.isets
          val trans = concept._2
//          if (itemset.size>trans_size.value){
            concepts_str += Tuple2(trans, itemset.mkString(" "))
//            }
          }
          concepts_str.toList  
      }
      
      val fc_1 = two_hop_p.map(func2).flatMap(x=>x)
  
      val tunedPartitioner2 = new HashPartitioner(10) 
      val fc_4 = fc_1.partitionBy(tunedPartitioner2).persist(StorageLevel.MEMORY_AND_DISK)

      val fc_5 = fc_4.reduceByKey((a,b) => a)
      
      fc_5.saveAsTextFile(args(4))//.collect.map(println)
      
      /*
      def func3(t:Tuple2[BitVector, String]):String={
        var key = ""
        //val bitv = spark.sparkContext.parallelize(t._1.toIndexedSeq.zipWithIndex).filter{_._1}.map{case(b,ind) => ind}.collect()
        val bitv = t._1.toIndexedSeq.zipWithIndex.collect{case(a,b) if a => b}
        bitv.mkString(" ")+","+t._2
      }    
      val fc_2 = fc_1.map(func3)
      fc_2.collect().map(println)//.saveAsTextFile(args(4))
      * 
      */
      
    }
}