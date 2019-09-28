package com.rayabavj

/* This is Charm Version for SCALA 2.11*/

import collection._
import util.control.Breaks._

/* ItemSet class to model the itemsets in the transaction data*/
class ItemSet(val isets:immutable.TreeSet[String] = immutable.TreeSet[String](), val sup:Int = 0)
extends Ordered[ItemSet]                // Ordered trait is required to compare ItemSets
//with mutable.Set[String]
//with mutable.SetLike[String,ItemSet]
{
//  var isets = mutable.TreeSet[String]()
  
  def +=(that:String, sup:Int):ItemSet = {
    new ItemSet(this.isets+that, sup)
  }
  
  /*
   * This function required to add  
   * one ItemSet to another ItemSet	*/
  def ++=(that:ItemSet, sup:Int):ItemSet = {
    new ItemSet(this.isets++that.isets, sup)
  }
  
  /* 
   * This function is required to 
   * compare to order the ItemSet	*/
  def compare(that:ItemSet):Int = {
    if (this.sup>that.sup) {
      1
    }
    else if (this.sup<that.sup){
      -1
    }
    else{
      this.isets.toString.compareTo(that.isets.toString)
    }
  }
  
  /*
   * This function checks if all the
   * "that" elements are present in this*/
  def containsAll(that:ItemSet):Boolean = {
    ((isets & that.isets) == that.isets)
  }
}

/* This case class is required as wrapper to the 'var' type variable
 * of immutable TreeMap. It is essential in adding elements to the 
 * variable of 'var' type when passed to a function */
case class ItemsMapTrans(var imt: immutable.TreeMap[ItemSet, mutable.TreeSet[Int]]
                               = immutable.TreeMap[ItemSet, mutable.TreeSet[Int]]())//(implicitly[Ordering[ItemSet]].reverse)) 


/* The Charm Class */                               
class Charm{
  
  val skipset = new mutable.TreeSet[ItemSet]()
  
  def charm(data:ItemsMapTrans, minsup: Int):ItemsMapTrans= {
    
//    val filteredata:mutable.Map[ItemSet, Set[Int]] = new mutable.HashMap()
    
//    val set_ItemSet:Set[ItemSet] = data.keySet
      
    val filteredata = ItemsMapTrans(data.imt.filter((t) => t._1.sup>=minsup))
    
    val c = ItemsMapTrans()
    
    charmExtended(filteredata, c, minsup)
    
    c
  }
  
  def charmExtended(nodes: ItemsMapTrans, c:ItemsMapTrans, minsup:Int):Unit = {
    
    val items: List[ItemSet] = nodes.imt.keySet.toList                            // Get the list of all the ItemSet
    
    for (i <- 0 until items.size){                                            // Iterate over all the ItemSet            
          
      var xi = items(i)
      
      breakable {                                                             // Using breakable to continue to 
      if (skipset.contains(xi)){                                              // next iteration if it is present in skipset
          break                                                                
        }
      else{
          var x_prev = xi
          var x = xi
          var y = mutable.TreeSet[Int]()
          val newN = ItemsMapTrans()
          for (j <- i+1 until items.size){
            var xj = items(j)
            breakable{
              if (skipset.contains(xj)){
                break
              }
              else{
                // print(xi.isets)
                // print(xj.isets)
                y = nodes.imt.getOrElse(xi, new mutable.TreeSet[Int]())
                // print(nodes.getOrElse(xj, new mutable.TreeSet[Int]()))
                y = y.intersect(nodes.imt.getOrElse(xj, new mutable.TreeSet[Int]()))
                // print(y)
                x = x ++= (xj, y.size)
                xi = charmProp(xi, xj, y, minsup, nodes, newN)                    // Checking the Charm Properties
                x_prev = xi                                                       // Compensating to match Java Functionality
              }
            }
          }
          //println(newN+"\n")
          if (!newN.imt.isEmpty)
          {
            // println("Condition 1 is Satisfied\n")
            charmExtended(newN, c, minsup)
          }
          if (!(x_prev.isets.isEmpty) && !(nodes.imt.get(x_prev).isEmpty) && !(isSubsumed(c, nodes.imt.getOrElse(x_prev, new mutable.TreeSet[Int]()))))
          {
            // println("Condition 2 is Satisfied\n")
            // println(x_prev.isets)
            c.imt+=(x_prev -> nodes.imt.getOrElse(x_prev, new mutable.TreeSet[Int]()))
          }
          if (!(x.isets.isEmpty) && !(nodes.imt.get(x).isEmpty) && !(isSubsumed(c, nodes.imt.getOrElse(x, new mutable.TreeSet[Int]()))))
          {
            // println("Condition 3 is Satisfied\n")
            c.imt+=(x -> nodes.imt.getOrElse(x, new mutable.TreeSet[Int]()))
          } 
        }
      }
    }
  }
  
