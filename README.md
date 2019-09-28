# Charm_Scala

This repository contains a Maven Project for CHARM algorithm, to mine the closed itemsets, implementated in Scala. Furthermore, it is an updated version of implementation done by https://github.com/SatishUC15/CHARM-Algorithm (Courtesy: SatishUC15).

The following are the updates introduced to the SatishUC15 implementation:

1. The support is added as a ItemSet class member to sort the itemsets by increasing order of support. Thus, maximizing the occurences of Property 1 and Property 2 of CHARM algorithm eventually reducing the depth of the tree.

2. A bitvector implementation is also provided so as to reduce the memory consumed. 
    Also, see how bitvector implementation done here: 
 [Bitvector Implementation of CHARM properties](#Bitvector Implementation of CHARM properties)
    
### Bitvector Implementation of CHARM properties






