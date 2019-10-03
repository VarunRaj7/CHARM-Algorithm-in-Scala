# Charm_Scala

This repository contains a two Maven Project implementing CHARM algorithm, to mine the closed itemsets, in Scala. Furthermore, it is an updated version of implementation done by [SatishUC15](https://github.com/SatishUC15/CHARM-Algorithm).

The following are the updates in implementation:

1. The support is added as a ItemSet class member to sort the itemsets by increasing order of support. Thus, maximizing the occurences of Property 1 and Property 2 of CHARM algorithm that eventually reduced the depth of the tree.

2. A bitvector implementation is also employed so as to reduce the memory consumed. 
    
    Also, see how bitvector implementation done: [Bitvector Implementation of CHARM properties](#bitvector-implementation-of-charm-properties)
    
#### Source:

CHARM: An Efficient Algorithm for Closed Association Rule Mining, by Mohammed J. Zaki and Ching-Jui Hsiao, Computer Science Department, Rensselaer Polytechnic Institute, Troy NY 12180

[CHARM algorithm](https://pdfs.semanticscholar.org/9f80/dbdd6e613d98dead0cc9e6c88fe04d70f330.pdf).

[SatishUC15](https://github.com/SatishUC15/CHARM-Algorithm)

### Classes

The ItemSet,  class structure is shown as below:

**_ItemSet_**

|----isets: immutable.TreeSet[String]   ## Items of ItemSet class are stored as TreeSet of String

|----sup: Int = 0   ## Support member of ItemSet class

|----(+=):ItemSet

    Input: String<ItemName>, sup<Support of the ItemSet>
    Output: ItemSet
    This is an operation to adds an ItemSet with an Item given its support and returns the new merged ItemSet.
    Example: val Apple = new ItemSet()+=("Apple", 5) // Apple appeared in 5 transactions
             val Banana = new ItemSet()+=("Banana", 3) // Banana appeared in 3 transactions

|----(++=):ItemSet
        
    Input: ItemSet<ItemSet2Name>, sup<Combined Support>
    Output: ItemSet
    This an operation that joins two ItemSets given the combined support.
    Example: val Apple_Banana = Apple++=(Banana, 2)
             Here, both apple and bananas are present in 2 transactions only 

|----compare(that:ItemSet):Int
        
    Input: ItemSet
    Output: Int
    This operation compares two ItemSet by Support first, if supports are similar then it compares lexically and returns an integer 
    if this<that -1 else +1.
    Example: Apple.compare(Banana)
             returns 1

|----containsAll(that:ItemSet): Boolean
        
    Input: ItemSet
    Output: Boolean
    This operation returns true of all the items in "that" ItemSet is present in "this"(self) ItemSet.
    Example: Apple_Banana.compare(Apple)
             returns True                                                                                                          


**_Case class ItemsMapTrans_**

|----imt: immutable.TreeMap[ItemSet, mutable.TreeSet[Int]]


**_Charm_** 

|----skipset:mutable.TreeSet[ItemSet]

|----charm(data:ItemsMapTrans, minsup:Int):ItemsMapTrans

    Inputs: ItemsMapTrans<data>, Int<minimum_support>
    Outputs: ItemsMapTrans<closed_ItemSets>
    This function will call charmExtended function with the Items that have minimum support > minsup. 
    The resulting data is called nodes which is of type case class ItemMapTrans.
        
|----charmExtended(nodes:ItemsMapTrans, c:ItemsMapTrans, minsup:Int):Unit

    Inputs: ItemsMapTrans<nodes of the data>, ItemsMapTrans<for closed itemsets> & Int<minimum_support>
    Outputs: Unit
    This function will perform CHARM-EXTENDED function as described in the paper.
    
|----charmProp(xi:ItemSet, xj:ItemSet, y:mutable.TreeSet[Int], minsup:Int, nodes:ItemsMapTrans, newN:ItemsMapTrans):ItemSet

    Inputs: xi<ItemSet1>, xj<ItemSet2>, y<Transactions of xi U xj>, minsup<Int>, nodes<the entire data stored as ItemsMapTrans>, 
    newN<New nodes in the visualization of CHARM algorithm workflow tree>
    Outputs: ItemSet
    This function will perform the same function as CHARM-PROPERTY as described in the paper, that is checking of the four properties
    and replacing or addition of new nodes.
   
|----replaceInItems(curr:ItemSet, target:ItemSet, xmap:ItemsMapTrans):Unit 
    
    Inputs: curr<ItemSet1>, target<ItemSet2>, xmamp<ItemsMapTrans>
    Outputs: Unit
    This function will replace the <curr> ItemSet with the <target> ItemSet in the xmap nodes.
   
|----isSubsumed(c:ItemsMapTrans, y:mutable.TreeSet[Int]):Boolean

    Inputs: c<closed Itemsets of form ItemsMapTrans upto this instance>, y<Transactions set>
    Ouput: Boolean
    This function will return true if closed itemset upto the instance this function is called has any transaction sets listed in y else
    False.
    
**_Object Charm_**

This object has only one function apply, so as to provide the charm function as a factory made function.

|----apply(data:ItemsMapTrans, minsup:Int):ItemsMapTrans

    Inputs: data<ItemsMapTrans>, minimum_support<Int>
    Outputs: closed_ItemSets<ItemsMapTrans>
    This function will create a Charm class object and calls charm function on this object with the inputs passed.
    
### Projects:

This respository contains two maven projects titled fcv3 and fcv4 which are normal and bitvector implementations of CHARM in scala. It will also contain a test sample scala code on how to import and use the charm module with the classes discussed above. This file is provided with the title : **"fc_test.scala"**.

Example file is already stored in the data folder as testInp_v2.txt. The first few lines are show below:

    218	1,2,3,12,14,22,23,29,41,46
    219	1,2,3,14,22,23,29,41
    220	1,2,3,14,22,23,29,41
    200	3,15,19
    ..
   
Where each line consists of an item with name which has digit "2" at the begining and followed by the transactions list tab-separated.

The results of minsup of 3 are:
    
    15	200 201 202 203 204 205 206 211
    23 29	215 216 217 218 219 220
    3 15 19	200 202 204 206
    14 15 31	201 202 203
    14 15 29	201 206
    ...

This output list consists of closed Itemsets (each item is space-separated) and corresponding transactions (each separated by space) map to, separated by a tab.

### How Support member facilitated ItemSet class?

As the ItemSet class extends Ordered[T] class, we wrote compare method using support to define the ordering. Hence, this helped in case
class ItemsMapTrans where the same ordering of treemap of ItemSet is used, by default it is increasing order.  

### Bitvector Implementation of CHARM properties:

In this case the case class ItemMaps trans is changed to immutable.TreeMap[ItemSet, mutable.TreeSet[BitVector]] as opposed to
immutable.TreeMap[ItemSet, mutable.TreeSet[Int]]. And some functionalities of the Charm are also change which will be discussed below:

1. Every occurence of mutable.TreeSet[Int] is changed to mutable.TreeSet[BitVector] and corresponding functionality is modified, such as
to know the support of any itemset, the corresponding transaction size is seen earlier but in this case the population function on
bitvector is used to return the number of 1's in it.  

2. The four properties of CHARM are transformed into working with bits as follows:

Property 1:
t(X<sub>i</sub>) = t(X<sub>j</sub>)

Implementation with bitvector:
bitvector1 == bitvector2

Property 2:
t(X<sub>i</sub>) is subset of t(X<sub>j</sub>)

Implementation with bitvector:
((bitvector1 & bitvector2)== bitvector1)

Example:
case 1:
bitvector1 = 1001
bitvector2 = 0011
(bitvector1 & bitvector2)= 0001 not equal to bitvector1

case 2:
bitvector1 = 0001
bitvector2 = 1001
(bitvector1 & bitvector2)= 0001 equal to bitvector1

Property 3:
t(X<sub>j</sub>) is subset of t(X<sub>i</sub>)

Implementation with bitvector:
((bitvector1 & bitvector2)== bitvector2)

Example:
case 1:
bitvector1 = 0001
bitvector2 = 0011
(bitvector1 & bitvector2)= 0001 not equal to bitvector2

case 2:
bitvector1 = 1011
bitvector2 = 0011
(bitvector1 & bitvector2)= 0011 equal to bitvector2

Property 4:
t(X<sub>j</sub>) is not equal t(X<sub>i</sub>)

Implementation with bitvector:
!(bitvector1 == bitvector2)