  def charmProp(xi:ItemSet, xj:ItemSet, y:mutable.TreeSet[Int], minsup:Int, nodes:ItemsMapTrans,
                newN:ItemsMapTrans):ItemSet={
    if (y.size>=minsup){                                                  // Checking Minimum Support Condition
      val temp = xi ++= (xj,y.size)                                                // Union of two Itemsets
      val yi = nodes.imt.getOrElse(xi, new mutable.TreeSet[Int]())        // transactions of Itemset "xi"
      val yj = nodes.imt.getOrElse(xj, new mutable.TreeSet[Int]())        // transactions of Itemset "xj"
      
      if (yi == yj)                                                       // Checking Property 1
      {
        // print(xi.isets)
        // print(xj.isets)
        skipset+=xj
        replaceInItems(xi, temp, newN)
        replaceInItems(xi, temp, nodes)
        temp
      }
      else if (yi.subsetOf(yj))                                       // Checking Property 2
      {
        replaceInItems(xi, temp, newN)
        replaceInItems(xi, temp, nodes)
        temp
      }
      else if (yj.subsetOf(yi))                                       // Checking Property 3
      {
        // print(xi.isets)
        // print(xj.isets)
        skipset+=xj
        newN.imt+=(temp -> y)
        xi
      }
      else
      {
        if (!(yi==yj))                                                // Checking Property 4
        {
          newN.imt+=(temp -> y)
        }
        xi
      }
    }
    else{
      xi
    }
  }
  
  def replaceInItems(curr: ItemSet, target:ItemSet, xmap:ItemsMapTrans):Unit={
    
    xmap.imt.filterKeys(_.containsAll(curr)) map (x => f(x))    // filter all that conatins the curr itemset as key  
                                                                // and apply map f function on that (k,v) pair in xmap
    def f(x:(ItemSet, mutable.TreeSet[Int]))={
      val (k,v) = x                                             // obtain (k,v) pair from xmap whose key matches with curr 
      xmap.imt-=k                                               // remove the key "k" from xmap 
      val k_new = k++=(target, v.size)                         // obtain new "k_new" by adding target itemset to "k"
      xmap.imt+=(k_new->v)                                      // add new "k_new"  to the xmap  
    }
  }
  
  def isSubsumed(c: ItemsMapTrans, y:mutable.TreeSet[Int]): Boolean ={
    /* This is version_1 implementation
    var subsumed = c.mapValues(_==y).values
    if (subsumed exists (_==true))
    {
      true
    }
    else
    {
      false
    }
    */
    
    /* This is version_2 implementation */
    
    val c_valit = c.imt.valuesIterator        // c_valit is a values iterator for treemap
    var flag = false                          // flag to indicate that a value of treemap
                                              // equal to "y" is already present
    while(!(flag) && c_valit.hasNext){        // Conditions to iterate to next value 
      if (c_valit.next()==y){
        flag = true                           // When the treemap_val == y change the flag
      }                                       // to true
    }
    flag                                      // Return the flag
  }
}

/* The Charm object to make the charm() function in the  
 * Charm class look like a factory-made method */
object Charm{
  
  def apply(data:ItemsMapTrans, minsup: Int):ItemsMapTrans={
    
    val charm_x = new Charm()
    
    val c = charm_x.charm(data, minsup)
    /*
    if (charm_x.skipset.iterator.hasNext){
      println(charm_x.skipset.iterator.next().isets)
    }
    * 
    */
    c
  }
}
 

