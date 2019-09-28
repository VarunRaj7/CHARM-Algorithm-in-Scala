package com.rayabavj

import scala.collection._

import breeze.linalg._

import org.apache.spark.sql.SparkSession

object fc_v3 {
  def main(args: Array[String]) {
    
    val spark = SparkSession
            .builder
            .master("local")
            .appName("FileRead")
            .getOrCreate()
            
    val tfile_rdd = spark.sparkContext.textFile(args(3).toString)
    
    val n_trans = spark.sparkContext.broadcast(args(0).toInt)
    
    println(n_trans.value)
    
    def func1(line:String):Tuple2[String, SparseVector[Boolean]]={
      
      val item_trans_ar = line.split("\t")
      
      var trans_sparsev = SparseVector.zeros[Boolean](n_trans.value+1)
      
      val trans_set = item_trans_ar(1).split(",")
      
      trans_set.foreach{ trans => trans_sparsev(trans.toInt) = true }
      
      val res_tup = (item_trans_ar(0), trans_sparsev)
      
      res_tup
     }
    
    val item_trans_sparsev = tfile_rdd.map(func1)
      
      
    //val item_trans_sparsev_ar = item_trans_sparsev.collect()
    
    //item_trans_sparsev_ar.map(println)

    def two_hop_func(item_sparsev:Tuple2[String, SparseVector[Boolean]]):Tuple2[Tuple2[String,SparseVector[Boolean]],List[String]] ={
      val key = item_sparsev
      val word = new mutable.ListBuffer[String]
      for (i <- 1 to n_trans.value){
        if (item_sparsev._2(i)){
          word+=i.toString
        }
      }
      (key,word.toList)
    }
    
    val two_hop_rdd = item_trans_sparsev.map(two_hop_func)
                                        .flatMapValues(x=>x)
                                        .map(x => (x._2,List(x._1)))
                                        .reduceByKey(_++_)
                                     
    val res = two_hop_rdd.collect()
    
    res.map(println)

    
    /*           
    val x = SparseVector.zeros[Boolean](5)
        
    x(0) = true
    x(4) = true
    
    println(x)
    
    val y = SparseVector.zeros[Boolean](5)
    
    y(1) = true
    y(4) = true
    
    println(x:&y)
    
    val z = SparseVector.zeros[Boolean](5)
    
    //z(1) = false
    z(4) = true
    //z(0) = false
    
    print(z)
    
    println(x:&y==z)
    * 
    */
    
  }
}