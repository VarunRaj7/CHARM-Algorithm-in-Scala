# Charm_Scala

This repository contains a Maven Project for CHARM algorithm, to mine the closed itemsets, implementated in Scala. Furthermore, it is an updated version of implementation done by https://github.com/SatishUC15/CHARM-Algorithm (Courtesy: SatishUC15).

The following are the updates introduced to the SatishUC15 implementation:

1. The support is added as a ItemSet class as a member to sort the itemsets by increasing order of support. Thus, maximizing the occurences of Property 1 and Property 2 of CHARM algorithm that eventually reduced the depth of the tree.

2. A bitvector implementation is also provided so as to reduce the memory consumed. 
    
    Also, see how bitvector implementation done: [Bitvector Implementation of CHARM properties](#bitvector-implementation-of-charm-properties)

### Classes

The ItemSet,  class structure is shown as below:

ItemSet

|----isets: immutable.TreeSet[String]

|----sup: Int = 0

|----(+=):ItemSet

    Input: String<ItemName>, sup<Support of the ItemSet>
    Output: ItemSet
    This is an operation to adds an ItemSet with an Item given its support and returns the new merged ItemSet.
    Example: val Apple = new ItemSet()+=("Apple", 5)
             val Banana = new ItemSet()+=("Banana", 3)

|----(++=):ItemSet
        
    Input: ItemSet<ItemSet2Name>, sup<Combined Support>
    Output: ItemSet
    This an operation that joins two ItemSets given the combined support.
    Example: val Apple_Banana = Apple++=(Banana, 2)
             Here, both apple and bananas are present in 2 transactions only 

|----compare(that:ItemSet):Int
        
    Input: ItemSet
    Output: Int
    This operation compares two ItemSet by Support first, if supports are similar then it compares lexically and returns an integer if this<that -1 else +1.
    Example: Apple.compare(Banana)
             returns 1

|----containsAll(that:ItemSet): Boolean
        
    Input: ItemSet
    Output: Boolean
    This operation returns true of all the items in "that" ItemSet is present in "this"(self) ItemSet.
    Example: Apple_Banana.compare(Apple)
             returns True                                                                                                          


case class ItemsMapTrans

|----imt: immutable.TreeMap[ItemSet, mutable.TreeSet[Int]]


Charm 

|----skipset:mutable.TreeSet[ItemSet]

|----charm(data:ItemsMapTrans, minsup:Int):ItemsMapTrans

    Inputs: ItemsMapTrans<data>, Int<minimum_support>
    Outputs: ItemsMapTrans<closed_ItemSets>
    This function will call charmExtended function with the Items that have minimum support > minsup. The resulting data is called nodes which is of type case class ItemMapTrans.
        
|----charmExtended(nodes:ItemsMapTrans, c:ItemsMapTrans, minsup:Int)

    Inputs: ItemsMapTrans<nodes of the data>, ItemsMapTrans<for closed itemsets> & Int<minimum_support>
    Outputs: Unit
    This function will perform CHARM-EXTENDED function as described in paper(CHARM algorithm)[https://pdfs.semanticscholar.org/9f80/dbdd6e613d98dead0cc9e6c88fe04d70f330.pdf].
    
|----charmProp(xi:ItemSet, xj:ItemSet, y:mutable.TreeSet[Int], minsup:Int, nodes:ItemsMapTrans, newN:ItemsMapTrans)

    Inputs: xi<ItemSet1>, xj<ItemSet2>, y<support of xi U xj>, minsup<Int>, nodes<the entire data stored as ItemsMapTrans>, newN<New nodes in the visualization of CHARM algorithm workflow tree>
    Outputs: ItemSet
    This function will perform the same function as CHARM-PROPERTY as descibed in paper(CHARM algorithm)[https://pdfs.semanticscholar.org/9f80/dbdd6e613d98dead0cc9e6c88fe04d70f330.pdf].


### How Support member facilitated ItemSet class?
                                                                                                                                                     
### Bitvector Implementation of CHARM properties:







